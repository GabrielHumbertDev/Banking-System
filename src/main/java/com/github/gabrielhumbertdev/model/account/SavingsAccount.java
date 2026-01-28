package com.github.gabrielhumbertdev.model.account;


public class SavingsAccount extends Account {

    private double interestRate;

    public double getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(double interestRate) {
        this.interestRate = interestRate;
    }

    // Savings: no overdraft allowed

    public double withdraw(double amount) {
        if (!isPositiveAmount(amount)) {
            return 0;
        }
        if (amount > balance) {
            return 0;
        }
        balance -= amount;
        return amount;
    }

    public void addInterest() {
        if (!Double.isFinite(interestRate)) {
            return;
        }
        double interestDue = balance * interestRate / 100.0;
        if (Double.isFinite(interestDue)) {
            balance += interestDue;
        }
    }
}
