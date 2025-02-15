package com.sportlink.sportlink.account.account;

import com.sportlink.sportlink.account.ROLE;
import com.sportlink.sportlink.redis.RedisService;
import com.sportlink.sportlink.security.EncryptionUtil;
import com.sportlink.sportlink.security.JwtService;
import com.sportlink.sportlink.security.SecurityUtils;
import com.sportlink.sportlink.utils.DTO_Adapter;
import com.sportlink.sportlink.utils.ImgService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

@Service
@AllArgsConstructor
@Slf4j
public class AccountService {

    public final I_AccountRepository accountRepository;
    private final RedisService redisService;
    private final PasswordEncoder passwordEncoder;
    private final DTO_Adapter adapter;
    private final ImgService imgService;

    public final static int PASSWD_RESET_MINUTES_BAN = 30;

    // Create or Update UserAccount
    @Transactional
    public Account save(Account account) {
        Account acc = accountRepository.save(account);
        log.info("New account added: " + acc);
        return acc;
    }

    // Find UserAccount by ID
    public Optional<Account> findAccountById(Long id) {
        return accountRepository.findById(id);
    }

    public Optional<DTO_Account> findDTOAccountByUsername(String username) {
        Optional<Account> account = accountRepository.findByUsername(username);
        if (account.isEmpty()) {
            return Optional.empty();
        }
        Account ac = account.get();
        return Optional.of( adapter.getDTO_Account(ac) );
    }

    public Optional<Account> findAccountByEmail(String email) {
        return accountRepository.findByEmail(email);
    }

    @Transactional
    public boolean changePassword(String userToken, String otp, String newPassword) {
        String expectedOTP = redisService.getValue(userToken);
        if (!expectedOTP.equals(otp)) {
            return false;
        }
        redisService.deleteValue(userToken);

        String loginEmail = redisService.getValue(otp);
        redisService.deleteValue(otp);

        String record = redisService.getPasswdResetRecordKey(loginEmail);
        boolean hasRecord = redisService.hasKey(record);
        if (hasRecord) {
            return false;
        }
        redisService.saveValueWithExpiration(record,"ban",PASSWD_RESET_MINUTES_BAN);

        Optional<Account> account = accountRepository.findByEmail(loginEmail);
        if (account.isEmpty()) {
            return false;
        }

        String encoded = passwordEncoder.encode(newPassword);
        account.get().setPassword(encoded);
        log.info("Password changed: accountId = " + account.get().getId());
        return true;
    }


    // Delete Account by ID
    @Transactional
    public void deleteAccount(Long id) {
        accountRepository.deleteById(id);
        log.info("Account deleted: " + id);
    }

    @Transactional
    public boolean setProfileImgUUID(long accountId, MultipartFile image) {
        String filename = UUID.randomUUID().toString() + "jpg";
        if (image == null) {
            filename = "default.jpg";
        }
        boolean saved = imgService.saveImage(imgService.PATH_ACCOUNT, filename, image);
        if (!saved) {
            return false;
        }
        Account account = accountRepository.findById(accountId).orElseThrow();
        account.setProfilePicUUID(filename);
        accountRepository.save(account);
        return true;
    }
}
