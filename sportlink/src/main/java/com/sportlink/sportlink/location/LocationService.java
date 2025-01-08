package com.sportlink.sportlink.location;

import com.sportlink.sportlink.utils.DTO_Adapter;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LocationService {

    private final I_LocationRepository locationRepository;
    private final DTO_Adapter adapter;
    public static final double MAX_DEVIATION = 0.5;

    @Autowired
    public LocationService(I_LocationRepository locationRepository, DTO_Adapter adapter) {
        this.locationRepository = locationRepository;
        this.adapter = adapter;
    }

    @Transactional
    public DTO_Location saveLocation(@Valid DTO_Location dtoLocation) {

        if (dtoLocation.getVerificationStrategies().isEmpty()) {
            throw new IllegalArgumentException("No verification strategy found");
        }
        return adapter.getDTO_Location(
                locationRepository.save(adapter.getLocationFromDTO(dtoLocation))
        );
    }

    // This method will update only those variable which are != null
    @Transactional
    public DTO_Location updateLocation(DTO_Location dtoLocation) {

        Optional<Location> existingLocationOpt = locationRepository.findById(dtoLocation.getId());
        if (existingLocationOpt.isEmpty()) {
            throw new RuntimeException("Location not found with id: " + dtoLocation.getId());
        }

        Location existingLocation = existingLocationOpt.get();

        if (dtoLocation.getName() != null) {
            existingLocation.setName(dtoLocation.getName());
        }
        if (dtoLocation.getAddress() != null) {
            existingLocation.setAddress(dtoLocation.getAddress());
        }
        if (dtoLocation.getDescription() != null) {
            existingLocation.setDescription(dtoLocation.getDescription());
        }
        if (dtoLocation.getActivities() != null && !dtoLocation.getActivities().isEmpty()) {
            existingLocation.setActivities(new HashSet<> (dtoLocation.getActivities()));
        }
        if (dtoLocation.getLongitude() != null) {
            existingLocation.setLongitude(dtoLocation.getLongitude());
        }
        if (dtoLocation.getLatitude() != null) {
            existingLocation.setLatitude(dtoLocation.getLatitude());
        }
        if (dtoLocation.getVerificationStrategies() != null && !dtoLocation.getVerificationStrategies().isEmpty()) {
            existingLocation.setVerificationStrategies(new HashSet<>(dtoLocation.getVerificationStrategies()));
        }

        // 4. Save the updated entity
        return adapter.getDTO_Location(locationRepository.save(existingLocation));
    }

    @Transactional
    public void deleteLocation(Long id) {
        locationRepository.delete(id);
    }

    public Optional<DTO_Location> findLocationById(Long id) {
        Optional<Location> locationOpt = locationRepository.findById(id);
        return locationOpt.map(location -> adapter.getDTO_Location(location));
    }

    public List<DTO_Location> findNearbyLocations(double lon, double lat, int radiusMeter) {
        List<Location> nearby = locationRepository.findNearbyLocations(lon, lat, MAX_DEVIATION);
        List<DTO_Location> filtered = new ArrayList<>();

        return nearby.stream().filter(location ->
                isWithinRadius(
                        location.getLongitude(),
                        location.getLatitude(),
                        lon,
                        lat,
                        radiusMeter)
        ).map(adapter::getDTO_Location).collect(Collectors.toList());

    }

    public static boolean isWithinRadius(double lon1, double lat1, double lon2, double lat2, int radius) {
        // Convert coordinates from degrees to radians
        double lat1Rad = Math.toRadians(lat1);
        double lon1Rad = Math.toRadians(lon1);
        double lat2Rad = Math.toRadians(lat2);
        double lon2Rad = Math.toRadians(lon2);

        // Calculate differences
        double deltaLat = lat2Rad - lat1Rad;
        double deltaLon = lon2Rad - lon1Rad;

        // Apply Haversine formula
        double a = Math.pow(Math.sin(deltaLat / 2), 2) +
                Math.cos(lat1Rad) * Math.cos(lat2Rad) *
                        Math.pow(Math.sin(deltaLon / 2), 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        // Calculate distance
        double distance = 6371000 * c;

        // Check if distance is within the radius
        return distance <= radius;
    }
}
