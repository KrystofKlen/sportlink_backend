package com.sportlink.sportlink;

import com.sportlink.sportlink.location.*;
import com.sportlink.sportlink.location.VERIFICATION_STRATEGY;
import com.sportlink.sportlink.utils.DTO_Adapter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LocationServiceTest {

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

        when(dtoLocation.getVerificationStrategies()).thenReturn(Set.of(VERIFICATION_STRATEGY.USER_WITHIN_LOCATION_RADIUS));
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
    void testFindLocationByIdWhenLocationExists() {
        Long locationId = 1L;
        Location location = mock(Location.class);
        when(locationRepository.findById(locationId)).thenReturn(Optional.of(location));

        Optional<DTO_Location> result = locationService.findLocationById(locationId);

        assertTrue(result.isPresent());
        assertEquals(location, result.get());
    }

    @Test
    void testFindLocationByIdWhenLocationNotFound() {
        Long locationId = 1L;
        when(locationRepository.findById(locationId)).thenReturn(Optional.empty());

        Optional<DTO_Location> result = locationService.findLocationById(locationId);

        assertFalse(result.isPresent());
    }

    @Test
    void testFindNearbyLocations() {
        double lon = 40.7128;
        double lat = -74.0060;

        double radius = 10.0;
        List<Location> nearbyLocations = Arrays.asList(mock(Location.class), mock(Location.class));

        when(locationRepository.findNearbyLocations(lon, lat, radius)).thenReturn(nearbyLocations);

        List<Location> result = locationService.findNearbyLocations(lon, lat, radius);

        assertEquals(2, result.size());
        verify(locationRepository).findNearbyLocations(lon, lat, radius);
    }
}
