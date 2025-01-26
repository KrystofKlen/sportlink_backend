package com.sportlink.sportlink.account.account;

import com.sportlink.sportlink.account.ROLE;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    @Enumerated(EnumType.STRING)
    private ROLE role;

    private String profilePicUUID;

    @Enumerated(EnumType.STRING)
    private ACCOUNT_STATUS status;
}
