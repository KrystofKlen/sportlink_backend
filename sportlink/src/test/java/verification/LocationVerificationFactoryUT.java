package verification;

import com.sportlink.sportlink.codes.CodeData;
import com.sportlink.sportlink.codes.CodesService;
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

import static com.sportlink.sportlink.verification.location.LOCATION_VERIFICATION_STRATEGY.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class LocationVerificationFactoryUT {

    @Mock
    private CodesService codesService;

    @Mock
    private RedisService redisService;

    private LocationVerificationFactory locationVerificationFactory;
    private DTO_LocationVerificationRequest dtoRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        locationVerificationFactory = new LocationVerificationFactory(codesService, redisService);

        // Initialize DTO_LocationVerificationRequest
        dtoRequest = new DTO_LocationVerificationRequest();
        dtoRequest.setLocationLatitude(50.0);
        dtoRequest.setLocationLongitude(8.0);
        dtoRequest.setUserLatitude(50.1);
        dtoRequest.setUserLongitude(8.1);
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
                List.of(USER_WITHIN_RADIUS)
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
                List.of(USER_WITHIN_RADIUS)
        );

        List<Boolean> actualResults = verifyStrategies(strategies);
        List<Boolean> expectedResults = List.of(false); // Assuming the coordinates fall within the radius.

        assertEquals(expectedResults, actualResults, "USER_WITHIN_RADIUS strategy verification failed.");
    }

    @Test
    void testUserScanningCodeVerification() {
        // Setup: USER_SCANNING_CODE
        CodeData mockCodeData = new CodeData();
        mockCodeData.setLocationId(1L);
        when(codesService.findByCode(dtoRequest.getCode())).thenReturn(Optional.of(mockCodeData));

        List<I_VerificationStrategy> strategies = locationVerificationFactory.getVerificationStrategyList(
                dtoRequest,
                List.of(USER_SCANNING_CODE)
        );

        List<Boolean> actualResults = verifyStrategies(strategies);
        List<Boolean> expectedResults = List.of(true); // Assuming scanned location matches.

        assertEquals(expectedResults, actualResults, "USER_SCANNING_CODE strategy verification failed.");
    }

    @Test
    void testUserScanningCodeVerification_shouldFail() {
        // Setup: USER_SCANNING_CODE
        CodeData mockCodeData = new CodeData();
        mockCodeData.setLocationId(3L);
        when(codesService.findByCode(dtoRequest.getCode())).thenReturn(Optional.of(mockCodeData));

        List<I_VerificationStrategy> strategies = locationVerificationFactory.getVerificationStrategyList(
                dtoRequest,
                List.of(USER_SCANNING_CODE)
        );

        List<Boolean> actualResults = verifyStrategies(strategies);
        List<Boolean> expectedResults = List.of(false); // Assuming scanned location matches.

        assertEquals(expectedResults, actualResults, "USER_SCANNING_CODE strategy verification failed.");
    }

    @Test
    void testLocationScanningCodeVerification() {
        // Setup: LOCATION_SCANNING_CODE
        CodeData mockCodeData = new CodeData();
        mockCodeData.setUserId(2L);
        when(codesService.findByCode(dtoRequest.getCode())).thenReturn(Optional.of(mockCodeData));

        List<I_VerificationStrategy> strategies = locationVerificationFactory.getVerificationStrategyList(
                dtoRequest,
                List.of(LOCATION_SCANNING_CODE)
        );

        List<Boolean> actualResults = verifyStrategies(strategies);
        List<Boolean> expectedResults = List.of(true); // Assuming user IDs match.

        assertEquals(expectedResults, actualResults, "LOCATION_SCANNING_CODE strategy verification failed.");
    }

    @Test
    void testLocationScanningCodeVerification_shouldFail() {
        // Setup: LOCATION_SCANNING_CODE
        CodeData mockCodeData = new CodeData();
        mockCodeData.setUserId(3L);
        when(codesService.findByCode(dtoRequest.getCode())).thenReturn(Optional.of(mockCodeData));

        List<I_VerificationStrategy> strategies = locationVerificationFactory.getVerificationStrategyList(
                dtoRequest,
                List.of(LOCATION_SCANNING_CODE)
        );

        List<Boolean> actualResults = verifyStrategies(strategies);
        List<Boolean> expectedResults = List.of(false); // Assuming user IDs match.

        assertEquals(expectedResults, actualResults, "LOCATION_SCANNING_CODE strategy verification failed.");
    }

    @Test
    void testUserScanOneTimeCodeVerification() {
        // Setup: USER_SCAN_ONETIME_CODE
        String jsonData = "{\"locationId\":1, \"userId\":2}";
        when(redisService.getValue(dtoRequest.getCode())).thenReturn(jsonData);

        List<I_VerificationStrategy> strategies = locationVerificationFactory.getVerificationStrategyList(
                dtoRequest,
                List.of(USER_SCAN_ONETIME_CODE)
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
            locationVerificationFactory.getVerificationStrategyList(dtoRequest,List.of(USER_SCAN_ONETIME_CODE));
        } catch (EntityNotFoundException ex) {
            assertEquals(EntityNotFoundException.class, ex.getClass());
        }
    }

    @Test
    void testAllConditions() {
        // Setup all strategies
        CodeData mockCodeData = new CodeData();
        mockCodeData.setLocationId(1L);
        mockCodeData.setUserId(2L);
        when(codesService.findByCode(dtoRequest.getCode())).thenReturn(Optional.of(mockCodeData));
        when(redisService.getValue(dtoRequest.getCode())).thenReturn("{\"locationId\":1, \"userId\":2}");

        List<I_VerificationStrategy> strategies = locationVerificationFactory.getVerificationStrategyList(
                dtoRequest,
                List.of(
                        USER_WITHIN_RADIUS,
                        USER_SCANNING_CODE,
                        LOCATION_SCANNING_CODE,
                        USER_SCAN_ONETIME_CODE
                )
        );

        List<Boolean> actualResults = verifyStrategies(strategies);
        List<Boolean> expectedResults = List.of(true, true, true, true);

        assertEquals(expectedResults, actualResults, "All strategies verification failed.");
    }

    @Test
    void testAllConditions_shouldFail() {
        // Simulate failure in USER_SCAN_ONETIME_CODE
        when(redisService.getValue(dtoRequest.getCode())).thenReturn("{\"locationId\":3, \"userId\":2}");
        CodeData mockCodeData = new CodeData();
        mockCodeData.setLocationId(1L);
        mockCodeData.setUserId(2L);
        when(codesService.findByCode(dtoRequest.getCode())).thenReturn(Optional.of(mockCodeData));

        List<I_VerificationStrategy> strategies = locationVerificationFactory.getVerificationStrategyList(
                dtoRequest,
                List.of(
                        USER_WITHIN_RADIUS,
                        USER_SCANNING_CODE,
                        LOCATION_SCANNING_CODE,
                        USER_SCAN_ONETIME_CODE
                )
        );

        List<Boolean> actualResults = verifyStrategies(strategies);
        List<Boolean> expectedResults = List.of(true, true, true, false);

        assertEquals(expectedResults, actualResults, "Failure in USER_SCAN_ONETIME_CODE was not detected.");
    }
}
