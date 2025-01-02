package com.sportlink.sportlink.codes;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class CodeData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long exp;

    private Long idEntity;

    @Column(unique = true)
    private String code;

    @Column(columnDefinition = "json")
    private String payload;
}
