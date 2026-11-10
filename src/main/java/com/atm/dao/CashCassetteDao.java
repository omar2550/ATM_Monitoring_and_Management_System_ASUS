package com.atm.dao;

import com.atm.model.CashCassette;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/** cash_cassettes. الـ atm_id (association "cash trays") بيتبعت كـ parameter. */
public class CashCassetteDao extends BaseDao {

    private static final String SELECT =
            "SELECT cassette_id, denomination, note_count, capacity, low_threshold FROM cash_cassettes ";

    public void save(CashCassette c, long atmId) {
        execute("INSERT INTO cash_cassettes (cassette_id, atm_id, denomination, note_count, capacity, low_threshold) " +
                        "VALUES (?, ?, ?, ?, ?, ?)",
                c.getCassetteId(), atmId, c.getDenomination(), c.getNoteCount(), c.getCapacity(), c.getLowThreshold());
    }

    public void update(CashCassette c) {
        int rows = execute("UPDATE cash_cassettes SET note_count = ?, capacity = ?, low_threshold = ? " +
                        "WHERE cassette_id = ?",
                c.getNoteCount(), c.getCapacity(), c.getLowThreshold(), c.getCassetteId());
        if (rows == 0) {
            throw new DataAccessException("Cassette not found: " + c.getCassetteId(), null);
        }
    }

    public Optional<CashCassette> findById(String cassetteId) {
        return queryOne(SELECT + "WHERE cassette_id = ?", this::map, cassetteId);
    }

    public List<CashCassette> findByAtmId(long atmId) {
        return queryList(SELECT + "WHERE atm_id = ? ORDER BY denomination DESC", this::map, atmId);
    }

    public Optional<CashCassette> findByAtmIdAndDenomination(long atmId, int denomination) {
        return queryOne(SELECT + "WHERE atm_id = ? AND denomination = ?", this::map, atmId, denomination);
    }

    private CashCassette map(ResultSet rs) throws SQLException {
        CashCassette c = new CashCassette();
        c.setCassetteId(rs.getString("cassette_id"));
        c.setDenomination(rs.getInt("denomination"));
        c.setNoteCount(rs.getInt("note_count"));
        c.setCapacity(rs.getInt("capacity"));
        c.setLowThreshold(rs.getInt("low_threshold"));
        return c;
    }
}
