package org.example.pionerpixeltest.service.iml;

import lombok.RequiredArgsConstructor;
import org.example.pionerpixeltest.service.AccountService;
import org.example.pionerpixeltest.service.TransferService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class TransferServiceImpl implements TransferService {

    private final AccountService accountService;

    @Override
    @Transactional
    public void transfer(Long fromUserId, Long toUserId, BigDecimal amount) {
        accountService.transfer(fromUserId, toUserId, amount);
    }
}
