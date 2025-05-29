package org.example.pionerpixeltest.service;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class InitialBalanceHolder {

    private final Map<Long, BigDecimal> initialBalances = new ConcurrentHashMap<>();

    public void put(Long userId, BigDecimal balance) {
        initialBalances.putIfAbsent(userId, balance);
    }

    public BigDecimal get(Long userId) {
        return initialBalances.get(userId);
    }
}
