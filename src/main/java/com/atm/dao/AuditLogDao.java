package com.atm.dao;

import com.atm.model.AuditAction;
import com.atm.model.AuditLog;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/** audit_logs. الـ actor (association مع User) بيتبعت كـ parameter (null = النظام نفسه). */
public class AuditLogDao extends BaseDao {

    private static final String SELECT =
            "SELECT log_id, action, details, atm_id, target_id, created_at FROM audit_logs ";

    /** بتحفظ وبتملا logId اللي اتولّد. */
    public void save(AuditLog log, String actorId) {
        long id = insertReturningKey(
                "INSERT INTO audit_logs (actor_id, action, details, atm_id, target_id, created_at) " +
                        "VALUES (?, ?, ?, ?, ?, ?)",
                actorId, log.getAction(), log.getDetails(), log.getAtmId(), log.getTargetId(), log.getCreatedAt());
        log.setLogId(id);
    }

    public List<AuditLog> findByActor(String actorId) {
        return queryList(SELECT + "WHERE actor_id = ? ORDER BY created_at DESC, log_id DESC", this::map, actorId);
    }

    public List<AuditLog> findByAtmId(long atmId) {
        return queryList(SELECT + "WHERE atm_id = ? ORDER BY created_at DESC, log_id DESC", this::map, atmId);
    }

    public List<AuditLog> findByAction(AuditAction action) {
        return queryList(SELECT + "WHERE action = ? ORDER BY created_at DESC, log_id DESC", this::map, action);
    }

    public List<AuditLog> findRecent(int limit) {
        return queryList(SELECT + "ORDER BY created_at DESC, log_id DESC LIMIT ?", this::map, limit);
    }

    private AuditLog map(ResultSet rs) throws SQLException {
        AuditLog l = new AuditLog();
        l.setLogId(rs.getLong("log_id"));
        l.setAction(AuditAction.valueOf(rs.getString("action")));
        l.setDetails(rs.getString("details"));
        long atm = rs.getLong("atm_id");
        l.setAtmId(rs.wasNull() ? null : atm);
        l.setTargetId(rs.getString("target_id"));
        l.setCreatedAt(ts(rs, "created_at"));
        return l;
    }
}
