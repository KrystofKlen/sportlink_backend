package verification;

import com.sportlink.sportlink.verification.location.contexts.GeoContext;
import com.sportlink.sportlink.verification.location.stretegies.UserRadius;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

public class UserRadiusUT {
    @Mock
    private GeoContext geoContext;

    private UserRadius userRadius;

    @BeforeEach
    void setUp() {
        // Initialize mocks before each test
        MockitoAnnotations.openMocks(this);
        userRadius = new UserRadius(geoContext);
    }

    @Test
    void testVerifyWithinRadius() {
        // Set up mock behavior
        when(geoContext.getLocationLat()).thenReturn(10.0);
        when(geoContext.getLocationLon()).thenReturn(20.0);
        when(geoContext.getUserLat()).thenReturn(10.000001);
        when(geoContext.getUserLon()).thenReturn(20.000001);
        when(geoContext.getMaxRadius()).thenReturn(5.0);

        // Verify that the distance is within the specified radius
        boolean result = userRadius.verify();

        assertTrue(result, "The distance should be within the specified radius.");
    }

    @Test
    void testVerifyOutsideRadius() {
        // Set up mock behavior
        when(geoContext.getLocationLat()).thenReturn(10.0);
        when(geoContext.getLocationLon()).thenReturn(20.0);
        when(geoContext.getUserLat()).thenReturn(10.1);
        when(geoContext.getUserLon()).thenReturn(20.1);
        when(geoContext.getMaxRadius()).thenReturn(5.0);

        // Verify that the distance is outside the specified radius
        boolean result = userRadius.verify();

        assertFalse(result, "The distance should be outside the specified radius.");
    }

    @Test
    void testVerifyZeroDistance() {
        // Set up mock behavior for zero distance (user and location are at the same point)
        when(geoContext.getLocationLat()).thenReturn(10.0);
        when(geoContext.getLocationLon()).thenReturn(20.0);
        when(geoContext.getUserLat()).thenReturn(10.0);
        when(geoContext.getUserLon()).thenReturn(20.0);
        when(geoContext.getMaxRadius()).thenReturn(0.1);

        // Verify that the distance is zero, which is always within the radius
        boolean result = userRadius.verify();

        assertTrue(result, "The distance should be zero and within the radius.");
    }

    @Test
    void testVerifyNegativeDistance() {
        // Set up mock behavior for a negative case (invalid radius)
        when(geoContext.getLocationLat()).thenReturn(10.0);
        when(geoContext.getLocationLon()).thenReturn(20.0);
        when(geoContext.getUserLat()).thenReturn(10.1);
        when(geoContext.getUserLon()).thenReturn(20.1);
        when(geoContext.getMaxRadius()).thenReturn(0.0); // radius is zero

        // Verify that no distance should be within a radius of 0
        boolean result = userRadius.verify();

        assertFalse(result, "A radius of 0 should not allow any distance.");
    }
}
