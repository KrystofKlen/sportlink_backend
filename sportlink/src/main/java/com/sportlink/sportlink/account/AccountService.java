package com.sportlink.sportlink.account;

import com.sportlink.sportlink.redis.RedisService;
import com.sportlink.sportlink.security.EncryptionUtil;
import com.sportlink.sportlink.utils.ImgService;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AccountService {

    public final I_AccountRepository accountRepository;
    private final RedisService redisService;
    private final EncryptionUtil.SaltGenerator saltGenerator;
    private final PasswordEncoder passwordEncoder;

    // Create or Update UserAccount
    @Transactional
    public Account save(Account account) {
        return accountRepository.save(account);
    }

    // Find UserAccount by ID
    public Optional<Account> findAccountById(Long id) {
        return accountRepository.findById(id);
    }

    public Optional<Account> findAccountByEmail(String email) {
        return accountRepository.findByEmail(email);
    }

    @Transactional
    public boolean changePassword(String userToken, String otp, String newPassword, String loginEmail) {
        String expectedOTP = redisService.getValue(userToken);
        if (!expectedOTP.equals(otp)) {
            return false;
        }

        Optional<Account> account = accountRepository.findByEmail(loginEmail);
        if (account.isEmpty()) {
            return false;
        }

        String salt = saltGenerator.generateSalt();
        String saltedPassword = passwordEncoder.encode(newPassword + salt);
        account.get().setSalt(salt);
        account.get().setPassword(saltedPassword);
        return true;
    }


    // Delete Account by ID
    @Transactional
    public void deleteAccount(Long id) {
        accountRepository.deleteById(id);
    }

    @Transactional
    public boolean setProfileImgUUID(long accountId, MultipartFile image) {
        String filename = UUID.randomUUID().toString() + "jpg";
        if (image == null) {
            filename = "default.jpg";
        }
        boolean saved = ImgService.saveImage("DIR", filename, image);
        if (!saved) {
            return false;
        }
        Account account = accountRepository.findById(accountId).orElseThrow();
        account.setProfilePicUUID(filename);
        accountRepository.save(account);
        return true;
    }

}
