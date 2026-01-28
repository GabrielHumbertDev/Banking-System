package com.github.gabrielhumbertdev.service;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.github.gabrielhumbertdev.model.account.Account;
import com.github.gabrielhumbertdev.model.customer.Customer;
import com.github.gabrielhumbertdev.util.EmailUtil;

import jakarta.mail.MessagingException;

/**
 * AlertService
 *
 * Story 3.1 (a/b): Transaction Alerts via Email - Generates standard alert
 * message format - Sends email using EmailUtil (Jakarta Mail) - Uses
 * streams/lambdas for lookups - Handles delivery failures (MessagingException)
 */
public class AlertService {

    private static final String EMAIL_SUBJECT = "Transaction Alert";
    private static final DecimalFormat MONEY = new DecimalFormat("0.00");

    private final List<Customer> customers;
    private final List<Account> accounts;

    /**
     * Dependency: customer + account entity lists (per UML requirement).
     */
    public AlertService(List<Customer> customers, List<Account> accounts) {
        this.customers = Objects.requireNonNull(customers, "customers cannot be null");
        this.accounts = Objects.requireNonNull(accounts, "accounts cannot be null");
    }

    // ============================
    // Question 2: Transaction alerts for deposit/withdraw
    // ============================
    /**
     * Sends a transaction alert for a specific customer & account. Uses
     * generateAlertMessage(...) and EmailUtil.sendEmail(...).
     *
     * Handles message delivery failures and returns success/failure.
     */
    public boolean sendTransactionAlert(Customer customer, Account account, double amount) {
        if (customer == null || account == null) {
            return false;
        }

        // Standard message body (required format)
        String messageBody = generateAlertMessage(customer, account, amount);

        // Question 2: Customer stores email id so alerts can be delivered
        String toEmail = safe(customer.getEmail());

        if (toEmail.isEmpty()) {
            return false;
        }

        try {
            EmailUtil.sendEmail(toEmail, EMAIL_SUBJECT, messageBody);
            return true;
        } catch (MessagingException e) {
            // Requirement: "take care of message delivery failures"
            return false;
        }
    }

    /**
     * Sends a transaction alert by account id. Uses streams to find: - the account
     * - the customer who owns that account
     */
    public boolean sendTransactionAlert(long accountId, double amount) {

        Optional<Account> accountOpt = accounts.stream().filter(a -> a != null && a.getACCOUNT_ID() == accountId)
                .findFirst();

        if (!accountOpt.isPresent()) {
            return false;
        }

        Account account = accountOpt.get();

        Optional<Customer> customerOpt = customers.stream().filter(Objects::nonNull)
                .filter(c -> c.getAccounts() != null)
                .filter(c -> c.getAccounts().stream().anyMatch(a -> a != null && a.getACCOUNT_ID() == accountId))
                .findFirst();

        if (!customerOpt.isPresent()) {
            return false;
        }

        return sendTransactionAlert(customerOpt.get(), account, amount);
    }

    // ============================
    // UML Requirement: sendTransactionAlert(customerId: Long): void
    // ============================
    /**
     * Sends a transaction alert by customer ID. Uses streams to find the customer
     * and their accounts.
     */
    public void sendTransactionAlert(Long customerId) {
        if (customerId == null) {
            return;
        }

        Optional<Customer> customerOpt = customers.stream().filter(Objects::nonNull)
                .filter(c -> c.getCUSTOMER_ID() == customerId).findFirst();

        if (!customerOpt.isPresent()) {
            return;
        }

        Customer customer = customerOpt.get();

        if (customer.getAccounts() == null || customer.getAccounts().isEmpty()) {
            return;
        }

        // Send alert for the first account
        Account account = customer.getAccounts().get(0);

        String messageBody = generateAlertMessage(customer, account, account.getBalance());
        String toEmail = safe(customer.getEmail());

        if (toEmail.isEmpty()) {
            return;
        }

        try {
            EmailUtil.sendEmail(toEmail, EMAIL_SUBJECT, messageBody);
        } catch (MessagingException e) {
            // Delivery failure handled here
        }
    }

    // ============================
    // Question 3B: Minimum balance violation alert (fine charged)
    // ============================
    /**
     * Sends an email alert when the account balance violates the minimum balance
     * criteria AND a fine has been charged.
     *
     * Requirement (Q3): - Set minimum balance limit for checking account (done in
     * CheckingAccount) - Generate email alert informing customer about the fine
     * charged
     */
    public boolean sendMinimumBalanceFineAlert(Customer customer, Account account, double fineAmount,
                                               double minimumBalance) {

        if (customer == null || account == null) {
            return false;
        }

        String toEmail = safe(customer.getEmail());
        if (toEmail.isEmpty()) {
            return false;
        }

        String messageBody = generateMinimumBalanceFineMessage(customer, account, fineAmount, minimumBalance);

        try {
            EmailUtil.sendEmail(toEmail, "Minimum Balance Alert", messageBody);
            return true;
        } catch (MessagingException e) {
            // Delivery failure handled here
            return false;
        }
    }

    /**
     * Question 3B: Generates the standard message for minimum balance fine alerts.
     */
    public String generateMinimumBalanceFineMessage(Customer customer, Account account, double fineAmount,
                                                    double minimumBalance) {

        String customerName = safe(customer.getName());
        String accountNumber = String.valueOf(account.getACCOUNT_ID());
        String minBalanceStr = "$" + MONEY.format(minimumBalance);
        String fineStr = "$" + MONEY.format(fineAmount);
        String newBalanceStr = "$" + MONEY.format(account.getBalance());

        return "Hello " + customerName + ",\n\n"
                + "We would like to notify you that your account balance is below the minimum required.\n"
                + "Minimum Balance Requirement:\n" + "- Minimum Balance: " + minBalanceStr + "\n\n"
                + "A fine has been charged for the respective account:\n" + "- Fine Amount: " + fineStr + "\n"
                + "- Account Number: " + accountNumber + "\n" + "- New Balance: " + newBalanceStr + "\n\n"
                + "Thank you for banking with us.\n" + "Best regards,\n" + "Your Bank";
    }

    /**
     * Required standard message format:
     *
     * Hello John Doe, We would like to notify you of a recent transaction on your
     * account. Transaction Details: - Amount: $500.00 - Account Number: 1234 - New
     * Balance: $1500.00
     *
     * Thank you for banking with us. Best regards, Your Bank
     */
    public String generateAlertMessage(Customer customer, Account account, double amount) {

        String customerName = safe(customer.getName());
        String amountStr = "$" + MONEY.format(amount);
        String accountNumber = String.valueOf(account.getACCOUNT_ID());
        String newBalanceStr = "$" + MONEY.format(account.getBalance());

        return "Hello " + customerName + ", We would like to notify you of a recent transaction on your account.\n"
                + "Transaction Details:\n" + "- Amount: " + amountStr + "\n" + "- Account Number: " + accountNumber
                + "\n" + "- New Balance: " + newBalanceStr + "\n\n" + "Thank you for banking with us.\n"
                + "Best regards,\n" + "Your Bank";
    }

    private String safe(String value) {
        return value == null ? "" : value.trim();
    }
}