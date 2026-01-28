package com.github.gabrielhumbertdev.service;


import java.util.List;

import com.github.gabrielhumbertdev.dao.AccountReaderDAO;
import com.github.gabrielhumbertdev.dao.AccountWriterDAO;
import com.github.gabrielhumbertdev.model.account.Account;

public class AccountServiceImpl implements AccountService {

    private AccountReaderDAO accReaderDao;
    private AccountWriterDAO accWriterDao;

    public AccountServiceImpl(AccountReaderDAO accReaderDao, AccountWriterDAO accWriterDao) {
        this.accReaderDao = accReaderDao;
        this.accWriterDao = accWriterDao;
    }

    @Override
    public List<Account> getAccounts() {
        return accReaderDao.readAccounts();
    }

    @Override
    public void removeAccount(Account account) {
        accWriterDao.deleteAccount(account);
    }

    @Override
    public Account createAccount(Account account) {
        return accWriterDao.createAccount(account);
    }
}
