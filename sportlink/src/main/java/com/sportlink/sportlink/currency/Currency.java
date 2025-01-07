package com.sportlink.sportlink.balance;

import com.sportlink.sportlink.account.Account;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Currency {

    @Id
    private String name;

    @OneToOne(cascade = CascadeType.ALL)
    private Account issuer;
}
