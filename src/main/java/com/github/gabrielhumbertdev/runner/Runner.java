package com.github.gabrielhumbertdev.runner;


import com.github.gabrielhumbertdev.model.account.Account;
import com.github.gabrielhumbertdev.model.account.CheckingAccount;
import com.github.gabrielhumbertdev.model.account.SavingsAccount;
import com.github.gabrielhumbertdev.model.customer.Customer;
import com.github.gabrielhumbertdev.model.customer.Person;
import com.github.gabrielhumbertdev.model.organisation.Company;
import com.github.gabrielhumbertdev.service.AccountController;

public class Runner {

    public static void main(String[] args) {
        // customerIdsStartAt2MAndStepBy7
        System.out.println("customerIdsStartAt2MAndStepBy7");
        Customer cust1 = new Person("James", "Leeds St");
        Customer cust2 = new Company("FDM", "10 Whitehall");
        System.out.println("Expected 2_000_000L   actual " + cust1.getCUSTOMER_ID());
        System.out.println("Expected 2_000_007L   actual " + cust2.getCUSTOMER_ID());
        System.out.println("--------------------------------------------------------");

        // accountIdsStartAt1000AndStepBy5
        System.out.println("accountIdsStartAt1000AndStepBy5");
        Account acc1 = new CheckingAccount();
        Account acc2 = new SavingsAccount();
        System.out.println("Expected 1_000L, actual " + acc1.getACCOUNT_ID());
        System.out.println("Expected 1_005L, actual " + acc2.getACCOUNT_ID());
        System.out.println("--------------------------------------------------------");

        // checkingDepositAndWithdrawAllowOverdraft
        System.out.println("checkingDepositAndWithdrawAllowOverdraft");
        CheckingAccount checking = new CheckingAccount();
        checking.deposit(100);
        System.out.println("Expected 100.0, actual " + checking.getBalance());

        double taken = checking.withdraw(40);
        System.out.println("Expected 40.0, actual " + taken);
        System.out.println("Expected 60.0, actual " + checking.getBalance());

        checking.withdraw(120); // overdraft allowed
        System.out.println("Expected -60.0, actual " + checking.getBalance());
        System.out.println("--------------------------------------------------------");

        // savingsWithdrawBlocksOverdraft
        System.out.println("savingsWithdrawBlocksOverdraft");
        SavingsAccount savingsAccount1 = new SavingsAccount();
        savingsAccount1.deposit(50);

        double withdrawn = savingsAccount1.withdraw(80); // too much
        System.out.println("Expected 0.0, actual " + withdrawn);
        System.out.println("Expected 50.0,actual " + savingsAccount1.getBalance());

        withdrawn = savingsAccount1.withdraw(20); // ok
        System.out.println("Expected 20.0, actual " + withdrawn);
        System.out.println("Expected 30.0, actual " + savingsAccount1.getBalance());
        System.out.println("--------------------------------------------------------");

        // savingsAddInterest
        System.out.println("savingsAddInterest");
        SavingsAccount savingsAccount2 = new SavingsAccount();
        savingsAccount2.setInterestRate(5.0); // 5%
        savingsAccount2.deposit(100.0);
        savingsAccount2.addInterest();
        System.out.println("Expected 105.0, actual " + savingsAccount2.getBalance());
        System.out.println("--------------------------------------------------------");

        // checkingNextCheckNumberIncrementsAndSetterWorks
        System.out.println("checkingNextCheckNumberIncrementsAndSetterWorks");
        CheckingAccount checkingAccount2 = new CheckingAccount();
        System.out.println("Expected 1, actual " + checkingAccount2.getNextCheckNumber());
        System.out.println("Expected 2, actual " + checkingAccount2.getNextCheckNumber());

        checkingAccount2.setNextCheckNumber(10);
        System.out.println("Expected 10, actual " + checkingAccount2.getNextCheckNumber());
        System.out.println("Expeted 11, actual " + checkingAccount2.getNextCheckNumber());
        System.out.println("--------------------------------------------------------");

        // controllerCreatesCustomersByType
        System.out.println("controllerCreatesCustomersByType");
        AccountController ctrl1 = new AccountController();

        Customer cust3 = ctrl1.createCustomer("James", "Leeds St", "person");
        Customer cust4 = ctrl1.createCustomer("FDM", "10 Whitehall", "company");

        System.out.println("Expected true, actual " + (cust3 instanceof Person));
        System.out.println("Expected true, actual " + (cust4 instanceof Company));
        System.out.println("Expected 2, actual " + ctrl1.getCustomers().size());
        System.out.println("--------------------------------------------------------");

        // controllerCreatesAccountsAndWiresThemToCustomer
        System.out.println("controllerCreatesAccountsAndWiresThemToCustomer");
        AccountController ctrl2 = new AccountController();
        Customer cust5 = ctrl2.createCustomer("James", "Leeds St", "person");

        Account acc3 = ctrl2.createAccount(cust5, "checking");
        Account acc4 = ctrl2.createAccount(cust5, "savings");

        System.out.println("Expected true, actual " + (acc3 instanceof CheckingAccount));
        System.out.println("Expected true, actual " + (acc4 instanceof SavingsAccount));

        System.out.println("Expected true 2, actual " + ctrl2.getAccounts().size());
        System.out.println("Expected true 2, actual " + cust5.getAccounts().size());
        System.out.println("Expected true, actual " + (cust5.getAccounts().contains(acc3)));
        System.out.println("Expected true, actual " + (cust5.getAccounts().contains(acc4)));
        System.out.println("--------------------------------------------------------");

        // controllerRemoveAccountPurgesFromControllerAndAllCustomers
        System.out.println("controllerRemoveAccountPurgesFromControllerAndAllCustomers");
        AccountController ctrl3 = new AccountController();
        Customer cust6 = ctrl3.createCustomer("James", "Leeds St", "person");
        Customer cust7 = ctrl3.createCustomer("Chris", "Liverpool Rd", "person");

        Account acc5 = ctrl3.createAccount(cust6, "checking");
        cust7.addAccount(acc5); // simulate joint ownership

        System.out.println("Expected 1, actual " + cust6.getAccounts().size());
        System.out.println("Expected 1, actual " + cust7.getAccounts().size());
        System.out.println("Expected 1, actual " + ctrl3.getAccounts().size());

        ctrl3.removeAccount(acc5);

        System.out.println("Expected 0, actual " + ctrl3.getAccounts().size());
        System.out.println("Expected 0, actual " + cust6.getAccounts().size());
        System.out.println("Expected 0, actual " + cust7.getAccounts().size());
        System.out.println("--------------------------------------------------------");

        // controllerRemoveCustomerAlsoDropsTheirAccountsFromController
        System.out.println("controllerRemoveCustomerAlsoDropsTheirAccountsFromController");
        AccountController ctrl4 = new AccountController();

        Customer cust8 = ctrl4.createCustomer("James", "Leeds St", "person");
        Customer cust9 = ctrl4.createCustomer("Chris", "Liverpool Rd", "person");

        Account acc6 = ctrl4.createAccount(cust8, "checking");
        Account acc7 = ctrl4.createAccount(cust9, "savings");

        System.out.println("Expected 2, actual " + ctrl4.getAccounts().size());

        ctrl4.removeCustomer(cust8);

        System.out.println("Expected 1, actual " + ctrl4.getCustomers().size());
        System.out.println("Expected 1,  actual " + ctrl4.getAccounts().size());
        System.out.println("Expected false " + ctrl4.getAccounts().contains(acc6));
        System.out.println("Expeted  true " + ctrl4.getAccounts().contains(acc7));
        System.out.println("--------------------------------------------------------");

        // personChargeAllAccountsChargesEveryAccountByAmount
        System.out.println("personChargeAllAccountsChargesEveryAccountByAmount");
        Person person1 = new Person("James", "Leeds St");
        CheckingAccount chk1 = new CheckingAccount();
        SavingsAccount sav1 = new SavingsAccount();
        chk1.deposit(100);
        sav1.deposit(100);

        person1.addAccount(chk1);
        person1.addAccount(sav1);

        person1.chargeAllAccounts(10.0);

        System.out.println("Expected 90.0, actual " + chk1.getBalance());
        System.out.println("Expected 90.0, actual " + sav1.getBalance());
        System.out.println("--------------------------------------------------------");

        // companyChargeAllAccountsChargesCheckingByAmountAndSavingsByDouble
        System.out.println("companyChargeAllAccountsChargesCheckingByAmountAndSavingsByDouble");

        // companyChargeAllAccountsChargesCheckingByAmountAndSavingsByDouble
        Company company = new Company("ACME", "456 Ave");
        CheckingAccount chk2 = new CheckingAccount();
        SavingsAccount sav2 = new SavingsAccount();
        chk2.deposit(100);
        sav2.deposit(100);

        company.addAccount(chk2);
        company.addAccount(sav2);

        company.chargeAllAccounts(10.0);

        System.out.println("Expected 90.0, actual " + chk2.getBalance());
        System.out.println("Expected 80.0, actual " + sav2.getBalance());
        System.out.println("--------------------------------------------------------");

        System.out.println("EDGE CASES");
        // EDGE CASES
        // instantiating customer with null values
        System.out.println("instantiate customer with null values?");
        try {
            Customer customer = new Person(null, "10 whitehall"); // This will throw an exception
        } catch (IllegalArgumentException e) {
            System.out.println("Error creating customer: " + e.getMessage());
        }
        try {
            Customer customer2 = new Person("James", null); // This will throw an exception
        } catch (IllegalArgumentException e) {
            System.out.println("Error creating customer: " + e.getMessage());
        }
        System.out.println("--------------------------------------------------------");

        // depositing a negative amount into checking
        System.out.println("checking allows user  to deposit a negative amount?");
        CheckingAccount checking2 = new CheckingAccount();

        try {
            checking2.deposit(-100.0); // This should throw an exception
        } catch (IllegalArgumentException e) {
            System.out.println("Deposit failed: " + e.getMessage());
        }
        System.out.println("--------------------------------------------------------");

        // withdraw a negative amount from checking
        System.out.println("checking allows users to withdraw a negative amount?");
        CheckingAccount checking3 = new CheckingAccount();

        try {
            checking3.deposit(100.0);
            checking3.withdraw(-50.0); // This will throw an exception
        } catch (IllegalArgumentException e) {
            System.out.println("Transaction failed: " + e.getMessage());
        }
        System.out.println("--------------------------------------------------------");

        // withdraw a negative amount from savings
        System.out.println("savings allows users to withdraw a negative amount?");
        SavingsAccount savingsAccount3 = new SavingsAccount();
        try {
            savingsAccount3.deposit(100.0);
            savingsAccount3.withdraw(-50.0); // This will throw an exception
        } catch (IllegalArgumentException e) {
            System.out.println("Withdrawal failed: " + e.getMessage());
        }
        System.out.println("--------------------------------------------------------");

        // Set a negative interest rate for savings
        System.out.println("Set a negative interest rate for savings?");
        SavingsAccount savingsAccount4 = new SavingsAccount();

        try {
            savingsAccount4.setInterestRate(-0.05); // This will throw an exception
        } catch (IllegalArgumentException e) {
            System.out.println("Failed to set interest rate: " + e.getMessage());
        }
        System.out.println("--------------------------------------------------------");

        // createCustomer passed null values
        AccountController ctrl5 = new AccountController();
        try {
            // Passing null values to simulate invalid input
            ctrl5.createCustomer(null, "Leed Street", "person");
        } catch (IllegalArgumentException e) {
            System.out.println("Failed to create customer: " + e.getMessage());
        }

        try {
            ctrl5.createCustomer("James", null, "company");
        } catch (IllegalArgumentException e) {
            System.out.println("Failed to create customer: " + e.getMessage());
        }

        try {
            ctrl5.createCustomer("Bob", "Liverpool road", null);
        } catch (IllegalArgumentException e) {
            System.out.println("Failed to create customer: " + e.getMessage());
        }
        System.out.println("--------------------------------------------------------");

        // createAccount passed null values
        AccountController ctrl6 = new AccountController();
        Customer customer3 = new Person("Joe blogg", "Easy street");

        try {
            Account acc8 = ctrl5.createAccount(null, "CheckingAccount");
            System.out.println("Account created successfully: " + acc8);
        } catch (IllegalArgumentException e) {
            System.err.println("Failed to create account: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("An unexpected error occurred: " + e.getMessage());
        }

        try {
            Account acc8 = ctrl5.createAccount(customer3, null);
            System.out.println("Account created successfully: " + acc8);
        } catch (IllegalArgumentException e) {
            System.err.println("Failed to create account: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("An unexpected error occurred: " + e.getMessage());
        }
    }

}
