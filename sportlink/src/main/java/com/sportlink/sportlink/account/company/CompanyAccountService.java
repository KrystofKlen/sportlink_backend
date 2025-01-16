package com.sportlink.sportlink.account.company;

import com.sportlink.sportlink.account.account.Account;
import com.sportlink.sportlink.account.account.AccountService;
import com.sportlink.sportlink.account.account.I_AccountRepository;
import com.sportlink.sportlink.account.ROLE;
import com.sportlink.sportlink.redis.RedisService;
import com.sportlink.sportlink.security.EncryptionUtil;
import com.sportlink.sportlink.utils.DTO_Adapter;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class CompanyAccountService extends AccountService {

    private final DTO_Adapter adapter;

    public CompanyAccountService(
            I_AccountRepository accountRepository,
            RedisService redisService,
            EncryptionUtil.SaltGenerator saltGenerator,
            PasswordEncoder passwordEncoder, DTO_Adapter adapter) {

        // Call the superclass constructor with all required dependencies
        super(accountRepository, redisService, saltGenerator, passwordEncoder, adapter);
        this.adapter = adapter;
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
