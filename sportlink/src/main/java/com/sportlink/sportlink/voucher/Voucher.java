package com.sportlink.sportlink.voucher;

import com.sportlink.sportlink.account.Account;
import com.sportlink.sportlink.balance.Balance;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
public class Voucher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(cascade = CascadeType.ALL)
    private Item item;

    @OneToOne(cascade = CascadeType.ALL)
    private Balance price;

    @OneToOne(cascade = CascadeType.ALL)
    private Account issuer;

    private LocalDate expirationDate;

    private VOUCHER_STATE state;

    private String code;
}
