package com.github.gabrielhumbertdev.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import com.github.gabrielhumbertdev.model.customer.Person;
import com.github.gabrielhumbertdev.model.depositbox.SafetyDepositBox;
import com.github.gabrielhumbertdev.util.EmailUtil;

//Q4: Deposit box allocation + release (revoked) triggers alerts
public class SafetyDepositBoxServiceAlertTest {

    private SafetyDepositBoxService service;

    @BeforeEach
    public void setup() {
        service = SafetyDepositBoxService.getInstance();

        // Reset singleton state so tests are stable
        service.getSafetyDepositBoxes().clear();
        service.setNumberOfSafetyDepositBoxes(2);
    }

    @Test
    public void allocateSafetyDepositBox_withCustomer_sendsAllocationEmail() throws Exception {

        Person customer = new Person("Jane Doe", "London");
        customer.setEmail("jane.doe@test.com");

        try (MockedStatic<EmailUtil> emailMock = Mockito.mockStatic(EmailUtil.class)) {

            emailMock.when(() -> EmailUtil.sendEmail(anyString(), anyString(), anyString())).thenAnswer(inv -> null);

            // Act
            SafetyDepositBox box = service.allocateSafetyDepositBox(customer);

            // Assert
            assertNotNull(box);

            emailMock.verify(() -> EmailUtil.sendEmail(org.mockito.ArgumentMatchers.eq("jane.doe@test.com"),
                    org.mockito.ArgumentMatchers.eq("Safety Deposit Box Allocated"),
                    org.mockito.ArgumentMatchers.contains("ALLOCATED")), times(1));
        }
    }

    @Test
    public void releaseSafetyDepositBox_withCustomer_sendsReleaseEmail() throws Exception {

        Person customer = new Person("Jane Doe", "London");
        customer.setEmail("jane.doe@test.com");

        try (MockedStatic<EmailUtil> emailMock = Mockito.mockStatic(EmailUtil.class)) {

            emailMock.when(() -> EmailUtil.sendEmail(anyString(), anyString(), anyString())).thenAnswer(inv -> null);

            // Act - allocate INSIDE the mock scope
            SafetyDepositBox box = service.allocateSafetyDepositBox(customer);
            assertNotNull(box);

            // Release the box
            service.releaseSafetyDepositBox(customer, box);

            // Assert - verify the RELEASE email was sent
            emailMock.verify(() -> EmailUtil.sendEmail(org.mockito.ArgumentMatchers.eq("jane.doe@test.com"),
                    org.mockito.ArgumentMatchers.eq("Safety Deposit Box Released"),
                    org.mockito.ArgumentMatchers.anyString()), times(1));
        }
    }

    @Test
    public void allocateSafetyDepositBox_withNoEmail_stillAllocatesBox() throws Exception {
        // Arrange
        Person customer = new Person("Jane Doe", "London");
        // No email set

        try (MockedStatic<EmailUtil> emailMock = Mockito.mockStatic(EmailUtil.class)) {
            emailMock.when(() -> EmailUtil.sendEmail(anyString(), anyString(), anyString())).thenAnswer(inv -> null);

            // Act
            SafetyDepositBox box = service.allocateSafetyDepositBox(customer);

            // Assert - box still allocated even if email fails
            assertNotNull(box);
            assertTrue(box.isAllotted());
        }
    }

    @Test
    public void allocateSafetyDepositBox_whenNoBoxesAvailable_returnsNull() {
        // Arrange
        service.setNumberOfSafetyDepositBoxes(0); // No boxes
        Person customer = new Person("Jane Doe", "London");
        customer.setEmail("jane@test.com");

        // Act
        SafetyDepositBox box = service.allocateSafetyDepositBox(customer);

        // Assert
        assertNull(box);
    }

    @Test
    public void releaseSafetyDepositBox_marksBoxAsNotAllotted() throws Exception {
        // Arrange
        Person customer = new Person("Jane Doe", "London");
        customer.setEmail("jane@test.com");

        try (MockedStatic<EmailUtil> emailMock = Mockito.mockStatic(EmailUtil.class)) {
            emailMock.when(() -> EmailUtil.sendEmail(anyString(), anyString(), anyString())).thenAnswer(inv -> null);

            SafetyDepositBox box = service.allocateSafetyDepositBox(customer);

            // Act
            service.releaseSafetyDepositBox(customer, box);

            // Assert - box should no longer be allotted
            assertFalse(box.isAllotted());
        }
    }
}