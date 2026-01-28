package com.github.gabrielhumbertdev.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import com.github.gabrielhumbertdev.model.account.CheckingAccount;
import com.github.gabrielhumbertdev.model.customer.Person;
import com.github.gabrielhumbertdev.util.EmailUtil;

//B) AccountController: Withdraw Triggers Fine + Alert (Q3C)
public class AccountControllerMinimumBalanceTest {

    private AccountController controller;

    @BeforeEach
    public void setup() {
        controller = new AccountController();
    }

    @Test
    public void withdraw_belowMinimumBalance_appliesFine() throws Exception {
        // Arrange
        Person customer = new Person("John Doe", "London");
        customer.setEmail("john@test.com");

        CheckingAccount checking = new CheckingAccount();
        double minimumBalance = checking.getMinimumBalance(); // 50.0
        double fineAmount = checking.getMinimumBalanceFine(); // 25.0
        checking.setBalance(80.0); // Start above minimum

        double withdrawAmount = 40.0;

        // Mock EmailUtil to prevent real email sending
        try (MockedStatic<EmailUtil> emailMock = Mockito.mockStatic(EmailUtil.class)) {
            emailMock.when(() -> EmailUtil.sendEmail(anyString(), anyString(), anyString())).thenAnswer(inv -> null);

            // Act - withdraw enough to go below minimum (80 - 40 = 40, which is < 50)
            controller.withdraw(customer, checking, withdrawAmount);

            // Assert
            // Expected: 80 - 40 = 40 (below 50 minimum)
            // Then fine applied: 40 - 25 = 15
            double expectedBalance = 80.0 - 40.0 - 25.0; // 15.0
            assertEquals(expectedBalance, checking.getBalance(), 0.01, "Balance should be 80 - 40 - 25 (fine) = 15");
            assertTrue(checking.getBalance() < minimumBalance,
                    "Balance should be below minimum after withdrawal and fine");
        }
    }

    @Test
    public void withdraw_alreadyBelowMinimum_stillAppliesFine() throws Exception {
        // Arrange
        Person customer = new Person("John Doe", "London");
        customer.setEmail("john@test.com");

        CheckingAccount checking = new CheckingAccount();
        checking.setBalance(40.0); // Already below minimum (50)

        double withdrawAmount = 10.0;
        double fineAmount = checking.getMinimumBalanceFine(); // 25.0

        // Mock EmailUtil to prevent real email sending
        try (MockedStatic<EmailUtil> emailMock = Mockito.mockStatic(EmailUtil.class)) {
            emailMock.when(() -> EmailUtil.sendEmail(anyString(), anyString(), anyString())).thenAnswer(inv -> null);

            // Act
            controller.withdraw(customer, checking, withdrawAmount);

            // Assert
            // Expected: 40 - 10 = 30 (still below 50)
            // Then fine: 30 - 25 = 5
            double expectedBalance = 40.0 - 10.0 - 25.0; // 5.0
            assertEquals(expectedBalance, checking.getBalance(), 0.01, "Balance should be 40 - 10 - 25 (fine) = 5");
        }
    }

    @Test
    public void withdraw_staysAboveMinimum_noFine() throws Exception {
        // Arrange
        Person customer = new Person("John Doe", "London");
        customer.setEmail("john@test.com");

        CheckingAccount checking = new CheckingAccount();
        checking.setBalance(200.0);

        double minimumBalance = checking.getMinimumBalance(); // 50.0
        double withdrawAmount = 50.0;

        // Mock EmailUtil to prevent real email sending
        try (MockedStatic<EmailUtil> emailMock = Mockito.mockStatic(EmailUtil.class)) {
            emailMock.when(() -> EmailUtil.sendEmail(anyString(), anyString(), anyString())).thenAnswer(inv -> null);

            // Act - withdraw but stay above minimum (200 - 50 = 150, which is > 50)
            controller.withdraw(customer, checking, withdrawAmount);

            // Assert - no fine applied, balance should be exactly 150
            assertEquals(150.0, checking.getBalance(), 0.01, "No fine should be applied when staying above minimum");
            assertTrue(checking.getBalance() > minimumBalance, "Balance should still be above minimum");
        }
    }

    @Test
    public void withdraw_exactlyAtMinimum_noFine() throws Exception {
        // Arrange
        Person customer = new Person("John Doe", "London");
        customer.setEmail("john@test.com");

        CheckingAccount checking = new CheckingAccount();
        double minimumBalance = checking.getMinimumBalance(); // 50.0
        checking.setBalance(100.0);

        // Mock EmailUtil to prevent real email sending
        try (MockedStatic<EmailUtil> emailMock = Mockito.mockStatic(EmailUtil.class)) {
            emailMock.when(() -> EmailUtil.sendEmail(anyString(), anyString(), anyString())).thenAnswer(inv -> null);

            // Act - withdraw to exactly minimum (100 - 50 = 50)
            controller.withdraw(customer, checking, 50.0);

            // Assert - no fine when exactly at minimum
            assertEquals(minimumBalance, checking.getBalance(), 0.01,
                    "Balance should be exactly at minimum (50) with no fine");
        }
    }

    @Test
    public void withdraw_justBelowMinimum_appliesFine() throws Exception {
        // Arrange
        Person customer = new Person("John Doe", "London");
        customer.setEmail("john@test.com");

        CheckingAccount checking = new CheckingAccount();
        double minimumBalance = checking.getMinimumBalance(); // 50.0
        double fineAmount = checking.getMinimumBalanceFine(); // 25.0
        checking.setBalance(100.0);

        // Mock EmailUtil to prevent real email sending
        try (MockedStatic<EmailUtil> emailMock = Mockito.mockStatic(EmailUtil.class)) {
            emailMock.when(() -> EmailUtil.sendEmail(anyString(), anyString(), anyString())).thenAnswer(inv -> null);

            // Act - withdraw to go just below minimum (100 - 51 = 49, which is < 50)
            controller.withdraw(customer, checking, 51.0);

            // Assert
            // Expected: 100 - 51 = 49 (below 50)
            // Then fine: 49 - 25 = 24
            double expectedBalance = 100.0 - 51.0 - 25.0; // 24.0
            assertEquals(expectedBalance, checking.getBalance(), 0.01, "Balance should be 100 - 51 - 25 (fine) = 24");
        }
    }

    @Test
    public void withdraw_withNullCustomer_throwsException() {
        // Arrange
        CheckingAccount checking = new CheckingAccount();
        checking.setBalance(200.0);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            controller.withdraw(null, checking, 50.0);
        });
    }

    @Test
    public void withdraw_withNullAccount_throwsException() {
        // Arrange
        Person customer = new Person("John Doe", "London");
        customer.setEmail("john@test.com");

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            controller.withdraw(customer, null, 50.0);
        });
    }
}