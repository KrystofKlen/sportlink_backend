package com.sportlink.sportlink.location;

import com.sportlink.sportlink.account.company.CompanyAccount;
import com.sportlink.sportlink.reward.Reward;
import com.sportlink.sportlink.security.EncryptionUtil;
import com.sportlink.sportlink.verification.location.LOCATION_VERIFICATION_STRATEGY;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
@Entity
@AllArgsConstructor
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String address;
    private String description;

    @ElementCollection(fetch = FetchType.EAGER)
    private Set<ACTIVITY> activities;

    double longitude;
    double latitude;

    @ElementCollection(fetch = FetchType.EAGER)
    private Set<LOCATION_VERIFICATION_STRATEGY> verificationStrategies;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Reward> rewards;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> imagesUUID;

    @Column(unique = true)
    private String code;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private CompanyAccount issuer;

    public Location(Long id,
                    String name,
                    String address,
                    String description,
                    Set<ACTIVITY> activities,
                    double longitude,
                    double latitude,
                    Set<LOCATION_VERIFICATION_STRATEGY> verificationStrategies) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.description = description;
        this.activities = activities;
        this.longitude = longitude;
        this.latitude = latitude;
        this.verificationStrategies = verificationStrategies;
    }

    public Location() {
        code = EncryptionUtil.generateRandomSequence(10);
    }

}

