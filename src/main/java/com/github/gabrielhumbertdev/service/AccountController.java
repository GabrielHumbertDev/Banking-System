package com.github.gabrielhumbertdev.service;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.github.gabrielhumbertdev.model.account.Account;
import com.github.gabrielhumbertdev.model.account.CheckingAccount;
import com.github.gabrielhumbertdev.model.account.SavingsAccount;
import com.github.gabrielhumbertdev.model.customer.Customer;
import com.github.gabrielhumbertdev.model.customer.Person;
import com.github.gabrielhumbertdev.model.organisation.Company;


public class AccountController {

    private List<Customer> customers = new ArrayList<>();
    private List<Account> accounts = new ArrayList<>();

    // ============================
    // Question 2: Customer email IDs + Transaction Alerts (deposit/withdraw)
    // ============================
    // This controller sends transaction alerts by calling AlertService after
    // deposit/withdraw.
    private final AlertService alertService = new AlertService(customers, accounts);

    // Return unmodifiable view (protects internal state)
    public List<Customer> getCustomers() {
        return Collections.unmodifiableList(customers);
    }

    public void setCustomers(List<Customer> customers) {
        this.customers = customers;
    }

    // Return unmodifiable view (protects internal state)
    public List<Account> getAccounts() {
        return Collections.unmodifiableList(accounts);
    }

    public void setAccounts(List<Account> accounts) {
        this.accounts = accounts;
    }

    // Helper: validation for String inputs
    private boolean isNullOrBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    // Story 1: create customer
    public Customer createCustomer(String name, String address, String type) {

        if (isNullOrBlank(name)) {
            throw new IllegalArgumentException("Customer name cannot be null or empty");
        }
        if (isNullOrBlank(address)) {
            throw new IllegalArgumentException("Customer address cannot be null or empty");
        }
        if (isNullOrBlank(type)) {
            throw new IllegalArgumentException("Customer type cannot be null or empty");
        }

        type = type.trim();

        if (type.equalsIgnoreCase("person")) {
            Person p = new Person(name.trim(), address.trim());
            customers.add(p);
            return p;
        }

        if (type.equalsIgnoreCase("company")) {
            Company c = new Company(name.trim(), address.trim());
            customers.add(c);
            return c;
        }

        throw new IllegalArgumentException("Unknown customer type: " + type);
    }

    // Story 2: remove customer + remove all their accounts
    public void removeCustomer(Customer customer) {
        if (customer == null) {
            return;
        }

        for (Account acc : new ArrayList<>(customer.getAccounts())) {
            accounts.remove(acc);
        }

        // NOTE: you chose to keep this for now (can be revisited later)
        customer.getAccounts().clear();

        customers.remove(customer);
    }

    // Story 3: create account
    public Account createAccount(Customer customer, String type) {
        if (customer == null) {
            throw new IllegalArgumentException("Customer cannot be null");
        }
        if (type == null) {
            throw new IllegalArgumentException("Account type cannot be null");
        }

        type = type.trim();

        if (type.equalsIgnoreCase("checking")) {
            CheckingAccount ca = new CheckingAccount();
            accounts.add(ca);
            customer.addAccount(ca);
            return ca;
        }

        if (type.equalsIgnoreCase("savings")) {
            SavingsAccount sa = new SavingsAccount();
            accounts.add(sa);
            customer.addAccount(sa);
            return sa;
        }

        throw new IllegalArgumentException("Unknown account type: " + type);
    }

    // Story 4: remove account
    public void removeAccount(Account account) {
        if (account == null) {
            throw new IllegalArgumentException("Account cannot be null");
        }

        accounts.remove(account);

        for (Customer customer : customers) {
            customer.removeAccount(account);
        }
    }

    // ============================
    // Question 2: Deposit transaction + email alert
    // ============================
    public void deposit(Customer customer, Account account, double amount) {
        if (customer == null) {
            throw new IllegalArgumentException("Customer cannot be null");
        }
        if (account == null) {
            throw new IllegalArgumentException("Account cannot be null");
        }

        account.deposit(amount);

        // Question 2: Email alert for deposit
        alertService.sendTransactionAlert(customer, account, amount);
    }

    // ============================
    // Question 2: Withdraw transaction + email alert
    // Question 3C: If Checking violates minimum balance -> fine + email alert
    // ============================
    public void withdraw(Customer customer, Account account, double amount) {
        if (customer == null) {
            throw new IllegalArgumentException("Customer cannot be null");
        }
        if (account == null) {
            throw new IllegalArgumentException("Account cannot be null");
        }

        double withdrawn = account.withdraw(amount);

        // Question 2: Email alert for withdraw (only if withdraw succeeded)
        if (withdrawn > 0) {
            alertService.sendTransactionAlert(customer, account, -withdrawn);
        }

        // Question 3C: Apply minimum balance fine + send fine alert (Checking only)
        if (account instanceof CheckingAccount) {
            CheckingAccount checking = (CheckingAccount) account;

            double fine = checking.applyMinimumBalanceFineIfNeeded();
            if (fine > 0) {
                alertService.sendMinimumBalanceFineAlert(customer, checking, fine, checking.getMinimumBalance());
            }
        }
    }
}