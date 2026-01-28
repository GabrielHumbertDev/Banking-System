package com.github.gabrielhumbertdev.model.account;


public abstract class Account {

    protected double balance;

    private static long nextAccountId = 1000;
    private final long ACCOUNT_ID;

    public Account() {
        this.ACCOUNT_ID = nextAccountId;
        nextAccountId += 5;
    }

    public long getACCOUNT_ID() {
        return ACCOUNT_ID;
    }

    public double getBalance() {
        return balance;
    }

    // Specification asked for setter for all
    public void setBalance(double balance) {
        this.balance = balance;
    }

    // helper: must be positive AND finite
    protected boolean isPositiveAmount(double amount) {
        return Double.isFinite(amount) && amount > 0;
    }

    // deposit blocks <= 0
    public void deposit(double amount) {
        if (!isPositiveAmount(amount)) {
            return;
        }
        balance += amount;
    }

    // withdraw blocks <= 0 (returns 0 for invalid request)
    public double withdraw(double amount) {
        if (!isPositiveAmount(amount)) {
            return 0;
        }
        balance -= amount; // base Account can overdraw
        return amount;
    }

    // Correction can be allowed to set any value, but should still be finite
    public void correctBalance(double amount) {
        if (!Double.isFinite(amount)) {
            return;
        }
        balance = amount;
    }
}
