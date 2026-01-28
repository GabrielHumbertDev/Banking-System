package com.github.gabrielhumbertdev.model.customer;


import com.github.gabrielhumbertdev.model.account.Account;

public class Person extends Customer {

    public Person(String name, String address) {
        super(name, address);
    }

    // Person: subtract the amount from every account

    @Override
    public void chargeAllAccounts(double amount) {
        if (!Double.isFinite(amount) || amount <= 0) {
            return;
        }
        for (Account a : getAccounts()) {
            a.withdraw(amount);
        }
    }
}
