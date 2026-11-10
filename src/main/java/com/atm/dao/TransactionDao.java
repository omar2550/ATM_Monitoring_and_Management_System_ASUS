package com.atm.dao;

import com.atm.model.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * transactions + (transfer_details / deposit_details / pin_change_details).
 * الـ associations (session / atm / account / destination / card) بتتحفظ من TransactionContext
 * وبتتجاب بـ findContext(...).
 */
public class TransactionDao extends BaseDao {

    private static final String SELECT =
            "SELECT t.transaction_id, t.tx_type, t.amount, t.status, t.created_at, t.failure_reason, " +
            "dd.deposit_type, dd.cheque_number " +
            "FROM transactions t LEFT JOIN deposit_details dd ON dd.transaction_id = t.transaction_id ";

    // ---------------------------------------------------------------
    // CREATE
    // ---------------------------------------------------------------
    public void save(Transaction tx, TransactionContext ctx) {
        if (ctx == null) {
            throw new IllegalArgumentException("TransactionContext is required");
        }
        String type = typeOf(tx);
        if (tx instanceof TransferTransaction && ctx.destinationAccount() == null) {
            throw new IllegalArgumentException("Transfer needs a destination account");
        }
        if (tx instanceof PinChangeTransaction && ctx.cardNumber() == null) {
            throw new IllegalArgumentException("PIN change needs a card number");
        }

        Tx.run(() -> {
            execute("INSERT INTO transactions (transaction_id, tx_type, session_id, atm_id, account_number, " +
                            "amount, status, created_at, failure_reason) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
                    tx.getTransactionId(), type, ctx.sessionId(), ctx.atmId(), ctx.accountNumber(),
                    tx instanceof FinancialTransaction f ? f.getAmount() : null,
                    tx.getStatus(), tx.getCreatedAt(), tx.getFailureReason());

            if (tx instanceof TransferTransaction) {
                execute("INSERT INTO transfer_details (transaction_id, destination_account) VALUES (?, ?)",
                        tx.getTransactionId(), ctx.destinationAccount());
            } else if (tx instanceof DepositTransaction d) {
                execute("INSERT INTO deposit_details (transaction_id, deposit_type, cheque_number) VALUES (?, ?, ?)",
                        tx.getTransactionId(), d.getDepositType(), d.getChequeNumber());
            } else if (tx instanceof PinChangeTransaction) {
                execute("INSERT INTO pin_change_details (transaction_id, card_number) VALUES (?, ?)",
                        tx.getTransactionId(), ctx.cardNumber());
            }
        });
    }

    // ---------------------------------------------------------------
    // UPDATE
    // ---------------------------------------------------------------
    public void updateStatus(String transactionId, TransactionStatus status, String failureReason) {
        int rows = execute("UPDATE transactions SET status = ?, failure_reason = ? WHERE transaction_id = ?",
                status, failureReason, transactionId);
        if (rows == 0) {
            throw new DataAccessException("Transaction not found: " + transactionId, null);
        }
    }

    // ---------------------------------------------------------------
    // READ
    // ---------------------------------------------------------------
    public Optional<Transaction> findById(String transactionId) {
        return queryOne(SELECT + "WHERE t.transaction_id = ?", this::map, transactionId);
    }

    /** Atm 1 --executes--> 0..* Transaction */
    public List<Transaction> findByAtmId(long atmId) {
        return queryList(SELECT + "WHERE t.atm_id = ? ORDER BY t.created_at DESC", this::map, atmId);
    }

    /** Account 1 --ledger--> 0..* Transaction */
    public List<Transaction> findByAccountNumber(String accountNumber) {
        return queryList(SELECT + "WHERE t.account_number = ? ORDER BY t.created_at DESC", this::map, accountNumber);
    }

    /** Session 1 --performs--> 0..* Transaction */
    public List<Transaction> findBySessionId(String sessionId) {
        return queryList(SELECT + "WHERE t.session_id = ? ORDER BY t.created_at", this::map, sessionId);
    }

    public List<Transaction> findBetween(LocalDateTime from, LocalDateTime to) {
        return queryList(SELECT + "WHERE t.created_at BETWEEN ? AND ? ORDER BY t.created_at", this::map, from, to);
    }

    /** عمليات الـ ATMs اللي الـ admin مسؤول عنها في فترة معينة. */
    public List<Transaction> findByAdminAndPeriod(String adminId, LocalDateTime from, LocalDateTime to) {
        return queryList(SELECT + "JOIN admin_atms m ON m.atm_id = t.atm_id " +
                        "WHERE m.admin_id = ? AND t.created_at BETWEEN ? AND ? ORDER BY t.created_at",
                this::map, adminId, from, to);
    }

    public Optional<TransactionContext> findContext(String transactionId) {
        return queryOne("SELECT t.session_id, t.atm_id, t.account_number, td.destination_account, pc.card_number " +
                        "FROM transactions t " +
                        "LEFT JOIN transfer_details td ON td.transaction_id = t.transaction_id " +
                        "LEFT JOIN pin_change_details pc ON pc.transaction_id = t.transaction_id " +
                        "WHERE t.transaction_id = ?",
                rs -> new TransactionContext(rs.getString("session_id"), rs.getLong("atm_id"),
                        rs.getString("account_number"), rs.getString("destination_account"),
                        rs.getString("card_number")),
                transactionId);
    }

    // ---------------------------------------------------------------

    private static String typeOf(Transaction tx) {
        if (tx instanceof WithdrawalTransaction) return "WITHDRAWAL";
        if (tx instanceof DepositTransaction) return "DEPOSIT";
        if (tx instanceof TransferTransaction) return "TRANSFER";
        if (tx instanceof BalanceInquiryTransaction) return "BALANCE_INQUIRY";
        if (tx instanceof PinChangeTransaction) return "PIN_CHANGE";
        throw new IllegalArgumentException("Unknown transaction type: " + tx.getClass().getName());
    }

    private Transaction map(ResultSet rs) throws SQLException {
        String type = rs.getString("tx_type");
        Transaction t = switch (type) {
            case "WITHDRAWAL" -> new WithdrawalTransaction();
            case "TRANSFER" -> new TransferTransaction();
            case "BALANCE_INQUIRY" -> new BalanceInquiryTransaction();
            case "PIN_CHANGE" -> new PinChangeTransaction();
            case "DEPOSIT" -> {
                DepositTransaction d = new DepositTransaction();
                d.setDepositType(DepositType.valueOf(rs.getString("deposit_type")));
                d.setChequeNumber(rs.getString("cheque_number"));
                yield d;
            }
            default -> throw new SQLException("Unknown tx_type: " + type);
        };
        t.setTransactionId(rs.getString("transaction_id"));
        t.setStatus(TransactionStatus.valueOf(rs.getString("status")));
        t.setCreatedAt(ts(rs, "created_at"));
        t.setFailureReason(rs.getString("failure_reason"));
        if (t instanceof FinancialTransaction f) {
            f.setAmount(rs.getBigDecimal("amount"));
        }
        return t;
    }
}
