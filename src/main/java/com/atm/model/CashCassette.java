package com.atm.model;

public class CashCassette {
    private String cassetteId;
    private int denomination, noteCount, capacity, lowThreshold;

    public String getCassetteId() {
        return cassetteId;
    }

    public void setCassetteId(String cassetteId) {
        this.cassetteId = cassetteId;
    }

    public int getDenomination() {
        return denomination;
    }

    public void setDenomination(int denomination) {
        this.denomination = denomination;
    }

    public int getNoteCount() {
        return noteCount;
    }

    public void setNoteCount(int noteCount) {
        this.noteCount = noteCount;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getLowThreshold() {
        return lowThreshold;
    }

    public void setLowThreshold(int lowThreshold) {
        this.lowThreshold = lowThreshold;
    }
}
