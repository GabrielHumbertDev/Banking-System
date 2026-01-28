package com.github.gabrielhumbertdev.dao;

import java.util.List;

import com.github.gabrielhumbertdev.model.account.Account;

public interface AccountReaderDAO {
    List<Account> readAccounts();
}
