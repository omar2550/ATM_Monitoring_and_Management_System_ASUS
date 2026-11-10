package com.atm.dao;

import com.atm.model.Atm;
import com.atm.enums.AtmStatus;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/** atms + admin_atms (association "manages"). */
public class AtmDao extends BaseDao {

    private static final String SELECT =
            "SELECT a.atm_id, a.location, a.status, a.max_withdraw_per_transaction, " +
            "a.max_deposit_per_transaction, a.note_multiple, a.last_heartbeat_at FROM atms a ";

    /** بتحفظ الـ ATM وبتملا atmId اللي اتولّد. */
    public void save(Atm atm) {
        long id = insertReturningKey(
                "INSERT INTO atms (location, status, max_withdraw_per_transaction, max_deposit_per_transaction, " +
                        "note_multiple, last_heartbeat_at) VALUES (?, ?, ?, ?, ?, ?)",
                atm.getLocation(), atm.getStatus(), atm.getMaxWithdrawPerTransaction(),
                atm.getMaxDepositPerTransaction(), atm.getNoteMultiple(), atm.getLastHeartbeatAt());
        atm.setAtmId(id);
    }

    public void update(Atm atm) {
        int rows = execute("UPDATE atms SET location = ?, status = ?, max_withdraw_per_transaction = ?, " +
                        "max_deposit_per_transaction = ?, note_multiple = ?, last_heartbeat_at = ? WHERE atm_id = ?",
                atm.getLocation(), atm.getStatus(), atm.getMaxWithdrawPerTransaction(),
                atm.getMaxDepositPerTransaction(), atm.getNoteMultiple(), atm.getLastHeartbeatAt(), atm.getAtmId());
        if (rows == 0) {
            throw new DataAccessException("ATM not found: " + atm.getAtmId(), null);
        }
    }

    public void updateStatus(long atmId, AtmStatus status) {
        if (execute("UPDATE atms SET status = ? WHERE atm_id = ?", status, atmId) == 0) {
            throw new DataAccessException("ATM not found: " + atmId, null);
        }
    }

    public void updateHeartbeat(long atmId, LocalDateTime at) {
        if (execute("UPDATE atms SET last_heartbeat_at = ? WHERE atm_id = ?", at, atmId) == 0) {
            throw new DataAccessException("ATM not found: " + atmId, null);
        }
    }

    public Optional<Atm> findById(long atmId) {
        return queryOne(SELECT + "WHERE a.atm_id = ?", this::map, atmId);
    }

    public List<Atm> findAll() {
        return queryList(SELECT + "ORDER BY a.atm_id", this::map);
    }

    public List<Atm> findByAdminId(String adminId) {
        return queryList(SELECT + "JOIN admin_atms m ON m.atm_id = a.atm_id WHERE m.admin_id = ? ORDER BY a.atm_id",
                this::map, adminId);
    }

    public void assignAdmin(String adminId, long atmId) {
        if (!isManagedBy(adminId, atmId)) {
            execute("INSERT INTO admin_atms (admin_id, atm_id) VALUES (?, ?)", adminId, atmId);
        }
    }

    public boolean isManagedBy(String adminId, long atmId) {
        return exists("SELECT 1 FROM admin_atms WHERE admin_id = ? AND atm_id = ?", adminId, atmId);
    }

    private Atm map(ResultSet rs) throws SQLException {
        Atm a = new Atm();
        a.setAtmId(rs.getLong("atm_id"));
        a.setLocation(rs.getString("location"));
        a.setStatus(AtmStatus.valueOf(rs.getString("status")));
        a.setMaxWithdrawPerTransaction(rs.getBigDecimal("max_withdraw_per_transaction"));
        a.setMaxDepositPerTransaction(rs.getBigDecimal("max_deposit_per_transaction"));
        a.setNoteMultiple(rs.getInt("note_multiple"));
        a.setLastHeartbeatAt(ts(rs, "last_heartbeat_at"));
        return a;
    }
}
