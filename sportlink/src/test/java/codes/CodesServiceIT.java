package codes;

import com.sportlink.sportlink.SportlinkApplication;
import com.sportlink.sportlink.account.account.Account;
import com.sportlink.sportlink.account.account.AccountService;
import com.sportlink.sportlink.account.device.LocationDevice;
import com.sportlink.sportlink.codes.CodesService;
import com.sportlink.sportlink.location.I_LocationRepository;
import com.sportlink.sportlink.location.Location;
import com.sportlink.sportlink.redis.RedisService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("test")
@SpringBootTest(classes = SportlinkApplication.class)
public class CodesServiceIT {

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AccountService accountService;
    @Autowired
    private I_LocationRepository locationRepository;
    @Autowired
    private CodesService codesService;
    @Autowired
    private RedisService redisService;

    @Test
    public void testEstablishLocationOTP_Success() {
        // Arrange
        Account user = new Account();
        user.setLoginEmail("user@example.com");
        user.setUsername("testUser");
        user.setPassword(passwordEncoder.encode("securePass"));
        accountService.save(user);

        // Create and save a Location
        Location location = new Location();
        location = locationRepository.save(location);

        // Create and save a LocationDevice linked to the location
        LocationDevice locationDevice = new LocationDevice();
        locationDevice.setLoginEmail("device@example.com");
        locationDevice.setUsername("deviceUser");
        locationDevice.setPassword(passwordEncoder.encode("devicePass"));
        locationDevice.setLocation(location);
        accountService.save(locationDevice);

        // Act
        String otp = codesService.establishLocationOTP(user.getId(), locationDevice.getId());

        // Assert
        assertEquals(10, otp.length());  // Ensure OTP length is correct

        String storedPayload = redisService.getValue(otp);
        String expectedPayload = user.getId()+"-"+ location.getId();
        assertEquals(expectedPayload, storedPayload);
    }
}
