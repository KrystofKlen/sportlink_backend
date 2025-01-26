package registration;

import com.sportlink.sportlink.account.account.Account;
import com.sportlink.sportlink.account.account.AccountService;
import com.sportlink.sportlink.account.account.DTO_Account;
import com.sportlink.sportlink.account.user.UserAccountService;
import com.sportlink.sportlink.redis.RedisService;
import com.sportlink.sportlink.registration.RegistrationPayload;
import com.sportlink.sportlink.registration.RegistrationService;
import com.sportlink.sportlink.security.EncryptionUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
public class RegistrationServiceUT {
    @Mock
    private UserAccountService userAccountService;

    @Mock
    private AccountService accountService;

    @Mock
    private EncryptionUtil.SaltGenerator saltGenerator;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private RedisService redisService;

    @InjectMocks
    private RegistrationService registrationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegisterWhenUsernameExists() {
        RegistrationPayload registrationData = new RegistrationPayload();
        registrationData.setLoginEmail("1@example.com");
        registrationData.setUsername("1");

        DTO_Account anotherUser = new DTO_Account();
        anotherUser.setUsername("1");

        when(accountService.findAccountByEmail("1@example.com")).thenReturn(Optional.empty());
        when(accountService.findDTOAccountByUsername("1")).thenReturn(Optional.of(anotherUser));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> registrationService.startRegistration(registrationData));
        assertEquals("User account already exists", exception.getMessage());
    }

    @Test
    void testRegisterWhenEmailExists() {
        RegistrationPayload registrationData = new RegistrationPayload();
        registrationData.setLoginEmail("1@example.com");
        registrationData.setUsername("1");

        Account anotherUser = new Account();

        when(accountService.findAccountByEmail("1@example.com")).thenReturn(Optional.of(anotherUser));
        when(accountService.findDTOAccountByUsername("1")).thenReturn(Optional.empty());

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> registrationService.startRegistration(registrationData));
        assertEquals("User account already exists", exception.getMessage());
    }


}
