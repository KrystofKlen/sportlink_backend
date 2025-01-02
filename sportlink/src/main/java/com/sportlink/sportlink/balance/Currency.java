package com.sportlink.sportlink.balance;

import com.sportlink.sportlink.account.Account;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Currency {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private CURRENCY name;

    private String image;

    @OneToOne(cascade = CascadeType.ALL)
    private Account issuer;
}
