package com.sportlink.sportlink.verification.location.contexts;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GeoContext {
    double userLon;
    double userLat;
    double locationLon;
    double locationLat;
    double maxRadius;
}
