package com.sportlink.sportlink.voucher;

import com.sportlink.sportlink.account.Account;
import com.sportlink.sportlink.account.user.UserAccount;
import com.sportlink.sportlink.currency.Currency;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

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

    @ManyToOne(fetch = FetchType.LAZY)
    private Account issuer;

    @ManyToOne(cascade = CascadeType.ALL)
    private Currency currency;

    private Integer price;

    private LocalDate expirationDate;

    @Enumerated(EnumType.STRING)
    private VOUCHER_STATE state;

    private String code;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> imagesUUID;

    @ManyToOne(fetch = FetchType.LAZY)
    private UserAccount buyer;
}
