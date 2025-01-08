package com.sportlink.sportlink.verification.location.stretegies;

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

        double dLat = latL - latU;
        double dLon = lonL - lonU;

        // Euclidean distance formula (ignores Earth's curvature)
        double distance = Math.sqrt(Math.pow(dLat, 2) + Math.pow(dLon, 2));

        // Check if the distance is within the specified radius
        return distance <= geoContext.getMaxRadius();
    }
}
