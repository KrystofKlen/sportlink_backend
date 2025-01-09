package com.sportlink.sportlink.media.location;

import com.sportlink.sportlink.location.Location;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class LocationMedia {

    @Id
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "location_id")
    private Location location;

    @ElementCollection
    private List<String> imgNames;
}
