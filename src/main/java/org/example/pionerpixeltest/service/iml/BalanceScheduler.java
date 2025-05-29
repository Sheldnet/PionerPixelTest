package org.example.pionerpixeltest.service.iml;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.pionerpixeltest.service.AccountService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class BalanceScheduler {
    private final AccountService accountService;

    @Scheduled(fixedRate = 30_000)
    public void accrue() {
        log.info("Запуск начисления процентов");
        accountService.accrueInterest();
    }

}
