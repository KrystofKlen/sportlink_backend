package com.sportlink.sportlink.location;

import com.sportlink.sportlink.location.verification.VERIFICATION_STRATEGY;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String address;
    private String description;

    @ElementCollection
    private List<String> images;

    @ElementCollection
    private Set<ACTIVITY> activities;

    GeoCoordinate geoCoordinate;

    @ElementCollection
    private Set<VERIFICATION_STRATEGY> verificationStrategies;
}

