package com.atm.dao;

import com.atm.enums.AlertType;
import com.atm.model.AtmAlert;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

/** atm_alerts. الـ atm_id (association "raises") والـ resolved_by (association "resolvedBy") بره الـ class. */
public class AtmAlertDao extends BaseDao {

    private static final String SELECT =
            "SELECT alert_id, type, message, created_at, resolved_at FROM atm_alerts ";

    public void save(AtmAlert alert, long atmId) {
        execute("INSERT INTO atm_alerts (alert_id, atm_id, type, message, created_at, resolved_at) " +
                        "VALUES (?, ?, ?, ?, ?, ?)",
                alert.getAlertId(), atmId, alert.getType(), alert.getMessage(), alert.getCreatedAt(),
                alert.getResolvedAt());
    }

    public List<AtmAlert> findByAtmId(long atmId) {
        return queryList(SELECT + "WHERE atm_id = ? ORDER BY created_at DESC", this::map, atmId);
    }

    public List<AtmAlert> findUnresolved() {
        return queryList(SELECT + "WHERE resolved_at IS NULL ORDER BY created_at DESC", this::map);
    }

    public List<AtmAlert> findUnresolvedByAtmId(long atmId) {
        return queryList(SELECT + "WHERE atm_id = ? AND resolved_at IS NULL ORDER BY created_at DESC",
                this::map, atmId);
    }

    public boolean existsUnresolved(long atmId, AlertType type) {
        return exists("SELECT 1 FROM atm_alerts WHERE atm_id = ? AND type = ? AND resolved_at IS NULL",
                atmId, type);
    }

    public void markResolved(String alertId, String adminId, LocalDateTime at) {
        int rows = execute("UPDATE atm_alerts SET resolved_at = ?, resolved_by = ? " +
                "WHERE alert_id = ? AND resolved_at IS NULL", at, adminId, alertId);
        if (rows == 0) {
            throw new IllegalStateException("Alert not found or already resolved: " + alertId);
        }
    }

    private AtmAlert map(ResultSet rs) throws SQLException {
        AtmAlert a = new AtmAlert();
        a.setAlertId(rs.getString("alert_id"));
        a.setType(AlertType.valueOf(rs.getString("type")));
        a.setMessage(rs.getString("message"));
        a.setCreatedAt(ts(rs, "created_at"));
        a.setResolvedAt(ts(rs, "resolved_at"));
        return a;
    }
}
