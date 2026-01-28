package com.github.gabrielhumbertdev.service;


import com.github.gabrielhumbertdev.model.customer.Customer;
import com.github.gabrielhumbertdev.model.depositbox.SafetyDepositBox;
import com.github.gabrielhumbertdev.util.EmailUtil;

import jakarta.mail.MessagingException;

/**
 * Sprint 3 - Week 1 OOD4 Deposit Box Alerts: - Customer receives email alert
 * when a SafetyDepositBox is allocated - Customer receives email alert when a
 * SafetyDepositBox is released
 *
 * Uses EmailUtil (Jakarta Mail) and handles delivery failures.
 */
public class SafetyDepositBoxAlertService {

    private static final String SUBJECT_ALLOCATED = "Safety Deposit Box Allocated";
    private static final String SUBJECT_RELEASED = "Safety Deposit Box Released";

    /**
     * Sends an email alert when a deposit box is allocated to a customer. Returns
     * true if email sent successfully, false if failed.
     */
    public boolean sendAllocationAlert(Customer customer, SafetyDepositBox box) {
        if (customer == null || box == null) {
            return false;
        }

        String to = safe(customer.getEmail());
        if (to.isEmpty()) {
            return false;
        }

        String message = generateAllocationMessage(customer, box);

        try {
            EmailUtil.sendEmail(to, SUBJECT_ALLOCATED, message);
            return true;
        } catch (MessagingException e) {
            // delivery failure handled here
            return false;
        }
    }

    /**
     * Sends an email alert when a deposit box is released by a customer. Returns
     * true if email sent successfully, false if failed.
     */
    public boolean sendReleaseAlert(Customer customer, SafetyDepositBox box) {
        if (customer == null || box == null) {
            return false;
        }

        String to = safe(customer.getEmail());
        if (to.isEmpty()) {
            return false;
        }

        String message = generateReleaseMessage(customer, box);

        try {
            EmailUtil.sendEmail(to, SUBJECT_RELEASED, message);
            return true;
        } catch (MessagingException e) {
            // delivery failure handled here
            return false;
        }
    }

    /**
     * Standard allocation message.
     */
    public String generateAllocationMessage(Customer customer, SafetyDepositBox box) {
        return "Hello " + safe(customer.getName()) + ",\n\n"
                + "Your Safety Deposit Box has been allocated successfully.\n" + "Deposit Box Details:\n" + "- Box ID: "
                + box.getId() + "\n" + "- Status: ALLOCATED\n\n" + "Thank you for banking with us.\n"
                + "Best regards,\n" + "Your Bank";
    }

    /**
     * Standard release message.
     */
    public String generateReleaseMessage(Customer customer, SafetyDepositBox box) {
        return "Hello " + safe(customer.getName()) + ",\n\n"
                + "Your Safety Deposit Box has been revoked successfully.\n" + "Deposit Box Details:\n" + "- Box ID: "
                + box.getId() + "\n" + "- Status: REVOKED\n\n" + "Thank you for banking with us.\n" + "Best regards,\n"
                + "Your Bank";
    }

    private String safe(String value) {
        return value == null ? "" : value.trim();
    }
}
