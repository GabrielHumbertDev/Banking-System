package com.github.gabrielhumbertdev.service;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import com.github.gabrielhumbertdev.dao.AccountReaderDAO;
import com.github.gabrielhumbertdev.dao.AccountWriterDAO;
import com.github.gabrielhumbertdev.model.account.Account;
import com.github.gabrielhumbertdev.model.account.CheckingAccount;
import com.github.gabrielhumbertdev.model.account.SavingsAccount;

//=======================
//Story 2: Account Management
//=======================
//Purpose: Verify that AccountService correctly delegates
//operations to the DAO layer.

public class AccountServiceImplTest {

    @Test
    public void getAccounts_returnsListFromReaderDao() {

        AccountReaderDAO readerDao = mock(AccountReaderDAO.class);
        AccountWriterDAO writerDao = mock(AccountWriterDAO.class);

        AccountService service = new AccountServiceImpl(readerDao, writerDao);

        @SuppressWarnings("unchecked")
        // Arrange
        List<Account> accounts = mock(List.class);
        when(readerDao.readAccounts()).thenReturn(accounts);

        // Act
        List<Account> result = service.getAccounts();

        // Assert
        assertSame(accounts, result);
        verify(readerDao).readAccounts();
        verifyNoInteractions(writerDao);
    }

    private static Stream<Account> accountProvider() {
        return Stream.of(new CheckingAccount(), new SavingsAccount());
    }

    @ParameterizedTest
    @MethodSource("accountProvider")
    public void removeAccount_callsDeleteAccountOnWriterDao(Account account) {
        // Arrange
        AccountReaderDAO readerDao = mock(AccountReaderDAO.class);
        AccountWriterDAO writerDao = mock(AccountWriterDAO.class);

        AccountService service = new AccountServiceImpl(readerDao, writerDao);

        // Act
        service.removeAccount(account);

        // Assert
        verify(writerDao).deleteAccount(account);
        verifyNoInteractions(readerDao);
    }

    @ParameterizedTest
    @MethodSource("accountProvider")
    public void createAccount_callsCreateAccountOnWriterDao_andReturnsAccount(Account account) {
        // Arrange
        AccountReaderDAO readerDao = mock(AccountReaderDAO.class);
        AccountWriterDAO writerDao = mock(AccountWriterDAO.class);

        AccountService service = new AccountServiceImpl(readerDao, writerDao);

        when(writerDao.createAccount(account)).thenReturn(account);

        // Act
        Account result = service.createAccount(account);

        // Assert
        assertSame(account, result);
        verify(writerDao).createAccount(account);
        verifyNoInteractions(readerDao);
    }
}
