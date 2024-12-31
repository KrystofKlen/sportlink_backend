package com.sportlink.sportlink.voucher;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;

    @ElementCollection
    private List<String> images;
}
