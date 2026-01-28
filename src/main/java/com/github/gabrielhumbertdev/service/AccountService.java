package com.github.gabrielhumbertdev.service;

import java.util.List;

import com.github.gabrielhumbertdev.model.account.Account;

public interface AccountService {

    List<Account> getAccounts();

    void removeAccount(Account account);

    Account createAccount(Account account);
}
