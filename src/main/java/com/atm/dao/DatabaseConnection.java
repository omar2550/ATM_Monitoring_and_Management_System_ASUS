package com.atm.dao;

import org.h2.tools.RunScript;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:h2:file:./data/atmdb",
            USER = "ASUS", PASSWORD = "";

    static {
        initSchema();
    }

    private static void initSchema() {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             InputStream in = DatabaseConnection.class.getResourceAsStream("/schema.sql")) {

            if (in == null) {
                throw new IllegalStateException("schema.sql not found in resources");
            }
            RunScript.execute(conn, new InputStreamReader(in, StandardCharsets.UTF_8));
            System.out.println("Database schema initialized.");

        } catch (Exception e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    public static Connection getConnect() {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            System.out.println("Connecting to database Error: " + e.getMessage());
            return null;
        }
    }
}
