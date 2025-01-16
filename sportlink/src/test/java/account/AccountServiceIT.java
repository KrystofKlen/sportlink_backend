package account;

import com.sportlink.sportlink.SportlinkApplication;
import com.sportlink.sportlink.account.account.Account;
import com.sportlink.sportlink.account.account.AccountService;
import com.sportlink.sportlink.account.account.I_AccountRepository;
import com.sportlink.sportlink.redis.RedisService;
import com.sportlink.sportlink.security.EncryptionUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = SportlinkApplication.class)
@Transactional
public class AccountServiceIT {

    @Autowired
    private AccountService accountService;

    @Autowired
    private I_AccountRepository accountRepository;

    @Autowired
    private RedisService redisService;

    @Autowired
    private EncryptionUtil.SaltGenerator saltGenerator;

    private PasswordEncoder passwordEncoder;

    @BeforeEach
    public void setUp() {
        passwordEncoder = new BCryptPasswordEncoder();
    }

    @Test
    void testSaveAndFindAccount() {
        Account account = new Account();
        account.setLoginEmail("test@example.com");
        account.setPassword("password123");

        // Save the account
        Account savedAccount = accountService.save(account);

        // Find the account by ID
        Optional<Account> fetchedAccount = accountService.findAccountById(savedAccount.getId());

        assertThat(fetchedAccount).isPresent();
        assertThat(fetchedAccount.get().getLoginEmail()).isEqualTo("test@example.com");
    }

    @Test
    void testChangePassword() {
        String email = "test@example.com";
        String otp = "123456";
        String userToken = "user123";
        String newPassword = "newPassword123";

        redisService.saveValueWithExpiration(userToken, otp, 1); // Simulating OTP storage

        Account account = new Account();
        account.setLoginEmail(email);
        account.setPassword("oldPassword123");
        accountRepository.save(account);

        boolean passwordChanged = accountService.changePassword(userToken, otp, newPassword, email);
        assertTrue(passwordChanged);

        Optional<Account> updatedAccount = accountService.findAccountByEmail(email);
        assertThat(updatedAccount).isPresent();

        String saltedPassword = updatedAccount.get().getPassword();
        String salt = updatedAccount.get().getSalt();

        assertTrue(passwordEncoder.matches(newPassword + salt, saltedPassword));
    }

    @Test
    void testDeleteAccount() {
        Account account = new Account();
        account.setLoginEmail("delete@example.com");
        account.setPassword("password123");

        Account savedAccount = accountService.save(account);
        Long accountId = savedAccount.getId();

        accountService.deleteAccount(accountId);

        Optional<Account> deletedAccount = accountService.findAccountById(accountId);
        assertThat(deletedAccount).isEmpty();
    }
}
