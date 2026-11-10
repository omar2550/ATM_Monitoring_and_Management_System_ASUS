package com.atm.dao;

import com.atm.model.Admin;
import com.atm.model.Customer;
import com.atm.model.User;
import com.atm.enums.UserStatus;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class UserDao extends BaseDao {

    private static final String SELECT =
            "SELECT user_id, role, name, national_id, phone, email, username, password_hash, status, " +
            "failed_login_attempts, created_at, last_login_at, last_logout_at FROM users ";

    public void save(User user) {
        String role = user instanceof Admin ? "ADMIN" : "CUSTOMER";
        execute("INSERT INTO users (user_id, role, name, national_id, phone, email, username, password_hash, " +
                        "status, failed_login_attempts, created_at, last_login_at, last_logout_at) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                user.getUserId(), role, user.getName(), user.getNationalId(), user.getPhone(), user.getEmail(),
                user.getUsername(), user.getPasswordHash(), user.getStatus(), user.getFailedLoginAttempts(),
                user.getCreatedAt(), user.getLastLoginAt(), user.getLastLogoutAt());
    }

    public void update(User user) {
        int rows = execute("UPDATE users SET name = ?, phone = ?, email = ?, username = ?, password_hash = ?, " +
                        "status = ?, failed_login_attempts = ?, last_login_at = ?, last_logout_at = ? " +
                        "WHERE user_id = ?",
                user.getName(), user.getPhone(), user.getEmail(), user.getUsername(), user.getPasswordHash(),
                user.getStatus(), user.getFailedLoginAttempts(), user.getLastLoginAt(), user.getLastLogoutAt(),
                user.getUserId());
        if (rows == 0) {
            throw new DataAccessException("User not found: " + user.getUserId(), null);
        }
    }

    public Optional<User> findById(String userId) {
        return queryOne(SELECT + "WHERE user_id = ?", this::map, userId);
    }

    public Optional<User> findByUsername(String username) {
        return queryOne(SELECT + "WHERE username = ?", this::map, username);
    }

    public Optional<Customer> findCustomerById(String userId) {
        return findById(userId).filter(u -> u instanceof Customer).map(u -> (Customer) u);
    }

    public Optional<Customer> findCustomerByUsername(String username) {
        return findByUsername(username).filter(u -> u instanceof Customer).map(u -> (Customer) u);
    }

    public Optional<Admin> findAdminById(String userId) {
        return findById(userId).filter(u -> u instanceof Admin).map(u -> (Admin) u);
    }

    public Optional<Admin> findAdminByUsername(String username) {
        return findByUsername(username).filter(u -> u instanceof Admin).map(u -> (Admin) u);
    }

    public List<Customer> findAllCustomers() {
        return queryList(SELECT + "WHERE role = 'CUSTOMER' ORDER BY created_at", this::map).stream()
                .map(u -> (Customer) u).toList();
    }

    public boolean existsByUsername(String username) {
        return exists("SELECT 1 FROM users WHERE username = ?", username);
    }

    public boolean existsByNationalId(String nationalId) {
        return exists("SELECT 1 FROM users WHERE national_id = ?", nationalId);
    }

    private User map(ResultSet rs) throws SQLException {
        boolean admin = "ADMIN".equals(rs.getString("role"));
        String id = rs.getString("user_id");
        String name = rs.getString("name");
        String nationalId = rs.getString("national_id");
        String phone = rs.getString("phone");
        String email = rs.getString("email");
        String username = rs.getString("username");
        String hash = rs.getString("password_hash");

        User user = admin
                ? new Admin(id, name, nationalId, phone, email, username, hash)
                : new Customer(id, name, nationalId, phone, email, username, hash);
        user.setStatus(UserStatus.valueOf(rs.getString("status")));
        user.setFailedLoginAttempts(rs.getInt("failed_login_attempts"));
        user.setCreatedAt(ts(rs, "created_at"));
        user.setLastLoginAt(ts(rs, "last_login_at"));
        user.setLastLogoutAt(ts(rs, "last_logout_at"));
        return user;
    }
}
