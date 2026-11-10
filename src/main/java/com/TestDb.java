package com;

import com.atm.dao.DatabaseConnection;

import java.sql.*;

public class TestDb {
    public static void main(String[] args) {
        try (Connection conn = DatabaseConnection.getConnect()) {

            if (conn == null) {
                System.out.println("Connection failed");
                return;
            }
            System.out.println("Connected to: " + conn.getMetaData().getURL());

            System.out.println("\n--- Tables ---");
            try (Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery(
                         "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES " +
                                 "WHERE TABLE_SCHEMA = 'PUBLIC' ORDER BY TABLE_NAME")) {
                while (rs.next()) {
                    System.out.println(" - " + rs.getString(1));
                }
            }

            System.out.println("\n--- Users ---");
            try (Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery("SELECT user_id, role, username FROM users")) {
                while (rs.next()) {
                    System.out.println(rs.getString("user_id") + " | "
                            + rs.getString("role") + " | " + rs.getString("username"));
                }
            }

            System.out.println("\n--- ATMs ---");
            try (Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery("SELECT atm_id, location, status FROM atms")) {
                while (rs.next()) {
                    System.out.println(rs.getLong("atm_id") + " | "
                            + rs.getString("location") + " | " + rs.getString("status"));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}