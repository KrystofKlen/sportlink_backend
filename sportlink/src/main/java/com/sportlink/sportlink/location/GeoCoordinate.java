package com.sportlink.sportlink.location;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GeoCoordinate {
    @Column(nullable = false)
    private double latitude;
    @Column(nullable = false)
    private double longitude;
}
