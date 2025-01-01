package com.sportlink.sportlink.location;

import com.sportlink.sportlink.location.verification.VERIFICATION_STRATEGY;
import jakarta.persistence.ElementCollection;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DTO_Location {
    private Long id;
    private String name;
    private String address;
    private String description;
    private List<String> images = new ArrayList<String>();
    private Set<ACTIVITY> activities = new HashSet<>();
    private GeoCoordinate geoCoordinate;
    private Set<VERIFICATION_STRATEGY> verificationStrategies = new HashSet<>();
}
