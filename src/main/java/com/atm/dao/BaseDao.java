package com.atm.dao;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * الأساس المشترك لكل الـ DAOs: فتح الاتصال، ربط الـ parameters، وتحويل الأخطاء.
 */
abstract class BaseDao {

    @FunctionalInterface
    protected interface RowMapper<T> {
        T map(ResultSet rs) throws SQLException;
    }

    /** اتصال جديد، أو الاتصال بتاع Tx.run لو شغالين جواها (وفي الحالة دي close() مبيعملش حاجة). */
    protected Connection open() {
        Connection shared = Tx.current();
        if (shared != null) {
            return nonClosing(shared);
        }
        Connection conn = DatabaseConnection.getConnect();
        if (conn == null) {
            throw new DataAccessException("Database connection is not available", null);
        }
        return conn;
    }

    protected <T> List<T> queryList(String sql, RowMapper<T> mapper, Object... params) {
        try (Connection conn = open(); PreparedStatement ps = conn.prepareStatement(sql)) {
            bind(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                List<T> result = new ArrayList<>();
                while (rs.next()) {
                    result.add(mapper.map(rs));
                }
                return result;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Query failed: " + sql, e);
        }
    }

    protected <T> Optional<T> queryOne(String sql, RowMapper<T> mapper, Object... params) {
        List<T> rows = queryList(sql, mapper, params);
        return rows.isEmpty() ? Optional.empty() : Optional.of(rows.get(0));
    }

    protected boolean exists(String sql, Object... params) {
        return !queryList(sql, rs -> 1, params).isEmpty();
    }

    /** INSERT / UPDATE / DELETE. بترجع عدد الصفوف المتأثرة. */
    protected int execute(String sql, Object... params) {
        try (Connection conn = open(); PreparedStatement ps = conn.prepareStatement(sql)) {
            bind(ps, params);
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Statement failed: " + sql, e);
        }
    }

    /** INSERT على جدول بـ identity، بترجع المفتاح اللي اتولّد. */
    protected long insertReturningKey(String sql, Object... params) {
        try (Connection conn = open();
             PreparedStatement ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            bind(ps, params);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getLong(1);
                }
                throw new DataAccessException("No generated key returned", null);
            }
        } catch (SQLException e) {
            throw new DataAccessException("Insert failed: " + sql, e);
        }
    }

    // ---------------------------------------------------------------

    private static void bind(PreparedStatement ps, Object[] params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            Object p = params[i];
            int idx = i + 1;
            if (p == null) {
                ps.setObject(idx, null);
            } else if (p instanceof Enum<?> e) {
                ps.setString(idx, e.name());
            } else if (p instanceof LocalDateTime t) {
                ps.setTimestamp(idx, Timestamp.valueOf(t));
            } else if (p instanceof LocalDate d) {
                ps.setDate(idx, Date.valueOf(d));
            } else {
                ps.setObject(idx, p);
            }
        }
    }

    private static Connection nonClosing(Connection shared) {
        return (Connection) Proxy.newProxyInstance(
                Connection.class.getClassLoader(),
                new Class<?>[]{Connection.class},
                (proxy, method, args) -> {
                    if (method.getName().equals("close")) {
                        return null;
                    }
                    try {
                        return method.invoke(shared, args);
                    } catch (InvocationTargetException e) {
                        throw e.getCause();
                    }
                });
    }

    // helpers لقراءة قيم nullable
    protected static LocalDateTime ts(ResultSet rs, String col) throws SQLException {
        Timestamp t = rs.getTimestamp(col);
        return t == null ? null : t.toLocalDateTime();
    }

    protected static LocalDate date(ResultSet rs, String col) throws SQLException {
        Date d = rs.getDate(col);
        return d == null ? null : d.toLocalDate();
    }
}
