package account;

import com.sportlink.sportlink.SportlinkApplication;
import com.sportlink.sportlink.account.ROLE;
import com.sportlink.sportlink.account.account.*;
import com.sportlink.sportlink.security.EncryptionUtil;
import com.sportlink.sportlink.security.JwtService;
import com.sportlink.sportlink.utils.DTO_Adapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = SportlinkApplication.class)
@Transactional // Rolls back changes after each test
public class AuthServiceIT {
    @Autowired
    private AuthService authService;

    @Autowired
    private I_AccountRepository accountRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private DTO_Adapter adapter;
    @Autowired
    private PasswordEncoder passwordEncoder;

    Account account;

    @BeforeEach
    public void setUp() {
        account = new Account();
        account.setUsername("testUser");
        account.setLoginEmail("test@example.com");
        String passwd = passwordEncoder.encode("testPassword");
        account.setPassword(passwd); // Assume BCryptEncoder or plain for test
        account.setRole(ROLE.ROLE_USER);
        account.setStatus(ACCOUNT_STATUS.ACTIVE);
        account = accountRepository.save(account);
    }

    @Test
    void testLogin_successfulWithUsername() {
        DTO_LoginResponse response = authService.login("testUser", "testPassword");
        // Assert
        assertNotNull(response);
        assertNotNull(response.getAccessToken());
        assertNotNull(response.getRefreshToken());
        assertEquals(account.getId(), response.getAccountId());
    }

    @Test
    void testLogin_successfulWithEmail() {
        DTO_LoginResponse response = authService.login("test@example.com", "testPassword");
        // Assert
        assertNotNull(response);
        assertNotNull(response.getAccessToken());
        assertNotNull(response.getRefreshToken());
        assertEquals(account.getId(), response.getAccountId());
    }

    @Test
    void testLoginWithBlockedStatus() {
        account.setStatus(ACCOUNT_STATUS.BANNED);
        account = accountRepository.save(account);
        try {
            authService.login("testUser", "testPassword");
            fail();
        }catch (LockedException ignored) {
        }
    }

    @Test
    void testLogin_invalidCredentials() {
        assertThrows(BadCredentialsException.class, () -> authService.login("testUser", "wrongPassword"));
    }

    @Test
    void testGetNewAccessToken_successful() {
        DTO_Account dtoAccount = adapter.getDTO_Account(account);
        String refreshToken = jwtService.generateToken(dtoAccount, JwtService.REFRESH_TOKEN_EXP);
        // Act: Generate a new access token
        String newAccessToken = authService.getNewAccessToken(account.getId(), refreshToken);
        // Assert
        assertNotNull(newAccessToken);
    }

    @Test
    void testGetNewAccessToken_invalidToken() {
        String invalidRefreshToken = "invalidToken";
        // Act & Assert: Invalid token should throw an exception
        assertThrows(IllegalArgumentException.class, () -> authService.getNewAccessToken(account.getId(), invalidRefreshToken));
    }

    @Test
    void testGetNewAccessToken_adminAccount() {
        // Arrange: Create an admin user in the database
        Account adminAccount = new Account();
        adminAccount.setUsername("adminUser");
        adminAccount.setLoginEmail("admin@example.com");
        adminAccount.setPassword("{noop}adminPassword");
        adminAccount.setRole(ROLE.ROLE_ADMIN);
        adminAccount.setStatus(ACCOUNT_STATUS.ACTIVE);
        accountRepository.save(adminAccount);

        DTO_Account dtoAccount = adapter.getDTO_Account(adminAccount);
        String refreshToken = jwtService.generateToken(dtoAccount, JwtService.REFRESH_TOKEN_EXP);

        // Act & Assert: Refresh token usage should not be allowed for admin accounts
        assertThrows(IllegalArgumentException.class, () -> authService.getNewAccessToken(adminAccount.getId(), refreshToken));
    }
}
