package com.atm.dao;

import com.atm.model.AuthMethod;
import com.atm.model.Session;
import com.atm.model.SessionStatus;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/** sessions. الـ atm / customer / card (associations) بيتبعتوا كـ parameters. */
public class SessionDao extends BaseDao {

    private static final String SELECT =
            "SELECT session_id, started_at, last_activity, timeout_seconds, auth_method, status FROM sessions ";

    public void save(Session s, long atmId, String customerId, String cardNumber) {
        execute("INSERT INTO sessions (session_id, atm_id, user_id, card_number, started_at, last_activity, " +
                        "timeout_seconds, auth_method, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
                s.getSessionId(), atmId, customerId, cardNumber, s.getStartedAt(), s.getLastActivity(),
                s.getTimeoutSeconds(), s.getAuthMethod(), s.getStatus());
    }

    public void update(Session s) {
        int rows = execute("UPDATE sessions SET last_activity = ?, status = ?, timeout_seconds = ? " +
                        "WHERE session_id = ?",
                s.getLastActivity(), s.getStatus(), s.getTimeoutSeconds(), s.getSessionId());
        if (rows == 0) {
            throw new DataAccessException("Session not found: " + s.getSessionId(), null);
        }
    }

    public Optional<Session> findById(String sessionId) {
        return queryOne(SELECT + "WHERE session_id = ?", this::map, sessionId);
    }

    /** صاحب الجلسة (association "customer"). */
    public Optional<String> findUserId(String sessionId) {
        return queryOne("SELECT user_id FROM sessions WHERE session_id = ?", rs -> rs.getString(1), sessionId);
    }

    public List<Session> findByUserId(String userId) {
        return queryList(SELECT + "WHERE user_id = ? ORDER BY started_at DESC", this::map, userId);
    }

    public List<Session> findActiveByAtmId(long atmId) {
        return queryList(SELECT + "WHERE atm_id = ? AND status = 'ACTIVE' ORDER BY started_at DESC",
                this::map, atmId);
    }

    private Session map(ResultSet rs) throws SQLException {
        Session s = new Session();
        s.setSessionId(rs.getString("session_id"));
        s.setStartedAt(ts(rs, "started_at"));
        s.setLastActivity(ts(rs, "last_activity"));
        s.setTimeoutSeconds(rs.getInt("timeout_seconds"));
        s.setAuthMethod(AuthMethod.valueOf(rs.getString("auth_method")));
        s.setStatus(SessionStatus.valueOf(rs.getString("status")));
        return s;
    }
}
