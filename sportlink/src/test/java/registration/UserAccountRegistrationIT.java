package registration;

import com.sportlink.sportlink.SportlinkApplication;
import com.sportlink.sportlink.account.Account;
import com.sportlink.sportlink.account.AccountService;
import com.sportlink.sportlink.account.company.CompanyAccount;
import com.sportlink.sportlink.account.user.DTO_UserAccount;
import com.sportlink.sportlink.account.user.UserAccountService;
import com.sportlink.sportlink.inventory.user.I_UserInventoryRepository;
import com.sportlink.sportlink.inventory.user.UserInventory;
import com.sportlink.sportlink.redis.RedisService;
import com.sportlink.sportlink.registration.DTO_CompanyRegistration;
import com.sportlink.sportlink.registration.DTO_UserRegistration;
import com.sportlink.sportlink.registration.RegistrationService;
import com.sportlink.sportlink.security.EncryptionUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = SportlinkApplication.class)
public class UserAccountRegistrationIT {
    @Autowired
    private RegistrationService registrationService;

    @Autowired
    private UserAccountService userAccountService;

    @Autowired
    private I_UserInventoryRepository inventoryRepository;

    @Autowired
    private RedisService redisService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EncryptionUtil.SaltGenerator saltGenerator;
    @Autowired
    private AccountService accountService;

    @BeforeEach
    void setUp() {
        redisService.clearAll();
    }

    @Test
    public void testStartAndCompleteRegistration_SuccessfulFlow() {
        // Arrange
        DTO_UserRegistration registrationData = new DTO_UserRegistration();
        registrationData.setLoginEmail("testuser@example.com");
        registrationData.setUsername("testuser");
        registrationData.setPassword("securePassword123");
        registrationData.setFirstName("Test");
        registrationData.setLastName("User");

        // Start registration
        String otp = registrationService.startRegistration(registrationData);
        assertNotNull(otp);

        // Verify OTP and complete registration
        UserInventory inventory = registrationService.completeRegistration("testuser", otp);
        if (inventory == null) {
            fail("Unable to complete registration");
        }

        // Verify user exists in database
        Optional<DTO_UserAccount> acc = userAccountService.findByEmail("testuser@example.com");
        assertTrue(acc.isPresent());
    }

    @Test
    public void testStartRegistration_UserAlreadyExists() {
        // Arrange
        DTO_UserRegistration registrationData = new DTO_UserRegistration();
        registrationData.setLoginEmail("existinguser@example.com");
        registrationData.setUsername("existinguser");
        registrationData.setPassword("securePassword123");

        // Simulate existing user
        registrationService.startRegistration(registrationData);

        // Try to register the same user again
        assertThrows(IllegalStateException.class, () -> {
            registrationService.startRegistration(registrationData);
        });
    }

    @Test
    public void testCompleteRegistration_InvalidOTP() {
        // Arrange
        DTO_UserRegistration registrationData = new DTO_UserRegistration();
        registrationData.setLoginEmail("invalidotp@example.com");
        registrationData.setUsername("invalidotpuser");
        registrationData.setPassword("securePassword123");

        // Start registration
        registrationService.startRegistration(registrationData);

        // Complete with wrong OTP
        assertThrows(IllegalStateException.class, () -> {
            registrationService.completeRegistration("invalidotpuser", "wrongOTP");
        });
    }

    @Test
    public void testCompanyRegistrationRequest() {
        // Arrange
        DTO_CompanyRegistration registrationData = new DTO_CompanyRegistration();
        registrationData.setLoginEmail("test@company.com");
        registrationData.setUsername("companyuser");
        registrationData.setPassword("securepassword");
        registrationData.setName("Test Company");
        registrationData.setAddress("123 Business St.");
        registrationData.setPhone("123-456-7890");
        registrationData.setContactEmail("contact@company.com");
        registrationData.setWebsiteUrl("https://www.testcompany.com");

        // act
        registrationService.requestCompanyRegistration(registrationData);

        Optional<Account> account = accountService.findAccountByEmail("test@company.com");
        assertTrue(account.isPresent());
        assertEquals(account.get().getUsername(), registrationData.getUsername());
        assertNotEquals(account.get().getPassword(), registrationData.getPassword());
        assertEquals(account.get().getLoginEmail(), registrationData.getLoginEmail());

        CompanyAccount companyAccount = (CompanyAccount) account.get();
        assertEquals(companyAccount.isApproved(), false);
    }
}
