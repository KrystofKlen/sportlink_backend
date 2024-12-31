package com.sportlink.sportlink.location;

import jakarta.persistence.ElementCollection;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class DTO_Location {
    private Long id;
    private String name;
    private String address;
    private String description;
    private List<String> images;
}
