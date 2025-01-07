package com.sportlink.sportlink.voucher;

import com.sportlink.sportlink.account.Account;
import com.sportlink.sportlink.currency.Currency;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Voucher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String description;

    @OneToOne(cascade = CascadeType.ALL)
    private Account issuer;

    @ManyToOne(cascade = CascadeType.ALL)
    private Currency currency;

    private Integer price;

    private LocalDate expirationDate;

    private VOUCHER_STATE state;

    private String code;
}
