package registration;

import com.sportlink.sportlink.account.user.DTO_UserAccount;
import com.sportlink.sportlink.account.user.UserAccountService;
import com.sportlink.sportlink.inventory.user.I_UserInventoryRepository;
import com.sportlink.sportlink.redis.RedisService;
import com.sportlink.sportlink.registration.DTO_UserRegistration;
import com.sportlink.sportlink.registration.RegistrationService;
import com.sportlink.sportlink.security.EncryptionUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class RegistrationServiceUT {
    @Mock
    private UserAccountService userAccountService;

    @Mock
    private EncryptionUtil.SaltGenerator saltGenerator;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private RedisService redisService;

    @Mock
    private I_UserInventoryRepository inventoryInventory;

    @InjectMocks
    private RegistrationService registrationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegisterWhenUsernameExists() {
        DTO_UserRegistration registrationData = new DTO_UserRegistration();
        registrationData.setLoginEmail("1@example.com");
        registrationData.setUsername("1");

        DTO_UserAccount anotherUser = new DTO_UserAccount();
        anotherUser.setUsername("1");

        when(userAccountService.findByEmail("1@example.com")).thenReturn(Optional.empty());
        when(userAccountService.findByUsername("1")).thenReturn(Optional.of(anotherUser));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> registrationService.startRegistration(registrationData));
        assertEquals("User account already exists", exception.getMessage());
    }

    @Test
    void testRegisterWhenEmailExists() {
        DTO_UserRegistration registrationData = new DTO_UserRegistration();
        registrationData.setLoginEmail("1@example.com");
        registrationData.setUsername("1");

        DTO_UserAccount anotherUser = new DTO_UserAccount();

        when(userAccountService.findByEmail("1@example.com")).thenReturn(Optional.of(anotherUser));
        when(userAccountService.findByUsername("1")).thenReturn(Optional.empty());

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> registrationService.startRegistration(registrationData));
        assertEquals("User account already exists", exception.getMessage());
    }


}
