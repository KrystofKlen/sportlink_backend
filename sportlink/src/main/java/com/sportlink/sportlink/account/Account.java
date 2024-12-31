package com.sportlink.sportlink.account;

import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private Long id;

    @Column(unique=true)
    @Getter
    private String loginEmail;

    @Column(unique=true)
    @Getter
    private String username;

    private String password;
    private String salt;

    @Getter
    @Setter
    private ROLE role;
}
