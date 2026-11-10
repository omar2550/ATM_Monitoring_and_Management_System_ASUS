package com.atm.model;

import com.atm.enums.AccountStatus;
import com.atm.enums.AccountType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Account {
    private String accountNumber;
    private BigDecimal balance,
    dailyLimit, withdrawnToday;
    private AccountStatus status;
    private AccountType type;
    private LocalDateTime openedAt, lastResetDate;


    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public BigDecimal getDailyLimit() {
        return dailyLimit;
    }

    public void setDailyLimit(BigDecimal dailyLimit) {
        this.dailyLimit = dailyLimit;
    }

    public BigDecimal getWithdrawnToday() {
        return withdrawnToday;
    }

    public void setWithdrawnToday(BigDecimal withdrawnToday) {
        this.withdrawnToday = withdrawnToday;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public void setStatus(AccountStatus status) {
        this.status = status;
    }

    public AccountType getType() {
        return type;
    }

    public void setType(AccountType type) {
        this.type = type;
    }

    public LocalDateTime getOpenedAt() {
        return openedAt;
    }

    public void setOpenedAt(LocalDateTime openedAt) {
        this.openedAt = openedAt;
    }

    public LocalDateTime getLastResetDate() {
        return lastResetDate;
    }

    public void setLastResetDate(LocalDateTime lastResetDate) {
        this.lastResetDate = lastResetDate;
    }
}
