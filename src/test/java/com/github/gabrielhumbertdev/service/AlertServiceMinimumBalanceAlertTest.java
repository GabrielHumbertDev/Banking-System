package com.github.gabrielhumbertdev.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import com.github.gabrielhumbertdev.model.account.CheckingAccount;
import com.github.gabrielhumbertdev.model.customer.Customer;
import com.github.gabrielhumbertdev.model.customer.Person;
import com.github.gabrielhumbertdev.util.EmailUtil;

//Q3B: Minimum balance fine email alert gets sent
public class AlertServiceMinimumBalanceAlertTest {

    @Test
    public void sendMinimumBalanceFineAlert_sendsEmailToCustomer() throws Exception {

        // Arrange (Q3B)
        List<Customer> customers = new ArrayList<>();
        List<com.github.gabrielhumbertdev.model.account.Account> accounts = new ArrayList<>();

        AlertService alertService = new AlertService(customers, accounts);

        Person customer = new Person("John Doe", "London");
        customer.setEmail("john.doe@test.com");

        CheckingAccount checking = new CheckingAccount();
        checking.setBalance(20.0); // already low (doesn't matter for this method)

        double fineAmount = 25.0;
        double minimumBalance = checking.getMinimumBalance();

        // Static mock EmailUtil (so no real email is sent)
        try (MockedStatic<EmailUtil> emailMock = Mockito.mockStatic(EmailUtil.class)) {

            emailMock.when(() -> EmailUtil.sendEmail(anyString(), anyString(), anyString())).thenAnswer(inv -> null);

            // Act
            boolean result = alertService.sendMinimumBalanceFineAlert(customer, checking, fineAmount, minimumBalance);

            // Assert
            assertTrue(result);

            emailMock.verify(() -> EmailUtil.sendEmail(org.mockito.ArgumentMatchers.eq("john.doe@test.com"),
                    org.mockito.ArgumentMatchers.eq("Minimum Balance Alert"),
                    org.mockito.ArgumentMatchers.contains("fine")), times(1));
        }
    }

    @Test
    public void sendMinimumBalanceFineAlert_withNullCustomer_returnsFalse() {
        // Arrange
        List<Customer> customers = new ArrayList<>();
        List<com.github.gabrielhumbertdev.model.account.Account> accounts = new ArrayList<>();
        AlertService alertService = new AlertService(customers, accounts);

        CheckingAccount checking = new CheckingAccount();
        checking.setBalance(20.0);

        // Act
        boolean result = alertService.sendMinimumBalanceFineAlert(null, checking, 25.0, 100.0);

        // Assert
        assertFalse(result);
    }

    @Test
    public void sendMinimumBalanceFineAlert_withNoEmail_returnsFalse() {
        // Arrange
        List<Customer> customers = new ArrayList<>();
        List<com.github.gabrielhumbertdev.model.account.Account> accounts = new ArrayList<>();
        AlertService alertService = new AlertService(customers, accounts);

        Person customer = new Person("John Doe", "London");
        // No email set

        CheckingAccount checking = new CheckingAccount();
        checking.setBalance(20.0);

        // Act
        boolean result = alertService.sendMinimumBalanceFineAlert(customer, checking, 25.0, 100.0);

        // Assert
        assertFalse(result);
    }
}