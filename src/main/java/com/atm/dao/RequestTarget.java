package com.atm.dao;

/** الـ associations بتاعة ServiceRequest: مين قدّمه، وبيخص أنهي حساب أو كارت. */
public record RequestTarget(String customerId, String accountNumber, String cardNumber) { }
