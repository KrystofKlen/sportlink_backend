package com.sportlink.sportlink.location;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String address;
    private String description;

    @ElementCollection
    private List<String> images;
}

