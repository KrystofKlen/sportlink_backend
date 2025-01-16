package com.sportlink.sportlink.currency;

import com.sportlink.sportlink.account.account.Account;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.Data;

@Entity
@Data
public class Currency {

    @Id
    private String name;

    @OneToOne(cascade = CascadeType.ALL)
    private Account issuer;
}
