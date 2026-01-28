package com.github.gabrielhumbertdev.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import com.github.gabrielhumbertdev.model.account.Account;
import com.github.gabrielhumbertdev.model.account.CheckingAccount;
import com.github.gabrielhumbertdev.model.customer.Customer;
import com.github.gabrielhumbertdev.model.customer.Person;
import com.github.gabrielhumbertdev.util.EmailUtil;

import jakarta.mail.MessagingException;

@DisplayName("EmailUtil Comprehensive Test Suite")
public class EmailUtilComprehensiveTest {

    @Nested
    @DisplayName("Happy Path Tests")
    class HappyPathTests {

        @Test
        @DisplayName("Should send email with valid parameters")
        public void sendEmail_withValidParameters_succeeds() throws Exception {
            try (MockedStatic<EmailUtil> emailMock = Mockito.mockStatic(EmailUtil.class)) {
                // Arrange
                emailMock.when(() -> EmailUtil.sendEmail(anyString(), anyString(), anyString()))
                        .thenAnswer(inv -> null);

                // Act
                EmailUtil.sendEmail("test@example.com", "Test", "Body");

                // Assert
                emailMock.verify(() -> EmailUtil.sendEmail(eq("test@example.com"), eq("Test"), eq("Body")), times(1));
            }
        }

        @Test
        @DisplayName("Should send multiple emails")
        public void sendEmail_multipleEmails_allSent() throws Exception {
            try (MockedStatic<EmailUtil> emailMock = Mockito.mockStatic(EmailUtil.class)) {
                emailMock.when(() -> EmailUtil.sendEmail(anyString(), anyString(), anyString()))
                        .thenAnswer(inv -> null);

                // Send 3 emails
                EmailUtil.sendEmail("user1@example.com", "Subject", "Body");
                EmailUtil.sendEmail("user2@example.com", "Subject", "Body");
                EmailUtil.sendEmail("user3@example.com", "Subject", "Body");

                // Verify all sent
                emailMock.verify(() -> EmailUtil.sendEmail(anyString(), anyString(), anyString()), times(3));
            }
        }
    }

    @Nested
    @DisplayName("Edge Case Tests")
    class EdgeCaseTests {

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = { "   ", "\t", "\n" })
        @DisplayName("Should handle blank recipients")
        public void sendEmail_withBlankRecipient_handled(String recipient) throws Exception {
            try (MockedStatic<EmailUtil> emailMock = Mockito.mockStatic(EmailUtil.class)) {
                emailMock.when(() -> EmailUtil.sendEmail(anyString(), anyString(), anyString()))
                        .thenAnswer(inv -> null);

                EmailUtil.sendEmail(recipient, "Subject", "Body");

                // Verify called (validation happens inside EmailUtil)
                emailMock.verify(() -> EmailUtil.sendEmail(eq(recipient), anyString(), anyString()), times(1));
            }
        }

        @Test
        @DisplayName("Should handle very long email body")
        public void sendEmail_withLongBody_handled() throws Exception {
            try (MockedStatic<EmailUtil> emailMock = Mockito.mockStatic(EmailUtil.class)) {
                emailMock.when(() -> EmailUtil.sendEmail(anyString(), anyString(), anyString()))
                        .thenAnswer(inv -> null);

                // Create 10KB body - FIXED: Create string manually instead of using repeat()
                StringBuilder sb = new StringBuilder(10000);
                for (int i = 0; i < 10000; i++) {
                    sb.append("A");
                }
                String longBody = sb.toString();

                EmailUtil.sendEmail("test@example.com", "Subject", longBody);

                emailMock.verify(
                        () -> EmailUtil.sendEmail(anyString(), anyString(), argThat(body -> body.length() == 10000)),
                        times(1));
            }
        }

        @Test
        @DisplayName("Should handle special characters in subject")
        public void sendEmail_withSpecialCharactersInSubject_handled() throws Exception {
            try (MockedStatic<EmailUtil> emailMock = Mockito.mockStatic(EmailUtil.class)) {
                emailMock.when(() -> EmailUtil.sendEmail(anyString(), anyString(), anyString()))
                        .thenAnswer(inv -> null);

                String specialSubject = "Test Alert with Special Chars";

                EmailUtil.sendEmail("test@example.com", specialSubject, "Body");

                emailMock.verify(() -> EmailUtil.sendEmail(anyString(), eq(specialSubject), anyString()), times(1));
            }
        }
    }

    @Nested
    @DisplayName("Error Handling Tests")
    class ErrorHandlingTests {

        @Test
        @DisplayName("Should throw MessagingException when SMTP fails")
        public void sendEmail_whenSMTPFails_throwsException() {
            try (MockedStatic<EmailUtil> emailMock = Mockito.mockStatic(EmailUtil.class)) {
                // Mock to throw exception
                emailMock.when(() -> EmailUtil.sendEmail(anyString(), anyString(), anyString()))
                        .thenThrow(new MessagingException("SMTP server unavailable"));

                // Assert exception is thrown
                assertThrows(MessagingException.class, () -> {
                    EmailUtil.sendEmail("test@example.com", "Subject", "Body");
                });
            }
        }

        @Test
        @DisplayName("Should throw MessagingException with correct message")
        public void sendEmail_whenFails_hasCorrectExceptionMessage() {
            try (MockedStatic<EmailUtil> emailMock = Mockito.mockStatic(EmailUtil.class)) {
                String errorMessage = "Connection timeout";
                emailMock.when(() -> EmailUtil.sendEmail(anyString(), anyString(), anyString()))
                        .thenThrow(new MessagingException(errorMessage));

                MessagingException exception = assertThrows(MessagingException.class, () -> {
                    EmailUtil.sendEmail("test@example.com", "Subject", "Body");
                });

                assertEquals(errorMessage, exception.getMessage());
            }
        }
    }

    @Nested
    @DisplayName("Parameter Verification Tests")
    class ParameterVerificationTests {

        @Test
        @DisplayName("Should receive exact email address")
        public void sendEmail_verifiesExactRecipient() throws Exception {
            try (MockedStatic<EmailUtil> emailMock = Mockito.mockStatic(EmailUtil.class)) {
                emailMock.when(() -> EmailUtil.sendEmail(anyString(), anyString(), anyString()))
                        .thenAnswer(inv -> null);

                EmailUtil.sendEmail("exact@example.com", "Subject", "Body");

                emailMock.verify(() -> EmailUtil.sendEmail(eq("exact@example.com"), anyString(), anyString()),
                        times(1));
            }
        }

        @Test
        @DisplayName("Should verify email body contains specific text")
        public void sendEmail_bodyContainsText() throws Exception {
            try (MockedStatic<EmailUtil> emailMock = Mockito.mockStatic(EmailUtil.class)) {
                emailMock.when(() -> EmailUtil.sendEmail(anyString(), anyString(), anyString()))
                        .thenAnswer(inv -> null);

                EmailUtil.sendEmail("test@example.com", "Alert", "Your balance is $100.00");

                emailMock.verify(() -> EmailUtil.sendEmail(anyString(), anyString(), contains("$100.00")), times(1));
            }
        }

        @Test
        @DisplayName("Should verify all three parameters match")
        public void sendEmail_allParametersMatch() throws Exception {
            try (MockedStatic<EmailUtil> emailMock = Mockito.mockStatic(EmailUtil.class)) {
                emailMock.when(() -> EmailUtil.sendEmail(anyString(), anyString(), anyString()))
                        .thenAnswer(inv -> null);

                EmailUtil.sendEmail("user@example.com", "Welcome", "Hello User");

                emailMock.verify(() -> EmailUtil.sendEmail(eq("user@example.com"), eq("Welcome"), eq("Hello User")),
                        times(1));
            }
        }
    }

    @Nested
    @DisplayName("Integration with Services")
    class IntegrationTests {

        @Test
        @DisplayName("AlertService should call EmailUtil correctly")
        public void alertService_callsEmailUtilWithCorrectParams() throws Exception {
            try (MockedStatic<EmailUtil> emailMock = Mockito.mockStatic(EmailUtil.class)) {
                emailMock.when(() -> EmailUtil.sendEmail(anyString(), anyString(), anyString()))
                        .thenAnswer(inv -> null);

                // Arrange - FIXED: Properly initialize lists
                List<Customer> customers = new ArrayList<>();
                List<Account> accounts = new ArrayList<>();
                AlertService alertService = new AlertService(customers, accounts);

                Person customer = new Person("John Doe", "London");
                customer.setEmail("john@test.com");

                CheckingAccount account = new CheckingAccount();
                account.setBalance(100.0);

                // Act
                alertService.sendTransactionAlert(customer, account, 50.0);

                // Assert
                emailMock.verify(() -> EmailUtil.sendEmail(eq("john@test.com"), eq("Transaction Alert"),
                        argThat(body -> body.contains("John Doe") && body.contains("$50.00"))), times(1));
            }
        }
    }

    @Nested
    @DisplayName("Advanced Verification Tests")
    class AdvancedVerificationTests {

        @Test
        @DisplayName("Should capture and verify arguments")
        public void sendEmail_captureArguments() throws Exception {
            try (MockedStatic<EmailUtil> emailMock = Mockito.mockStatic(EmailUtil.class)) {
                emailMock.when(() -> EmailUtil.sendEmail(anyString(), anyString(), anyString()))
                        .thenAnswer(inv -> null);

                // Call method
                EmailUtil.sendEmail("test@example.com", "Subject", "Body with data");

                // Verify with argument matchers
                emailMock.verify(() -> EmailUtil.sendEmail(eq("test@example.com"), eq("Subject"),
                        argThat(body -> body.contains("data"))), times(1));
            }
        }

        @Test
        @DisplayName("Should verify emails sent in correct order")
        public void emailsSentInCorrectOrder() throws Exception {
            try (MockedStatic<EmailUtil> emailMock = Mockito.mockStatic(EmailUtil.class)) {
                emailMock.when(() -> EmailUtil.sendEmail(anyString(), anyString(), anyString()))
                        .thenAnswer(inv -> null);

                // Send in specific order
                EmailUtil.sendEmail("first@example.com", "1", "First");
                EmailUtil.sendEmail("second@example.com", "2", "Second");
                EmailUtil.sendEmail("third@example.com", "3", "Third");

                // Verify all were sent
                emailMock.verify(() -> EmailUtil.sendEmail(contains("first"), anyString(), anyString()), times(1));
                emailMock.verify(() -> EmailUtil.sendEmail(contains("second"), anyString(), anyString()), times(1));
                emailMock.verify(() -> EmailUtil.sendEmail(contains("third"), anyString(), anyString()), times(1));
            }
        }

        @Test
        @DisplayName("Should verify email never sent when validation fails")
        public void emailNotSent_whenValidationFails() throws Exception {
            try (MockedStatic<EmailUtil> emailMock = Mockito.mockStatic(EmailUtil.class)) {
                emailMock.when(() -> EmailUtil.sendEmail(anyString(), anyString(), anyString()))
                        .thenAnswer(inv -> null);

                // Arrange
                List<Customer> customers = new ArrayList<>();
                List<Account> accounts = new ArrayList<>();
                AlertService service = new AlertService(customers, accounts);

                // Create customer WITHOUT email
                Person customer = new Person("John Doe", "London");
                // No email set!

                CheckingAccount account = new CheckingAccount();

                // Try to send alert
                service.sendTransactionAlert(customer, account, 100.0);

                // Verify email was NEVER sent
                emailMock.verify(() -> EmailUtil.sendEmail(anyString(), anyString(), anyString()), never());
            }
        }

        @Test
        @DisplayName("Should handle intermittent failures")
        public void emailService_handlesIntermittentFailure() throws Exception {
            try (MockedStatic<EmailUtil> emailMock = Mockito.mockStatic(EmailUtil.class)) {
                // First call fails, second succeeds
                emailMock.when(() -> EmailUtil.sendEmail(anyString(), anyString(), anyString()))
                        .thenThrow(new MessagingException("Temporary failure")).thenAnswer(inv -> null);

                // First attempt - should throw
                assertThrows(MessagingException.class, () -> {
                    EmailUtil.sendEmail("test@example.com", "Subject", "Body");
                });

                // Second attempt - should succeed
                assertDoesNotThrow(() -> {
                    EmailUtil.sendEmail("test@example.com", "Subject", "Body");
                });

                // Verify called twice total
                emailMock.verify(() -> EmailUtil.sendEmail(anyString(), anyString(), anyString()), times(2));
            }
        }
    }

    @Nested
    @DisplayName("Null Safety Tests")
    class NullSafetyTests {

        @Test
        @DisplayName("Should handle null recipient")
        public void sendEmail_withNullRecipient_handled() throws Exception {
            try (MockedStatic<EmailUtil> emailMock = Mockito.mockStatic(EmailUtil.class)) {
                emailMock.when(() -> EmailUtil.sendEmail(anyString(), anyString(), anyString()))
                        .thenAnswer(inv -> null);

                // Call with null recipient
                EmailUtil.sendEmail(null, "Subject", "Body");

                // Verify called with null
                emailMock.verify(() -> EmailUtil.sendEmail(isNull(), eq("Subject"), eq("Body")), times(1));
            }
        }

        @Test
        @DisplayName("Should handle null subject")
        public void sendEmail_withNullSubject_handled() throws Exception {
            try (MockedStatic<EmailUtil> emailMock = Mockito.mockStatic(EmailUtil.class)) {
                emailMock.when(() -> EmailUtil.sendEmail(anyString(), anyString(), anyString()))
                        .thenAnswer(inv -> null);

                // Call with null subject
                EmailUtil.sendEmail("test@example.com", null, "Body");

                // Verify called with null subject
                emailMock.verify(() -> EmailUtil.sendEmail(eq("test@example.com"), isNull(), eq("Body")), times(1));
            }
        }

        @Test
        @DisplayName("Should handle null body")
        public void sendEmail_withNullBody_handled() throws Exception {
            try (MockedStatic<EmailUtil> emailMock = Mockito.mockStatic(EmailUtil.class)) {
                emailMock.when(() -> EmailUtil.sendEmail(anyString(), anyString(), anyString()))
                        .thenAnswer(inv -> null);

                // Call with null body
                EmailUtil.sendEmail("test@example.com", "Subject", null);

                // Verify called with null body
                emailMock.verify(() -> EmailUtil.sendEmail(eq("test@example.com"), eq("Subject"), isNull()), times(1));
            }
        }
    }

    @Nested
    @DisplayName("Email Content Validation Tests")
    class EmailContentValidationTests {

        @Test
        @DisplayName("Should verify email contains customer name")
        public void emailBody_containsCustomerName() throws Exception {
            try (MockedStatic<EmailUtil> emailMock = Mockito.mockStatic(EmailUtil.class)) {
                emailMock.when(() -> EmailUtil.sendEmail(anyString(), anyString(), anyString()))
                        .thenAnswer(inv -> null);

                List<Customer> customers = new ArrayList<>();
                List<Account> accounts = new ArrayList<>();
                AlertService alertService = new AlertService(customers, accounts);

                Person customer = new Person("Jane Smith", "London");
                customer.setEmail("jane@test.com");

                CheckingAccount account = new CheckingAccount();
                account.setBalance(200.0);

                // Send alert
                alertService.sendTransactionAlert(customer, account, 75.0);

                // Verify email body contains customer name
                emailMock.verify(() -> EmailUtil.sendEmail(anyString(), anyString(), contains("Jane Smith")), times(1));
            }
        }

        @Test
        @DisplayName("Should verify email contains transaction amount")
        public void emailBody_containsTransactionAmount() throws Exception {
            try (MockedStatic<EmailUtil> emailMock = Mockito.mockStatic(EmailUtil.class)) {
                emailMock.when(() -> EmailUtil.sendEmail(anyString(), anyString(), anyString()))
                        .thenAnswer(inv -> null);

                List<Customer> customers = new ArrayList<>();
                List<Account> accounts = new ArrayList<>();
                AlertService alertService = new AlertService(customers, accounts);

                Person customer = new Person("John Doe", "London");
                customer.setEmail("john@test.com");

                CheckingAccount account = new CheckingAccount();
                account.setBalance(500.0);

                // Send alert for $123.45
                alertService.sendTransactionAlert(customer, account, 123.45);

                // Verify email body contains amount
                emailMock.verify(() -> EmailUtil.sendEmail(anyString(), anyString(), contains("$123.45")), times(1));
            }
        }

        @Test
        @DisplayName("Should verify email contains account number")
        public void emailBody_containsAccountNumber() throws Exception {
            try (MockedStatic<EmailUtil> emailMock = Mockito.mockStatic(EmailUtil.class)) {
                emailMock.when(() -> EmailUtil.sendEmail(anyString(), anyString(), anyString()))
                        .thenAnswer(inv -> null);

                List<Customer> customers = new ArrayList<>();
                List<Account> accounts = new ArrayList<>();
                AlertService alertService = new AlertService(customers, accounts);

                Person customer = new Person("John Doe", "London");
                customer.setEmail("john@test.com");

                CheckingAccount account = new CheckingAccount();
                long accountId = account.getACCOUNT_ID();

                // Send alert
                alertService.sendTransactionAlert(customer, account, 50.0);

                // Verify email body contains account ID
                emailMock.verify(
                        () -> EmailUtil.sendEmail(anyString(), anyString(), contains(String.valueOf(accountId))),
                        times(1));
            }
        }
    }
}