package com.github.gabrielhumbertdev.dao;

import com.github.gabrielhumbertdev.model.account.Account;

public interface AccountWriterDAO {

    Account createAccount(Account account);

    void deleteAccount(Account account);
}
