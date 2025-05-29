package org.example.pionerpixeltest.service;

import org.example.pionerpixeltest.domain.Account;

import java.math.BigDecimal;

public interface AccountService {
    void accrueInterest();

    void registerAccount(Account account);

    void transfer(Long fromUserId, Long toUserId, BigDecimal amount);

    BigDecimal getBalance(Long userId);
}
