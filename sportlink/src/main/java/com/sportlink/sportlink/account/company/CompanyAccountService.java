package com.sportlink.sportlink.account.company;

import com.sportlink.sportlink.account.Account;
import com.sportlink.sportlink.account.AccountService;
import com.sportlink.sportlink.account.I_AccountRepository;
import com.sportlink.sportlink.account.ROLE;
import com.sportlink.sportlink.redis.RedisService;
import com.sportlink.sportlink.security.EncryptionUtil;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class CompanyAccountService extends AccountService {

    public CompanyAccountService(
            I_AccountRepository accountRepository,
            RedisService redisService,
            EncryptionUtil.SaltGenerator saltGenerator,
            PasswordEncoder passwordEncoder) {

        // Call the superclass constructor with all required dependencies
        super(accountRepository, redisService, saltGenerator, passwordEncoder);
    }

    @Transactional
    public CompanyAccount updateAccount(Long id, CompanyAccount accountDetails) {
        Optional<Account> account = accountRepository.findById(id);
        if (account.isPresent()) {
            Account existingAccount = account.get();
            if(existingAccount.getRole().equals(ROLE.USER)){
                accountRepository.deleteById(existingAccount.getId());
            }
            // TODO
            return (CompanyAccount) accountRepository.save(existingAccount);
        }
        throw new EntityNotFoundException("Account not found");
    }
}
