package com.sportlink.sportlink.account.user;

import com.sportlink.sportlink.account.ACCOUNT_STATUS;
import com.sportlink.sportlink.account.Account;
import com.sportlink.sportlink.account.ROLE;
import com.sportlink.sportlink.currency.Currency;
import com.sportlink.sportlink.transfer.Transfer;
import com.sportlink.sportlink.visit.Visit;
import com.sportlink.sportlink.voucher.Voucher;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Entity
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class UserAccount extends Account {

    private String firstName;
    private String lastName;
    private Date dateOfBirth;

    public UserAccount(String loginEmail, String username, String passwordEncrypted, String salt,
                       String firstName, String lastName, Date dateOfBirth) {
        // Call the all-args constructor of the Account class
        super(null,loginEmail, username, passwordEncrypted, salt, ROLE.USER, null, ACCOUNT_STATUS.ACTIVE);

        // Initialize the fields specific to UserAccount
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
    }

    @ElementCollection
    @CollectionTable(
            name = "user_balance",
            joinColumns = @JoinColumn(name = "user_inventory_id")
    )
    @MapKeyJoinColumn(name = "currency_id")
    @Column(name = "balance")
    private Map<Currency, Integer> balance;

}
