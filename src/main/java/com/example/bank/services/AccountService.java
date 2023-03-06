package com.example.bank.services;

import com.example.bank.entities.Account;
import com.example.bank.exceptions.AccountAlreadyExistsException;
import com.example.bank.exceptions.AccountAlreadyLockedException;
import com.example.bank.exceptions.AccountNotFoundException;
import com.example.bank.exceptions.InDBQueryFailedException;
import com.example.bank.interceptor.RequestInterceptor;
import com.example.bank.repositories.AccountRepository;
import jakarta.persistence.LockModeType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional
@Service
public class AccountService {

    private final Logger LOG = LoggerFactory.getLogger(RequestInterceptor.class);
    @Autowired
    private AccountRepository accountRepository;
    public Account addAccount(Account account) {
        if (accountRepository.getAccountByNameAndUserId(account.getName(), account.getUserId()).isPresent()){
            throw new AccountAlreadyExistsException();
        }
        return accountRepository.saveAndFlush(account);
    }
    public Account getByName(String name) {
        return accountRepository.findAccountByName(name);
    }
//    public Account editAccount(Account account) {
//        return accountRepository.saveAndFlush(account);
//    }
    public List<Optional<Account>> findAccountsOfUser(int currentUserId) {
        List<Optional<Account>> accounts = accountRepository.findAccountsOfUser(currentUserId);
        if (accounts.isEmpty()){ throw new AccountNotFoundException(); }
        return accounts;
    }
//    @Transactional
    public int lockAccount(String accountName, int user_id){
        Optional<Account> accountToLock = accountRepository.getAccountByNameAndUserId(accountName, user_id);
        LOG.info("AccountService.lockAccount: accountToLock.isEmpty=" + accountToLock.isEmpty() + "; accountToLock.get().getStatus()=" + (accountToLock.isEmpty() ? "accountToLock.isEmpty" : accountToLock.get().getStatus()));
        if (accountToLock.isEmpty()){
            throw new AccountNotFoundException();
        } else if (accountToLock.get().getStatus()=="C") {
            throw new AccountAlreadyLockedException();
        }
        int result = accountRepository.lockAccount(accountName, user_id);
        if (result!=1){
            throw new InDBQueryFailedException();
        }
        return result;
    }

    public Optional<Account> getAccountByNameAndUserId(String accountName, int user_id) {
        Optional<Account> returnAccount = accountRepository.getAccountByNameAndUserId(accountName, user_id);
        if (returnAccount.isEmpty()){
            throw new AccountNotFoundException(); }
        return returnAccount;
    }

}
