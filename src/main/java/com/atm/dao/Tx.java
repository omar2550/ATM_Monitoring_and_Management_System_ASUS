package com.atm.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.Supplier;

/**
 * Unit of work بسيط: كل الـ DAO calls اللي جوه Tx.run(...) بتشتغل على نفس الاتصال،
 * يا كلها تتحفظ (commit) يا كلها تتلغي (rollback).
 *
 * <pre>
 *   Tx.run(() -> {
 *       accountDao.update(a);
 *       cassetteDao.update(c);
 *   });
 * </pre>
 * لو اتنادى Tx.run جوه Tx.run تانية، بيكمل في نفس الـ transaction (join).
 */
public final class Tx {

    private static final ThreadLocal<Connection> CURRENT = new ThreadLocal<>();

    private Tx() { }

    static Connection current() {
        return CURRENT.get();
    }

    public static <T> T run(Supplier<T> work) {
        if (CURRENT.get() != null) {
            return work.get();                       // join the running transaction
        }
        Connection conn = DatabaseConnection.getConnect();
        if (conn == null) {
            throw new DataAccessException("Database connection is not available", null);
        }
        try {
            conn.setAutoCommit(false);
            CURRENT.set(conn);
            T result = work.get();
            conn.commit();
            return result;
        } catch (SQLException e) {
            rollbackQuietly(conn);
            throw new DataAccessException("Transaction failed", e);
        } catch (RuntimeException e) {
            rollbackQuietly(conn);
            throw e;
        } finally {
            CURRENT.remove();
            try {
                conn.setAutoCommit(true);
                conn.close();
            } catch (SQLException ignored) {
                // nothing else to do
            }
        }
    }

    public static void run(Runnable work) {
        run(() -> {
            work.run();
            return null;
        });
    }

    private static void rollbackQuietly(Connection conn) {
        try {
            conn.rollback();
        } catch (SQLException ignored) {
            // nothing else to do
        }
    }
}
