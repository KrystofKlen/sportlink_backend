package com.sportlink.sportlink.location;

import com.sportlink.sportlink.verification.location.LOCATION_VERIFICATION_STRATEGY;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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

    @NotNull
    @Size(min = 1, message = "Name cannot be empty")
    private String name;

    @NotNull
    private String address;

    @NotNull
    private String description;

    @NotNull
    @NotEmpty
    private Set<ACTIVITY> activities = new HashSet<>();

    @NotNull
    private Double longitude;

    @NotNull
    private Double latitude;

    @NotNull
    @NotEmpty
    private Set<LOCATION_VERIFICATION_STRATEGY> verificationStrategies = new HashSet<>();

    private List<String> imagesUUIDs = new ArrayList<>();

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof DTO_Location) {
            DTO_Location other = (DTO_Location) obj;
            return id.equals(other.id);
        }
        return false;
    }
}
