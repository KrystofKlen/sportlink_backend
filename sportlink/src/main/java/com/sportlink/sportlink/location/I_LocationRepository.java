package com.sportlink.sportlink.location;

import java.util.List;
import java.util.Optional;

public interface I_LocationRepository {
    Location save(Location location);
    Optional<Location> findById(Long id);
    List<Location> findAll();
    void delete(Long id);
    List<Location> findByActivity(List<ACTIVITY> activity);
    List<Location> findNearbyLocations(double lon, double lat, double distance);
}
