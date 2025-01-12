package com.sportlink.sportlink.verification.location.stretegies;

import com.sportlink.sportlink.location.LocationService;
import com.sportlink.sportlink.verification.I_VerificationStrategy;
import com.sportlink.sportlink.verification.location.contexts.GeoContext;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class UserRadius implements I_VerificationStrategy {

    GeoContext geoContext;

    @Override
    public boolean verify() {
        double latL = geoContext.getLocationLat();
        double lonL = geoContext.getLocationLon();
        double latU = geoContext.getUserLat();
        double lonU = geoContext.getUserLon();

        return LocationService.isWithinRadius(lonL, latL, lonU, latU, (int) geoContext.getMaxRadius());
    }
}
