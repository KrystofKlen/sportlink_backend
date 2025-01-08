package com.sportlink.sportlink.location;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/locations")
public class LocationController {
    private final LocationService locationService;

    @Autowired
    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    /**
     * Create a new location
     */
    @PostMapping
    public ResponseEntity<DTO_Location> createLocation(@Valid @RequestBody DTO_Location dtoLocation) {
        DTO_Location savedLocation = locationService.saveLocation(dtoLocation);
        return ResponseEntity.ok(savedLocation);
    }

    /**
     * Update an existing location
     */
    @PutMapping("/{id}")
    public ResponseEntity<DTO_Location> updateLocation(@PathVariable Long id, @RequestBody DTO_Location dtoLocation) {
        dtoLocation.setId(id);
        DTO_Location updatedLocation = locationService.updateLocation(dtoLocation);
        return ResponseEntity.ok(updatedLocation);
    }

    /**
     * Delete a location by ID
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLocation(@PathVariable Long id) {
        locationService.deleteLocation(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Find a location by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<DTO_Location> getLocationById(@PathVariable Long id) {
        Optional<DTO_Location> location = locationService.findLocationById(id);
        return location.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Find nearby locations based on longitude, latitude, and radius
     */
    @GetMapping("/nearby")
    public ResponseEntity<List<DTO_Location>> getNearbyLocations(
            @RequestParam double longitude,
            @RequestParam double latitude,
            @Positive @RequestParam int radius) {

        List<DTO_Location> locations = locationService.findNearbyLocations(longitude, latitude, radius);
        return ResponseEntity.ok(locations);
    }
}
