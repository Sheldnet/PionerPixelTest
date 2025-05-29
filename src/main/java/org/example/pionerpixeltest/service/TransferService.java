package org.example.pionerpixeltest.service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public interface TransferService {
    void transfer(Long fromUserId, Long toUserId, BigDecimal amount);

}
