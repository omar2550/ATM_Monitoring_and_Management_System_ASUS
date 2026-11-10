package com.atm.model;

import com.atm.dao.AccountDao;
import com.atm.dao.CardDao;
import com.atm.dao.ServiceRequestDao;
import com.atm.enums.AccountStatus;
import com.atm.enums.CardStatus;
import com.atm.enums.RequestType;

import java.util.List;

public class Customer extends User {

    public Customer(String userId, String name, String nationalId, String phone,
                    String email, String username, String passwordHash) {
        super(userId, name, nationalId, phone, email, username, passwordHash);
    }

    public Customer() {
    }

    public List<Account> viewAccounts() {
        return new AccountDao().findByCustomerId(getUserId());
    }

    public List<Card> viewCards() {
        return new CardDao().findByCustomerId(getUserId());
    }

    public Account selectAccount(String accountNumber) {
        return new AccountDao().findByNumberAndCustomerId(accountNumber, getUserId()).orElseThrow(
                () -> new IllegalArgumentException("Account: " + accountNumber + " does not belong to this customer")
        );
    }

    public ServiceRequest requestFreezeAccount(String accountNumber, String reason) {
        Account account = selectAccount(accountNumber);
        if (account.getStatus() != AccountStatus.ACTIVE)
            throw new IllegalArgumentException("Only an ACTIVE account can be frozen");
        return submit(new ServiceRequest(RequestType.FREEZE_ACCOUNT, reason), accountNumber, null);
    }

    public ServiceRequest requestUnFreezeAccount(String accountNumber, String reason) {
        Account account = selectAccount(accountNumber);
        if (account.getStatus() != AccountStatus.FROZEN)
            throw new IllegalArgumentException("Only an frozen account can be activated");
        return submit(new ServiceRequest(RequestType.UNFREEZE_ACCOUNT, reason), accountNumber, null);
    }

    public ServiceRequest requestBlockCard(String cardNumber, String reason) {
        Card card = new CardDao().findByCardNumber(cardNumber).orElseThrow(
                () -> new IllegalArgumentException("Card: " + cardNumber + " does not belong to this customer")
        );
        if (card.getStatus() == CardStatus.BLOCKED || card.getStatus() == CardStatus.EXPIRED)
            throw new IllegalArgumentException("Card is already " + card.getStatus());
        return submit(new ServiceRequest(RequestType.BLOCK_CARD, reason), null, cardNumber);
    }

    private ServiceRequest submit(ServiceRequest request, String accountNumber, String cardNumber) {
        new ServiceRequestDao().save(request, getUserId(), accountNumber, cardNumber);
        return request;
    }
}
