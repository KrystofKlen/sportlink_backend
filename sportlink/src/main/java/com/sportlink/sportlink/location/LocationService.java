package com.sportlink.sportlink.location;

import com.sportlink.sportlink.utils.DTO_Adapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LocationService {
    
    private final I_LocationRepository locationRepository;
    private final DTO_Adapter adapter;

    @Autowired
    public LocationService(I_LocationRepository locationRepository, DTO_Adapter adapter) {
        this.locationRepository = locationRepository;
        this.adapter = adapter;
    }

    public void saveLocation(DTO_Location dtoLocation) {
        if(dtoLocation.getVerificationStrategies().isEmpty()){
            throw new IllegalArgumentException("No verification strategy found");
        }
        locationRepository.save(adapter.getLocationFromDTO(dtoLocation));
    }

    // This method will update only those variable which are != null
    public void updateLocation(DTO_Location dtoLocation) {

        Optional<Location> existingLocationOpt = locationRepository.findById(dtoLocation.getId());
        if(existingLocationOpt.isEmpty()){
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
        if (dtoLocation.getImages() != null) {
            existingLocation.setImages(dtoLocation.getImages());
        }
        if (dtoLocation.getActivities() != null) {
            existingLocation.setActivities(dtoLocation.getActivities());
        }
        if (dtoLocation.getGeoCoordinate() != null) {
            existingLocation.setGeoCoordinate(dtoLocation.getGeoCoordinate());
        }
        if (dtoLocation.getVerificationStrategies() != null) {
            existingLocation.setVerificationStrategies(dtoLocation.getVerificationStrategies());
        }

        // 4. Save the updated entity
        locationRepository.save(existingLocation);
    }

    public void deleteLocation(Long id) {
        locationRepository.delete(id);
    }

    public Optional<Location> findLocationById(Long id) {
       return locationRepository.findById(id);
    }

    public List<Location> findNearbyLocations(GeoCoordinate geoCoordinate, double radius) {
        return locationRepository.findNearbyLocations(geoCoordinate, radius);
    }
}
