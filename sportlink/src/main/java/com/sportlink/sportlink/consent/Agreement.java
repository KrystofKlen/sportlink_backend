package com.sportlink.sportlink.consent;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
public class Agreement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    @Column(name = "agreement", nullable = false)
    private String agreement;

    private LocalDate startDate;

    private LocalDate endDate;
}
