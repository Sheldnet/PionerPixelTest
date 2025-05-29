package org.example.pionerpixeltest.service.iml;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.pionerpixeltest.dao.AccountRepository;
import org.example.pionerpixeltest.domain.Account;
import org.example.pionerpixeltest.service.AccountService;
import org.example.pionerpixeltest.service.InitialBalanceHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepo;
    private final ConcurrentMap<Long, BigDecimal> initialMap = new ConcurrentHashMap<>();
    private final InitialBalanceHolder initialBalanceHolder;



    @PostConstruct
    public void cacheInitialBalances() {
        accountRepo.findAll().forEach(acc ->
                initialBalanceHolder.put(acc.getUser().getId(), acc.getBalance()));
    }

    @Override
    @Transactional
    public void accrueInterest() {
        log.info("Начисление процентов...");

        var accounts = accountRepo.findAll();

        for (Account acc : accounts) {
            Long userId = acc.getUser().getId();
            BigDecimal current = acc.getBalance();
            BigDecimal initial = initialBalanceHolder.get(userId);

            if (initial == null) continue;

            BigDecimal increased = current.multiply(BigDecimal.valueOf(1.10));
            BigDecimal limit = initial.multiply(BigDecimal.valueOf(2.07));

            if (increased.compareTo(limit) > 0) {
                acc.setBalance(limit);
            } else {
                acc.setBalance(increased);
            }
        }
    }



    @Override
    @Transactional
    public void registerAccount(Account account) {
        accountRepo.save(account);
        initialMap.putIfAbsent(
                account.getId(), account.getBalance());
    }

    @Override
    @Transactional
    public void transfer(Long fromUserId, Long toUserId, BigDecimal amount) {

        if (fromUserId.equals(toUserId)) {
            throw new IllegalArgumentException("Нельзя переводить самому себе");
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Сумма должна быть положительной");
        }

        Account from = accountRepo.findByUserIdForUpdate(fromUserId)
                .orElseThrow(() -> new NoSuchElementException("Счёт отправителя не найден"));
        Account to   = accountRepo.findByUserIdForUpdate(toUserId)
                .orElseThrow(() -> new NoSuchElementException("Счёт получателя не найден"));

        if (from.getBalance().compareTo(amount) < 0) {
            throw new IllegalStateException("Недостаточно средств");
        }

        from.setBalance(from.getBalance().subtract(amount));
        to.setBalance(to.getBalance().add(amount));

    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getBalance(Long userId) {
        return accountRepo.findByUserId(userId)
                .map(Account::getBalance)
                .orElseThrow(() -> new NoSuchElementException("Счёт не найден"));
    }

}


