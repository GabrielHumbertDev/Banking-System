package com.github.gabrielhumbertdev.model.organisation;

import com.github.gabrielhumbertdev.model.account.Account;
import com.github.gabrielhumbertdev.model.account.SavingsAccount;
import com.github.gabrielhumbertdev.model.customer.Customer;

public class Company extends Customer {

    public Company(String name, String address) {
        super(name, address);
    }

    // Company rules: - CheckingAccount: subtract amount - SavingsAccount: subtract
    // amount * 2

    @Override
    public void chargeAllAccounts(double amount) {
        if (!Double.isFinite(amount) || amount <= 0) {
            return;
        }
        for (Account a : getAccounts()) {
            if (a instanceof SavingsAccount) {
                a.withdraw(amount * 2);
            } else {
                a.withdraw(amount);
            }
        }
    }

}
