package location;

import com.sportlink.sportlink.SportlinkApplication;
import com.sportlink.sportlink.account.account.I_AccountRepository;
import com.sportlink.sportlink.account.company.CompanyAccount;
import com.sportlink.sportlink.currency.Currency;
import com.sportlink.sportlink.currency.I_CurrencyRepository;
import com.sportlink.sportlink.location.*;
import com.sportlink.sportlink.utils.DTO_Adapter;
import com.sportlink.sportlink.verification.location.LOCATION_VERIFICATION_STRATEGY;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest(classes = SportlinkApplication.class)
public class LocationServiceIT {
    @Autowired
    private LocationService locationService;

    @Autowired
    private I_LocationRepository locationRepository;

    @Autowired
    private DTO_Adapter dtoAdapter;

    private DTO_Location dtoLocation;
    private Location location;

    private CompanyAccount companyAccount;
    @Autowired
    private I_AccountRepository i_AccountRepository;
    @Autowired
    private I_CurrencyRepository i_CurrencyRepository;

    @BeforeEach
    @Transactional
    public void setUp() {
        // Set up the initial Location entity and DTO_Location.
        location = new Location();
        location.setName("Test Location");
        location.setAddress("Test Address");
        location.setDescription("Test Description");
        location.setVerificationStrategies(Set.of(LOCATION_VERIFICATION_STRATEGY.USER_WITHIN_RADIUS));

        // Corresponding DTO
        dtoLocation = dtoAdapter.getDTO_Location(location);

        companyAccount = new CompanyAccount();
        companyAccount.setName("Test Company Account");
        companyAccount = i_AccountRepository.save(companyAccount);

        Currency currency = new Currency();
        currency.setName("AAA");
        currency.setIssuer(companyAccount);
        currency = i_CurrencyRepository.save(currency);
    }

    @Test
    public void testSaveLocation() {
        // Save the DTO Location via LocationService
        DTO_Location savedLocation = locationService.saveLocation(dtoLocation, companyAccount.getId());

        // Assert that the Location is saved and the DTO returned is not null
        assertNotNull(savedLocation);
        assertEquals("Test Location", savedLocation.getName());

        // Check if the Location is saved in the repository
        Optional<Location> foundLocation = locationRepository.findById(savedLocation.getId());
        assertTrue(foundLocation.isPresent());
        assertEquals("Test Location", foundLocation.get().getName());
    }

    @Test
    public void testUpdateLocation() {
        // First save the Location to have it in the database
        DTO_Location savedLocation = locationService.saveLocation(dtoLocation, companyAccount.getId());

        // Now update the Location data
        savedLocation.setName("Updated Location");
        savedLocation.setAddress("Updated Address");

        // Update the Location via LocationService
        DTO_Location updatedLocation = locationService.updateLocation(savedLocation);

        // Assert the updated location is returned
        assertNotNull(updatedLocation);
        assertEquals("Updated Location", updatedLocation.getName());

        // Check if the Location in the database was updated
        Optional<Location> foundLocation = locationRepository.findById(updatedLocation.getId());
        assertTrue(foundLocation.isPresent());
        assertEquals("Updated Location", foundLocation.get().getName());
        assertEquals("Updated Address", foundLocation.get().getAddress());
        assertEquals("Test Description", foundLocation.get().getDescription());
    }

    @Test
    public void testUpdateLocationNotFound() {
        // Create a DTO_Location with an ID that doesn't exist in the database
        dtoLocation.setId(999L);

        // Try to update and assert that it throws a RuntimeException
        RuntimeException exception = assertThrows(RuntimeException.class, () -> locationService.updateLocation(dtoLocation));
        assertEquals("Location not found with id: 999", exception.getMessage());
    }

    @Test
    public void testDeleteLocation() {
        // First save the Location to the repository
        DTO_Location savedLocation = locationService.saveLocation(dtoLocation, companyAccount.getId());

        // Now delete the Location
        locationService.deleteLocation(savedLocation.getId());

        // Assert that the Location is no longer in the repository
        Optional<Location> foundLocation = locationRepository.findById(savedLocation.getId());
        assertFalse(foundLocation.isPresent());
    }

    @Test
    public void testFindLocationById() {
        // First save the Location to the repository
        DTO_Location savedLocation = locationService.saveLocation(dtoLocation, companyAccount.getId());

        // Retrieve the Location by ID
        Optional<DTO_Location> foundLocation = locationService.findLocationById(savedLocation.getId());

        // Assert that the location is found and is correct
        assertTrue(foundLocation.isPresent());
        assertEquals("Test Location", foundLocation.get().getName());
    }

    @Test
    public void testFindLocationByIdNotFound() {
        // Try to find a Location with a non-existing ID
        Optional<DTO_Location> foundLocation = locationService.findLocationById(999L);

        // Assert that no location is found
        assertFalse(foundLocation.isPresent());
    }

    @Test
    public void testFindNearbyLocations() {
        // First save a location with specific coordinates to the repository
        dtoLocation.setLongitude(10.0);
        dtoLocation.setLatitude(20.0);
        locationService.saveLocation(dtoLocation, companyAccount.getId());

        // Find locations within a certain radius
        List<DTO_Location> nearbyLocations = locationService.findNearbyLocations(10.0, 20.0, 5);

        // Assert that at least one nearby location is returned
        assertNotNull(nearbyLocations);
        assertFalse(nearbyLocations.isEmpty());
        assertEquals(1, nearbyLocations.size()); // The saved location should be found
    }

    @Test
    public void testFindLocationByActivities() {
        Location location1 = new Location();
        location1.setName("1");
        Location location2 = new Location();
        location2.setName("2");
        Location location3 = new Location();
        location3.setName("3");
        Location location4 = new Location();
        location4.setName("4");
        Location location5 = new Location();
        location5.setName("5");

        location1.setActivities(Set.of(ACTIVITY.FOOTBALL, ACTIVITY.SWIMMING));
        location2.setActivities(Set.of(ACTIVITY.ICE_SKATING,ACTIVITY.SKATING, ACTIVITY.FOOTBALL));
        location3.setActivities(Set.of(ACTIVITY.BOWLING, ACTIVITY.SWIMMING));
        location4.setActivities(Set.of(ACTIVITY.FOOTBALL));
        location5.setActivities(Set.of(ACTIVITY.SWIMMING, ACTIVITY.ICE_SKATING));

        locationRepository.save(location1);
        locationRepository.save(location2);
        locationRepository.save(location3);
        locationRepository.save(location4);
        locationRepository.save(location5);

        Set<DTO_Location> result = locationService.findByActivities(List.of(ACTIVITY.FOOTBALL));
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(3, result.size());
        result.forEach(location -> {
            assertTrue(location.getActivities().contains(ACTIVITY.FOOTBALL));
            assertTrue( !location.getName().equals("5") && !location.getName().equals("3") );
        });

        result.clear();
        result = locationService.findByActivities(List.of(ACTIVITY.GOLF));
        assertNotNull(result);
        assertTrue(result.isEmpty());

        result = locationService.findByActivities(List.of(ACTIVITY.SWIMMING, ACTIVITY.ICE_SKATING, ACTIVITY.FOOTBALL));
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(5, result.size());
    }
}
