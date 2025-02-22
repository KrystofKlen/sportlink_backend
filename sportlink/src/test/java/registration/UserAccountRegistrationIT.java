package registration;

import com.sportlink.sportlink.SportlinkApplication;
import com.sportlink.sportlink.account.account.ACCOUNT_STATUS;
import com.sportlink.sportlink.account.account.Account;
import com.sportlink.sportlink.account.account.AccountService;
import com.sportlink.sportlink.account.company.CompanyAccount;
import com.sportlink.sportlink.account.device.LocationDevice;
import com.sportlink.sportlink.account.user.UserAccountService;
import com.sportlink.sportlink.consent.ConsentService;
import com.sportlink.sportlink.consent.DTO_Consent;
import com.sportlink.sportlink.location.I_LocationRepository;
import com.sportlink.sportlink.location.Location;
import com.sportlink.sportlink.redis.RedisService;
import com.sportlink.sportlink.registration.DTO_CompanyRegistration;
import com.sportlink.sportlink.registration.DTO_DeviceRegistration;
import com.sportlink.sportlink.registration.RegistrationPayload;
import com.sportlink.sportlink.registration.RegistrationService;
import com.sportlink.sportlink.security.EncryptionUtil;
import com.sportlink.sportlink.utils.EmailSender;
import com.sportlink.sportlink.utils.PayloadParser;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;

@ActiveProfiles("test")
@SpringBootTest(classes = SportlinkApplication.class)
public class UserAccountRegistrationIT {
    @Autowired
    private RegistrationService registrationService;

    @Autowired
    private UserAccountService userAccountService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AccountService accountService;

    @Autowired
    private ConsentService consentService;

    @Spy
    private EmailSender emailSender;

    @Autowired
    private I_LocationRepository locationRepository;

    @BeforeEach
    void setUp() {
        redisService.clearAll();
    }

    @Test
    public void testStartAndCompleteRegistration_SuccessfulFlow() throws MessagingException {
        // Arrange
        RegistrationPayload registrationData = new RegistrationPayload();
        registrationData.setLoginEmail("testuser@example.com");
        registrationData.setUsername("testuser");
        registrationData.setPassword("securePassword123");
        registrationData.setFirstName("Test");
        registrationData.setLastName("User");

        // Create an Agreement before testing consent
        String agreementText = "GDPR Agreement Text";
        LocalDate endDate = LocalDate.now().plusYears(1); // Example end date for agreement
        consentService.addAgreement(agreementText, endDate);

        // Start registration
        doNothing().when(emailSender).sendHtmlEmail(any(), any(), any());

        String otp = registrationService.startRegistration(registrationData);
        assertNotNull(otp);

        // Verify user exists in database
        String val = redisService.getValue(registrationData.getUsername());
        assertNotNull(val);
        RegistrationPayload registered = PayloadParser.parseJsonToObject(val, RegistrationPayload.class);
        assertNotEquals("securePassword123", registered.getPassword());
        assertEquals(registrationData.getFirstName(), registered.getFirstName());
        assertEquals(registrationData.getLastName(), registered.getLastName());
        assertEquals(registrationData.getLoginEmail(), registered.getLoginEmail());

        registrationService.completeRegistration("testuser", otp);

        // check consent
        Optional<Account> account = accountService.findAccountByEmail("testuser@example.com");
        assertNotNull(account.orElse(null));
        List<DTO_Consent> consents = consentService.getAccountsConsents(account.get().getId());
        assertEquals(1, consents.size());
    }

    @Test
    public void testStartRegistration_UserAlreadyExists() {
        // Arrange
        RegistrationPayload registrationData = new RegistrationPayload();
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
        RegistrationPayload registrationData = new RegistrationPayload();
        registrationData.setLoginEmail("invalidotp@example.com");
        registrationData.setUsername("invalidotpuser");
        registrationData.setPassword("securePassword123");

        // Create an Agreement before testing consent
        String agreementText = "GDPR Agreement Text";
        LocalDate endDate = LocalDate.now().plusYears(1); // Example end date for agreement
        consentService.addAgreement(agreementText, endDate);

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
        registrationData.setCurrencyName("Currency");

        // act
        registrationService.requestCompanyRegistration(registrationData);

        Optional<Account> account = accountService.findAccountByEmail("test@company.com");
        assertTrue(account.isPresent());
        assertEquals(account.get().getUsername(), registrationData.getUsername());
        assertNotEquals(account.get().getPassword(), registrationData.getPassword());
        assertEquals(account.get().getLoginEmail(), registrationData.getLoginEmail());

        CompanyAccount companyAccount = (CompanyAccount) account.get();
        assertEquals(companyAccount.getStatus(), ACCOUNT_STATUS.NOT_APPROVED);
    }

    @Test
    public void testRegisterLocationDevice_Success() {
        // Arrange
        DTO_DeviceRegistration registrationData = new DTO_DeviceRegistration();
        registrationData.setLoginEmail("locationdevice@example.com");
        registrationData.setUsername("locationDeviceUser");
        registrationData.setPassword("securePassword123");

        // Create and save a location with an issuer account
        CompanyAccount issuer = new CompanyAccount();
        issuer.setLoginEmail("issuer@example.com");
        issuer.setUsername("issuerUser");
        issuer.setPassword(passwordEncoder.encode("issuerPass"));
        accountService.save(issuer);

        Location location = new Location();
        location.setIssuer(issuer);
        locationRepository.save(location);

        registrationData.setLocationId(location.getId());

        // Act
        registrationService.registerLocationDevice(registrationData, issuer.getId());

        // Assert
        Optional<Account> registeredDevice = accountService.findAccountByEmail("locationdevice@example.com");
        assertTrue(registeredDevice.isPresent());
        assertTrue(registeredDevice.get() instanceof LocationDevice);

        LocationDevice locationDevice = (LocationDevice) registeredDevice.get();
        assertEquals(locationDevice.getLoginEmail(), registrationData.getLoginEmail());
        assertEquals(locationDevice.getUsername(), registrationData.getUsername());
        assertNotEquals(locationDevice.getPassword(), registrationData.getPassword()); // Should be encrypted
        assertEquals(locationDevice.getLocation().getId(), location.getId());
    }
}
