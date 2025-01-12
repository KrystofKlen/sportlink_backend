package location;

import com.sportlink.sportlink.account.company.CompanyAccount;
import com.sportlink.sportlink.location.*;
import com.sportlink.sportlink.utils.DTO_Adapter;
import com.sportlink.sportlink.verification.location.LOCATION_VERIFICATION_STRATEGY;
import org.junit.jupiter.api.BeforeEach;
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

    @Mock
    private CompanyAccount companyAccount;

    @InjectMocks
    private LocationService locationService;

    @BeforeEach
    public void setUp() {
        companyAccount = mock(CompanyAccount.class);
    }

    @Test
    void testSaveLocationWithEmptyVerificationStrategies() {
        DTO_Location dtoLocation = new DTO_Location();
        dtoLocation.setVerificationStrategies(Set.of());

        // Ensure IllegalArgumentException is thrown if no strategies are provided
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            locationService.saveLocation(dtoLocation, companyAccount);
        });

        assertEquals("No verification strategy found", thrown.getMessage());
    }

    @Test
    void testSaveLocationWithValidData() {
        DTO_Location dtoLocation = mock(DTO_Location.class);
        Location location = mock(Location.class);

        when(dtoLocation.getVerificationStrategies()).thenReturn(Set.of(LOCATION_VERIFICATION_STRATEGY.USER_WITHIN_RADIUS));
        when(adapter.getLocationFromDTO(dtoLocation)).thenReturn(location);

        locationService.saveLocation(dtoLocation, companyAccount);

        verify(locationRepository).save(location);
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

        // Update location's name
        when(dtoLocation.getName()).thenReturn("New Location");

        locationService.updateLocation(dtoLocation);

        verify(existingLocation).setName("New Location");
        verify(locationRepository).save(existingLocation);
    }

    @Test
    void testDeleteLocation() {
        Long locationId = 1L;

        locationService.deleteLocation(locationId);

        verify(locationRepository).deleteById(locationId);
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
        double lon1 = -73.985428;
        double lat1 = 40.748817;
        double lon2 = -73.985100;
        double lat2 = 40.749000;
        int radius = 100;

        boolean result = LocationService.isWithinRadius(lon1, lat1, lon2, lat2, radius);

        assertTrue(result);
    }

    @Test
    void testIsWithinRadius_whenPointsAreOutsideRadius() {
        double lon1 = -73.985428;
        double lat1 = 40.748817;
        double lon2 = -73.990000;
        double lat2 = 40.750000;
        int radius = 100;

        boolean result = LocationService.isWithinRadius(lon1, lat1, lon2, lat2, radius);

        assertFalse(result);
    }

    @Test
    void testIsWithinRadius_whenPointsAreIdentical() {
        double lon1 = -73.985428;
        double lat1 = 40.748817;
        double lon2 = -73.985428;
        double lat2 = 40.748817;
        int radius = 0;

        boolean result = LocationService.isWithinRadius(lon1, lat1, lon2, lat2, radius);

        assertTrue(result);
    }
}