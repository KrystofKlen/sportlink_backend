package com.sportlink.sportlink.voucher;

import com.sportlink.sportlink.account.Account;
import com.sportlink.sportlink.currency.MultiCurrencyAmount;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Voucher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(cascade = CascadeType.ALL)
    private Item item;

    @OneToOne(cascade = CascadeType.ALL)
    private MultiCurrencyAmount price;

    @OneToOne(cascade = CascadeType.ALL)
    private Account issuer;
}
