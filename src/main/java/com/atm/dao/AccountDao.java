package com.atm.dao;

import com.atm.model.Account;
import com.atm.model.AccountStatus;
import com.atm.model.AccountType;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/** accounts. الـ customer_id (association "owns") مش attribute في Account، فبيتبعت كـ parameter. */
public class AccountDao extends BaseDao {

    private static final String SELECT =
            "SELECT account_number, balance, status, type, opened_at, daily_limit, withdrawn_today, " +
            "last_reset_date FROM accounts ";

    public void save(Account a, String customerId) {
        execute("INSERT INTO accounts (account_number, customer_id, balance, status, type, opened_at, " +
                        "daily_limit, withdrawn_today, last_reset_date) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
                a.getAccountNumber(), customerId, a.getBalance(), a.getStatus(), a.getType(), a.getOpenedAt(),
                a.getDailyLimit(), a.getWithdrawnToday(), a.getLastResetDate());
    }

    /** بتحدّث الحقول المتغيرة بس (الرصيد، الحالة، الحدود). */
    public void update(Account a) {
        int rows = execute("UPDATE accounts SET balance = ?, status = ?, daily_limit = ?, " +
                        "withdrawn_today = ?, last_reset_date = ? WHERE account_number = ?",
                a.getBalance(), a.getStatus(), a.getDailyLimit(), a.getWithdrawnToday(), a.getLastResetDate(),
                a.getAccountNumber());
        if (rows == 0) {
            throw new DataAccessException("Account not found: " + a.getAccountNumber(), null);
        }
    }

    public Optional<Account> findByAccountNumber(String accountNumber) {
        return queryOne(SELECT + "WHERE account_number = ?", this::map, accountNumber);
    }

    public List<Account> findByCustomerId(String customerId) {
        return queryList(SELECT + "WHERE customer_id = ? ORDER BY opened_at", this::map, customerId);
    }

    public Optional<Account> findByNumberAndCustomerId(String accountNumber, String customerId) {
        return queryOne(SELECT + "WHERE account_number = ? AND customer_id = ?", this::map,
                accountNumber, customerId);
    }

    public boolean existsByAccountNumber(String accountNumber) {
        return exists("SELECT 1 FROM accounts WHERE account_number = ?", accountNumber);
    }

    private Account map(ResultSet rs) throws SQLException {
        Account a = new Account();
        a.setAccountNumber(rs.getString("account_number"));
        a.setBalance(rs.getBigDecimal("balance"));
        a.setStatus(AccountStatus.valueOf(rs.getString("status")));
        a.setType(AccountType.valueOf(rs.getString("type")));
        a.setOpenedAt(ts(rs, "opened_at"));
        a.setDailyLimit(rs.getBigDecimal("daily_limit"));
        a.setWithdrawnToday(rs.getBigDecimal("withdrawn_today"));
        a.setLastResetDate(date(rs, "last_reset_date"));
        return a;
    }
}
