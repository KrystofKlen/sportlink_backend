package com.sportlink.sportlink.account;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class CompanyAccountService extends AccountService{

    public CompanyAccountService(I_AccountRepository accountRepository) {
        super(accountRepository);
    }

    @Transactional
    public CompanyAccount updateAccount(Long id, CompanyAccount accountDetails) {
        Optional<Account> account = accountRepository.findById(id);
        if (account.isPresent()) {
            Account existingAccount = account.get();
            if(existingAccount.getRole().equals(ROLE.USER)){
                accountRepository.delete(existingAccount.getId());
            }
            // TODO
            return (CompanyAccount) accountRepository.save(existingAccount);
        }
        throw new EntityNotFoundException("Account not found");
    }
}
