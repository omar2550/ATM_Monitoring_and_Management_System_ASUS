package com.atm.model;

import com.atm.enums.UserStatus;

import java.time.LocalDateTime;

public abstract class User {
    private String userId,
            name,
            nationalId,
            phone,
            email,
            userName, passwordHash;
    private UserStatus status;
    private int failedLoginAttempts;
    private LocalDateTime createdAt, lastLoginAt, lastLogoutAt;

    public void updateProfile(String name, String phone, String email) {
        this.name = name;
        this.phone = phone.length() == 11 ? phone: this.phone;
        // regx to check the emails ex@exampel.somthing
        this.email = email;
    }

    public void changePassword(String oldSecret, String newSecret) {
        if (passwordHash.equals(oldSecret) && newSecret.length() > 5) {
            passwordHash = newSecret;
        }
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
        // regx to check the emails ex@exampel.somthing
        this.email = email;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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
