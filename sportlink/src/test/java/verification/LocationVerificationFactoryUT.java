package verification;

import com.sportlink.sportlink.location.I_LocationRepository;
import com.sportlink.sportlink.location.Location;
import com.sportlink.sportlink.redis.RedisService;
import com.sportlink.sportlink.verification.I_VerificationStrategy;
import com.sportlink.sportlink.verification.location.DTO_LocationVerificationRequest;
import com.sportlink.sportlink.verification.location.LocationVerificationFactory;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.sportlink.sportlink.verification.location.LOCATION_VERIFICATION_STRATEGY.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class LocationVerificationFactoryUT {

    @Mock
    private RedisService redisService;

    @Mock
    private I_LocationRepository locationRepository;

    private Location location;

    private LocationVerificationFactory locationVerificationFactory;
    private DTO_LocationVerificationRequest dtoRequest;

    @BeforeEach
    void setUp() {
        location = new Location();
        MockitoAnnotations.openMocks(this);
        locationVerificationFactory = new LocationVerificationFactory(redisService, locationRepository);

        // Initialize DTO_LocationVerificationRequest
        dtoRequest = new DTO_LocationVerificationRequest();
        dtoRequest.setLocationLatitude(50.0);
        dtoRequest.setLocationLongitude(8.0);
        dtoRequest.setUserLatitude(50.00001);
        dtoRequest.setUserLongitude(8.00001);
        dtoRequest.setCode("test-code");
        dtoRequest.setLocationId(1L);
        dtoRequest.setUserId(2L);
    }

    private List<Boolean> verifyStrategies(List<I_VerificationStrategy> strategies) {
        return strategies.stream().map(I_VerificationStrategy::verify).toList();
    }

    @Test
    void testUserWithinRadiusVerification() {
        // Setup: USER_WITHIN_RADIUS
        List<I_VerificationStrategy> strategies = locationVerificationFactory.getVerificationStrategyList(
                dtoRequest,
                Set.of(USER_WITHIN_RADIUS)
        );

        List<Boolean> actualResults = verifyStrategies(strategies);
        List<Boolean> expectedResults = List.of(true); // Assuming the coordinates fall within the radius.

        assertEquals(expectedResults, actualResults, "USER_WITHIN_RADIUS strategy verification failed.");
    }

    @Test
    void testUserWithinRadiusVerification_shouldFail() {
        dtoRequest.setUserLatitude(80.2);
        // Setup: USER_WITHIN_RADIUS
        List<I_VerificationStrategy> strategies = locationVerificationFactory.getVerificationStrategyList(
                dtoRequest,
                Set.of(USER_WITHIN_RADIUS)
        );

        List<Boolean> actualResults = verifyStrategies(strategies);
        List<Boolean> expectedResults = List.of(false); // Assuming the coordinates fall within the radius.

        assertEquals(expectedResults, actualResults, "USER_WITHIN_RADIUS strategy verification failed.");
    }

    @Test
    void testUserScanningCodeVerification() {
        location.setId(1L);
        when(locationRepository.findByCode(anyString())).thenReturn( Optional.of(location) );

        List<I_VerificationStrategy> strategies = locationVerificationFactory.getVerificationStrategyList(
                dtoRequest,
                Set.of(USER_SCANNING_CODE)
        );

        List<Boolean> actualResults = verifyStrategies(strategies);
        List<Boolean> expectedResults = List.of(true); // Assuming scanned location matches.

        assertEquals(expectedResults, actualResults, "USER_SCANNING_CODE strategy verification failed.");
    }

    @Test
    void testUserScanningCodeVerification_shouldFail() {
        location.setId(6L);
        when(locationRepository.findByCode(anyString())).thenReturn( Optional.of(location) );

        List<I_VerificationStrategy> strategies = locationVerificationFactory.getVerificationStrategyList(
                dtoRequest,
                Set.of(USER_SCANNING_CODE)
        );

        List<Boolean> actualResults = verifyStrategies(strategies);
        List<Boolean> expectedResults = List.of(false); // Assuming scanned location matches.

        assertEquals(expectedResults, actualResults, "USER_SCANNING_CODE strategy verification failed.");
    }

    @Test
    void testUserScanOneTimeCodeVerification() {
        // Setup: USER_SCAN_ONETIME_CODE
        when(redisService.getValue(dtoRequest.getCode())).thenReturn("2");

        List<I_VerificationStrategy> strategies = locationVerificationFactory.getVerificationStrategyList(
                dtoRequest,
                Set.of(USER_SCAN_ONETIME_CODE)
        );

        List<Boolean> actualResults = verifyStrategies(strategies);
        List<Boolean> expectedResults = List.of(true); // Assuming code data matches.

        assertEquals(expectedResults, actualResults, "USER_SCAN_ONETIME_CODE strategy verification failed.");
    }

    @Test
    void testUserScanOneTimeCodeVerificationFails() {
        // Setup: USER_SCAN_ONETIME_CODE with invalid Redis data
        when(redisService.getValue(dtoRequest.getCode())).thenReturn(null);

        try {
            locationVerificationFactory.getVerificationStrategyList(dtoRequest,Set.of(USER_SCAN_ONETIME_CODE));
        } catch (EntityNotFoundException ex) {
            assertEquals(EntityNotFoundException.class, ex.getClass());
        }
    }
}
