package org.example.pionerpixeltest.service;

import org.example.pionerpixeltest.dao.AccountRepository;
import org.example.pionerpixeltest.domain.Account;
import org.example.pionerpixeltest.service.iml.AccountServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {
    @Mock
    AccountRepository accountRepository;

    @InjectMocks
    AccountServiceImpl accountService;

    @Test
    void transfer_shouldTransferMoneySuccessfully() {
        Account from = new Account();
        from.setId(1L);
        from.setBalance(new BigDecimal("200"));

        Account to = new Account();
        to.setId(2L);
        to.setBalance(new BigDecimal("50"));

        when(accountRepository.findByUserIdForUpdate(1L)).thenReturn(Optional.of(from));
        when(accountRepository.findByUserIdForUpdate(2L)).thenReturn(Optional.of(to));

        accountService.transfer(1L, 2L, new BigDecimal("100"));

        assertEquals(new BigDecimal("100"), from.getBalance());
        assertEquals(new BigDecimal("150"), to.getBalance());
    }

    @Test
    void transfer_shouldThrowIfSenderNotFound() {
        when(accountRepository.findByUserIdForUpdate(1L)).thenReturn(Optional.empty());

        NoSuchElementException ex = assertThrows(NoSuchElementException.class,
                () -> accountService.transfer(1L, 2L, new BigDecimal("100")));

        assertEquals("Счёт отправителя не найден", ex.getMessage());
    }

    @Test
    void transfer_shouldThrowIfRecipientNotFound() {
        Account from = new Account();
        from.setId(1L);
        from.setBalance(new BigDecimal("200"));

        when(accountRepository.findByUserIdForUpdate(1L)).thenReturn(Optional.of(from));
        when(accountRepository.findByUserIdForUpdate(2L)).thenReturn(Optional.empty());

        NoSuchElementException ex = assertThrows(NoSuchElementException.class,
                () -> accountService.transfer(1L, 2L, new BigDecimal("100")));

        assertEquals("Счёт получателя не найден", ex.getMessage());
    }

    @Test
    void transfer_shouldThrowIfInsufficientBalance() {
        Account from = new Account();
        from.setId(1L);
        from.setBalance(new BigDecimal("50"));

        Account to = new Account();
        to.setId(2L);
        to.setBalance(new BigDecimal("50"));

        when(accountRepository.findByUserIdForUpdate(1L)).thenReturn(Optional.of(from));
        when(accountRepository.findByUserIdForUpdate(2L)).thenReturn(Optional.of(to));

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> accountService.transfer(1L, 2L, new BigDecimal("100")));

        assertEquals("Недостаточно средств", ex.getMessage());
    }

    @Test
    void transfer_shouldThrowIfTransferToSelf() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> accountService.transfer(1L, 1L, new BigDecimal("100")));

        assertEquals("Нельзя переводить самому себе", ex.getMessage());
    }

    @Test
    void transfer_shouldThrowIfAmountIsZero() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> accountService.transfer(1L, 2L, BigDecimal.ZERO));

        assertEquals("Сумма должна быть положительной", ex.getMessage());
    }

    @Test
    void transfer_shouldThrowIfAmountIsNegative() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> accountService.transfer(1L, 2L, new BigDecimal("-50")));

        assertEquals("Сумма должна быть положительной", ex.getMessage());
    }

}
