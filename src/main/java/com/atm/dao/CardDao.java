package com.atm.dao;

import com.atm.model.Card;
import com.atm.enums.CardStatus;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/** cards. الـ account_number (association "issued") بيتبعت كـ parameter. */
public class CardDao extends BaseDao {

    private static final String SELECT =
            "SELECT c.card_number, c.pin_hash, c.expiry_date, c.status, c.failed_pin_attempts, c.issued_at " +
            "FROM cards c ";

    public void save(Card card, String accountNumber) {
        execute("INSERT INTO cards (card_number, account_number, pin_hash, expiry_date, status, " +
                        "failed_pin_attempts, issued_at) VALUES (?, ?, ?, ?, ?, ?, ?)",
                card.getCardNumber(), accountNumber, card.getPinHash(), card.getExpiryDate(), card.getStatus(),
                card.getFailedPinAttempts(), card.getIssuedAt());
    }

    public void update(Card card) {
        int rows = execute("UPDATE cards SET pin_hash = ?, expiry_date = ?, status = ?, failed_pin_attempts = ? " +
                        "WHERE card_number = ?",
                card.getPinHash(), card.getExpiryDate(), card.getStatus(), card.getFailedPinAttempts(),
                card.getCardNumber());
        if (rows == 0) {
            throw new DataAccessException("Card not found: " + card.getCardNumber(), null);
        }
    }

    public Optional<Card> findByCardNumber(String cardNumber) {
        return queryOne(SELECT + "WHERE c.card_number = ?", this::map, cardNumber);
    }

    public List<Card> findByAccountNumber(String accountNumber) {
        return queryList(SELECT + "WHERE c.account_number = ? ORDER BY c.issued_at", this::map, accountNumber);
    }

    public List<Card> findByCustomerId(String customerId) {
        return queryList(SELECT + "JOIN accounts a ON a.account_number = c.account_number " +
                "WHERE a.customer_id = ? ORDER BY c.issued_at", this::map, customerId);
    }

    public Optional<Card> findByNumberAndCustomerId(String cardNumber, String customerId) {
        return queryOne(SELECT + "JOIN accounts a ON a.account_number = c.account_number " +
                "WHERE c.card_number = ? AND a.customer_id = ?", this::map, cardNumber, customerId);
    }

    /** صاحب الكارت (customer_id) عن طريق الحساب. */
    public Optional<String> findOwnerId(String cardNumber) {
        return queryOne("SELECT a.customer_id FROM cards c JOIN accounts a ON a.account_number = c.account_number " +
                "WHERE c.card_number = ?", rs -> rs.getString(1), cardNumber);
    }

    public Optional<String> findAccountNumber(String cardNumber) {
        return queryOne("SELECT account_number FROM cards WHERE card_number = ?", rs -> rs.getString(1), cardNumber);
    }

    public boolean existsByCardNumber(String cardNumber) {
        return exists("SELECT 1 FROM cards WHERE card_number = ?", cardNumber);
    }

    private Card map(ResultSet rs) throws SQLException {
        Card c = new Card();
        c.setCardNumber(rs.getString("card_number"));
        c.setPinHash(rs.getString("pin_hash"));
        c.setExpiryDate(date(rs, "expiry_date"));
        c.setStatus(CardStatus.valueOf(rs.getString("status")));
        c.setFailedPinAttempts(rs.getInt("failed_pin_attempts"));
        c.setIssuedAt(ts(rs, "issued_at"));
        return c;
    }
}
