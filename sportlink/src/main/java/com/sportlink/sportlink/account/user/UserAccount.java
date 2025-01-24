package com.sportlink.sportlink.account.user;

import com.sportlink.sportlink.account.account.ACCOUNT_STATUS;
import com.sportlink.sportlink.account.account.Account;
import com.sportlink.sportlink.account.ROLE;
import com.sportlink.sportlink.currency.Currency;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Map;

@Entity
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class UserAccount extends Account {

    private String firstName;
    private String lastName;
    private Date dateOfBirth;

    public UserAccount(String loginEmail, String username, String passwordEncrypted,
                       String firstName, String lastName, Date dateOfBirth) {
        // Call the all-args constructor of the Account class
        super(null,loginEmail, username, passwordEncrypted, ROLE.ROLE_USER, null, ACCOUNT_STATUS.ACTIVE);

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
