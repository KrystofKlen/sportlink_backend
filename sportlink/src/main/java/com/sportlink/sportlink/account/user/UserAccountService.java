package com.sportlink.sportlink.account.user;

import com.sportlink.sportlink.account.account.Account;
import com.sportlink.sportlink.account.account.AccountService;
import com.sportlink.sportlink.account.account.I_AccountRepository;
import com.sportlink.sportlink.currency.Currency;
import com.sportlink.sportlink.redis.RedisService;
import com.sportlink.sportlink.security.EncryptionUtil;
import com.sportlink.sportlink.utils.DTO_Adapter;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class UserAccountService extends AccountService {

    private final I_AccountRepository accountRepository;
    private final DTO_Adapter adapter;

    public UserAccountService(
            I_AccountRepository accountRepository,
            RedisService redisService,
            EncryptionUtil.SaltGenerator saltGenerator,
            PasswordEncoder passwordEncoder,
            DTO_Adapter adapter
    ) {
        // Call the super constructor with the required dependencies for AccountService
        super(accountRepository, redisService, saltGenerator, passwordEncoder,adapter);

        this.accountRepository = accountRepository;
        this.adapter = adapter;
    }

    public Optional<UserAccount> getUserAccountByUserId(Long userId){
        try{
            UserAccount user = (UserAccount) accountRepository.findById(userId).get();
            return Optional.of(user);
        }catch (EntityNotFoundException | NoSuchElementException e){
            return Optional.empty();
        }
    }

    public Optional<DTO_UserAccount> findByEmail(String email){
        Optional<Account> account =  accountRepository.findByEmail(email);
        if(account.isEmpty()){
            return Optional.empty();
        }
        UserAccount user = (UserAccount) account.get();
        return Optional.of(adapter.getDTO_UserAccount(user));
    }

    public Optional<DTO_UserAccount> findByUsername(String username){
        Optional<Account> user =  accountRepository.findByUsername(username);
        if(user.isPresent()){
            UserAccount userAccount = (UserAccount) user.get();
            return Optional.of(adapter.getDTO_UserAccount(userAccount));
        }else {
            return Optional.empty();
        }
    }

    // Update Account
    @Transactional
    public UserAccount updateAccount(Long id, UserAccount accountDetails) {
        Optional<Account> account = accountRepository.findById(id);
        if (account.isPresent()) {

            String loginEmail = accountDetails.getLoginEmail();
            String username = accountDetails.getUsername();
            String password = accountDetails.getPassword();
            String salt = accountDetails.getSalt();
            String firstName = accountDetails.getFirstName();
            String lastName = accountDetails.getLastName();
            Date dateOfBirth = accountDetails.getDateOfBirth();

            UserAccount existingAccount = (UserAccount) account.get();

            if(loginEmail != null){
                existingAccount.setLoginEmail(loginEmail);
            }
            if(username != null){
                existingAccount.setUsername(username);
            }
            if(password != null){
                existingAccount.setPassword(password);
            }
            if(salt != null){
                existingAccount.setSalt(salt);
            }
            if(firstName != null){
                existingAccount.setFirstName(firstName);
            }
            if(lastName != null){
                existingAccount.setLastName(lastName);
            }
            if(dateOfBirth != null){
                existingAccount.setDateOfBirth(dateOfBirth);
            }

            return (UserAccount) accountRepository.save(existingAccount);
        }else {
            throw new EntityNotFoundException("Given account does not exist");
        }
    }

    public Map<String, Integer> getBalance(UserAccount account) {
        Map<Currency,Integer> balance = account.getBalance();
        return adapter.getDTO_Balance(balance);
    }
}
