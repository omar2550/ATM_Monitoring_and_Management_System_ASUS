package com.atm.model;

import com.atm.enums.AtmStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Atm {
    private String atmId, location;
    private AtmStatus status;
    private BigDecimal maxWithdrawPerTransaction,
    maxDepositPerTransaction;
    private int noteMultiple;
    private LocalDateTime lastHeartbeatAt;

    public String getAtmId() {
        return atmId;
    }

    public void setAtmId(String atmId) {
        this.atmId = atmId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public AtmStatus getStatus() {
        return status;
    }

    public void setStatus(AtmStatus status) {
        this.status = status;
    }

    public BigDecimal getMaxWithdrawPerTransaction() {
        return maxWithdrawPerTransaction;
    }

    public void setMaxWithdrawPerTransaction(BigDecimal maxWithdrawPerTransaction) {
        this.maxWithdrawPerTransaction = maxWithdrawPerTransaction;
    }

    public BigDecimal getMaxDepositPerTransaction() {
        return maxDepositPerTransaction;
    }

    public void setMaxDepositPerTransaction(BigDecimal maxDepositPerTransaction) {
        this.maxDepositPerTransaction = maxDepositPerTransaction;
    }

    public int getNoteMultiple() {
        return noteMultiple;
    }

    public void setNoteMultiple(int noteMultiple) {
        this.noteMultiple = noteMultiple;
    }

    public LocalDateTime getLastHeartbeatAt() {
        return lastHeartbeatAt;
    }

    public void setLastHeartbeatAt(LocalDateTime lastHeartbeatAt) {
        this.lastHeartbeatAt = lastHeartbeatAt;
    }
}
