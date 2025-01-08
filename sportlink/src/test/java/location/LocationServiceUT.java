package location;

import com.sportlink.sportlink.location.*;
import com.sportlink.sportlink.utils.DTO_Adapter;
import com.sportlink.sportlink.verification.location.LOCATION_VERIFICATION_STRATEGY;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LocationServiceUT {

    @Mock
    private I_LocationRepository locationRepository;

    @Mock
    private DTO_Adapter adapter;

    @InjectMocks
    private LocationService locationService;

    @Test
    void testSaveLocationWithEmptyVerificationStrategies() {
        DTO_Location dtoLocation = new DTO_Location();
        dtoLocation.setVerificationStrategies(Set.of());

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            locationService.saveLocation(dtoLocation);
        });
    }

    @Test
    void testSaveLocationWithValidData() {
        DTO_Location dtoLocation = mock(DTO_Location.class);
        Location location = mock(Location.class);

        when(dtoLocation.getVerificationStrategies()).thenReturn(Set.of(LOCATION_VERIFICATION_STRATEGY.USER_WITHIN_RADIUS));
        when(adapter.getLocationFromDTO(dtoLocation)).thenReturn(location);

        locationService.saveLocation(dtoLocation);

        verify(locationRepository).save(location);  // Verifies that save was called
    }

    @Test
    void testUpdateLocationWhenLocationNotFound() {
        DTO_Location dtoLocation = mock(DTO_Location.class);
        when(dtoLocation.getId()).thenReturn(1L);
        when(locationRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            locationService.updateLocation(dtoLocation);
        });

        assertEquals("Location not found with id: 1", thrown.getMessage());
    }

    @Test
    void testUpdateLocationWithValidData() {
        DTO_Location dtoLocation = mock(DTO_Location.class);
        Location existingLocation = mock(Location.class);
        when(dtoLocation.getId()).thenReturn(1L);
        when(locationRepository.findById(1L)).thenReturn(Optional.of(existingLocation));

        // Set up mock data
        when(dtoLocation.getName()).thenReturn("New Location");

        // Update method should update the name of the existing location
        locationService.updateLocation(dtoLocation);

        verify(existingLocation).setName("New Location");
        verify(locationRepository).save(existingLocation);  // Verifies that save was called
    }

    @Test
    void testDeleteLocation() {
        Long locationId = 1L;

        locationService.deleteLocation(locationId);

        verify(locationRepository).delete(locationId);  // Verifies that delete was called
    }

    @Test
    void testFindLocationByIdWhenLocationNotFound() {
        Long locationId = 1L;
        when(locationRepository.findById(locationId)).thenReturn(Optional.empty());

        Optional<DTO_Location> result = locationService.findLocationById(locationId);

        assertFalse(result.isPresent());
    }

    @Test
    void testIsWithinRadius_whenPointsAreWithinRadius() {
        // Arrange
        double lon1 = -73.985428;
        double lat1 = 40.748817; // Empire State Building
        double lon2 = -73.985100;
        double lat2 = 40.749000; // Nearby point
        int radius = 100; // 100 meters

        // Act
        boolean result = LocationService.isWithinRadius(lon1, lat1, lon2, lat2, radius);

        // Assert
        assertTrue(result, "Expected points to be within 100 meters radius");
    }

    @Test
    void testIsWithinRadius_whenPointsAreOutsideRadius() {
        // Arrange
        double lon1 = -73.985428;
        double lat1 = 40.748817; // Empire State Building
        double lon2 = -73.990000;
        double lat2 = 40.750000; // Farther point
        int radius = 100; // 100 meters

        // Act
        boolean result = LocationService.isWithinRadius(lon1, lat1, lon2, lat2, radius);

        // Assert
        assertFalse(result, "Expected points to be outside 100 meters radius");
    }

    @Test
    void testIsWithinRadius_whenPointsAreExactlyOnRadius() {
        // Arrange
        double lon1 = -73.985428;
        double lat1 = 40.748817; // Empire State Building
        double lon2 = -73.985600;
        double lat2 = 40.749200; // Point exactly on radius
        int radius = 50; // 50 meters

        // Act
        boolean result = LocationService.isWithinRadius(lon1, lat1, lon2, lat2, radius);

        // Assert
        assertTrue(result, "Expected points to be exactly on 50 meters radius");
    }

    @Test
    void testIsWithinRadius_whenPointsAreIdentical() {
        // Arrange
        double lon1 = -73.985428;
        double lat1 = 40.748817; // Empire State Building
        double lon2 = -73.985428;
        double lat2 = 40.748817; // Same point
        int radius = 0; // 0 meters

        // Act
        boolean result = LocationService.isWithinRadius(lon1, lat1, lon2, lat2, radius);

        // Assert
        assertTrue(result, "Expected identical points to be within 0 meters radius");
    }

    @Test
    void testIsWithinRadius_whenRadiusIsZeroAndPointsDiffer() {
        // Arrange
        double lon1 = -73.985428;
        double lat1 = 40.748817; // Empire State Building
        double lon2 = -73.985429;
        double lat2 = 40.748818; // Slightly different point
        int radius = 0; // 0 meters

        // Act
        boolean result = LocationService.isWithinRadius(lon1, lat1, lon2, lat2, radius);

        // Assert
        assertFalse(result, "Expected points to be outside 0 meters radius");
    }
}
