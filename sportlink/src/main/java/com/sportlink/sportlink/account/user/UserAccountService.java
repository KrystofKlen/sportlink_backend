package com.sportlink.sportlink.account;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class UserAccountService extends AccountService{

    public UserAccountService(I_AccountRepository accountRepository) {
        super(accountRepository);
    }

    public Optional<UserAccount> getUserAccountByUserId(Long userId){
        try{
            UserAccount user = (UserAccount) accountRepository.findById(userId).get();
            return Optional.of(user);
        }catch (EntityNotFoundException | NoSuchElementException e){
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
            String profilePic = accountDetails.getProfilePic();

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
            if(profilePic != null){
                existingAccount.setProfilePic(profilePic);
            }

            return (UserAccount) accountRepository.save(existingAccount);
        }else {
            throw new EntityNotFoundException("Given account does not exist");
        }
    }
}
