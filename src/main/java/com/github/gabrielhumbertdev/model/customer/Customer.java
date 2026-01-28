package com.github.gabrielhumbertdev.model.customer;


import java.util.ArrayList;
import java.util.List;

import com.github.gabrielhumbertdev.model.account.Account;

public abstract class Customer {

    // Email field for sending transaction alerts (Sprint 3 Week 1 OOD4)
    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    private final long CUSTOMER_ID;
    private static long nextCustomerId = 2_000_000;

    private String name;
    private String address;
    private List<Account> accounts = new ArrayList<>();

    public Customer(String name, String address) {

        // Secondary validation (lightweight, same rules)
        // This protects your domain model in case someone tries:
        // new Person(null, "x") or new Company(" ", " ")
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Customer name cannot be null or empty");
        }
        if (address == null || address.trim().isEmpty()) {
            throw new IllegalArgumentException("Customer address cannot be null or empty");
        }

        this.CUSTOMER_ID = nextCustomerId;
        nextCustomerId += 7;

        // Store trimmed values (prevents names like " Gabriel ")
        this.name = name.trim();
        this.address = address.trim();
    }

    public long getCUSTOMER_ID() {
        return CUSTOMER_ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        // Optional: also protect setters (prevents setting invalid values later)
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Customer name cannot be null or empty");
        }
        this.name = name.trim();
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        // Optional: also protect setters (prevents setting invalid values later)
        if (address == null || address.trim().isEmpty()) {
            throw new IllegalArgumentException("Customer address cannot be null or empty");
        }
        this.address = address.trim();
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    // Spec asked for setter for all
    public void setAccounts(List<Account> accounts) {
        this.accounts = accounts;
    }

    public void addAccount(Account account) {
        if (account == null) {
            throw new IllegalArgumentException("Account cannot be null");
        }
        accounts.add(account);
    }

    public void removeAccount(Account account) {
        accounts.remove(account);
    }

    // Behaviour differs between Person and Company
    public abstract void chargeAllAccounts(double amount);
}