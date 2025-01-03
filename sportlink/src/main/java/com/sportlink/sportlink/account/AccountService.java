package com.sportlink.sportlink.account;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class AccountService {
    
    public final I_AccountRepository accountRepository;

    public AccountService(I_AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }
    
    // Create or Update UserAccount
    @Transactional
    public Account save(Account Account) {
        return accountRepository.save(Account);
    }

    // Find UserAccount by ID
    public Optional<Account> findAccountById(Long id) {
        return accountRepository.findById(id);
    }

    // Find all Accounts
    public List<Account> findAllAccounts() {
        return accountRepository.findAll();
    }

    // Delete Account by ID
    @Transactional
    public void deleteAccount(Long id) {
        accountRepository.delete(id);
    }
}
