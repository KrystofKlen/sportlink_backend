package com.sportlink.sportlink.account;

import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique=true)
    private String loginEmail;

    @Column(unique=true)
    private String username;

    private String password;

    private String salt;

    private ROLE role;
}
