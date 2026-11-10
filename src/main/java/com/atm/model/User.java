package com.atm.model;

import com.atm.enums.UserStatus;
import java.time.LocalDateTime;

public abstract class User {
    private String userId,
            name,
            nationalId,
            phone,
            email,
            username, passwordHash;
    private UserStatus status;
    private int failedLoginAttempts;
    private LocalDateTime createdAt, lastLoginAt, lastLogoutAt;

    User(String userId, String name, String nationalId, String phone,
                   String email, String username, String passwordHash) {
        this.userId = userId;
        this.name = name;
        this.nationalId = nationalId;
        this.phone = phone;
        this.email = email;
        this.username = username;
        this.passwordHash = passwordHash;
        this.status = UserStatus.ACTIVE;
        this.failedLoginAttempts = 0;
        this.createdAt = LocalDateTime.now();
    }

    public User() {}

    public void updateProfile(String name, String phone, String email) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name must not be empty");
        }
        if (phone.length() != 11) {
            throw new IllegalArgumentException("phone must not be 11");
        }
        this.name = name;
        this.phone = phone;
        this.email = email;
    }

    public void changePassword(String oldSecret, String newSecret) {
        if(newSecret == null || newSecret.isBlank()) {
            throw new IllegalArgumentException("New password must not be empty");
        }
        if(oldSecret == null || oldSecret.isBlank()) {
            throw new IllegalArgumentException("Old password must not be empty");
        }
        if(newSecret.length() <=5){
            throw new IllegalArgumentException("password too short");
        }
        if (oldSecret.equals(newSecret)) {
            throw new IllegalArgumentException("pass can't be the same");
        }
        if (passwordHash.equals(oldSecret)) {
            throw new IllegalArgumentException("old pass isn't the same");
        }
        passwordHash=newSecret;
    }

    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getNationalId() {
        return nationalId;
    }
    public void setNationalId(String nationalId) {
        this.nationalId = nationalId;
    }
    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        if (phone.length() == 11)
            this.phone = phone;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getPasswordHash() {
        return passwordHash;
    }
    public void setPasswordHash(String passwordHash) {
        if (passwordHash.length() > 5)
            this.passwordHash = passwordHash;
    }
    public UserStatus getStatus() {
        return status;
    }
    public void setStatus(UserStatus status) {
        this.status = status;
    }
    public int getFailedLoginAttempts() {
        return failedLoginAttempts;
    }
    public void setFailedLoginAttempts(int failedLoginAttempts) {
        this.failedLoginAttempts = failedLoginAttempts;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    public LocalDateTime getLastLoginAt() {
        return lastLoginAt;
    }
    public void setLastLoginAt(LocalDateTime lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
    }
    public LocalDateTime getLastLogoutAt() {
        return lastLogoutAt;
    }
    public void setLastLogoutAt(LocalDateTime lastLogoutAt) {
        this.lastLogoutAt = lastLogoutAt;
    }
}
