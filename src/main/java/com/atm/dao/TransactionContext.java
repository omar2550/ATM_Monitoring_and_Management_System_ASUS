package com.atm.dao;

public record TransactionContext(String sessionId, long atmId, String accountNumber,
                                 String destinationAccount, String cardNumber) {

    public static TransactionContext of(String sessionId, long atmId, String accountNumber) {
        return new TransactionContext(sessionId, atmId, accountNumber, null, null);
    }

    public TransactionContext withDestination(String destinationAccount) {
        return new TransactionContext(sessionId, atmId, accountNumber, destinationAccount, cardNumber);
    }

    public TransactionContext withCard(String cardNumber) {
        return new TransactionContext(sessionId, atmId, accountNumber, destinationAccount, cardNumber);
    }
}
