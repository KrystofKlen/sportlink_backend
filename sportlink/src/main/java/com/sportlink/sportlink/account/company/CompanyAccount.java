package com.sportlink.sportlink.account.company;

import com.sportlink.sportlink.account.account.ACCOUNT_STATUS;
import com.sportlink.sportlink.account.account.Account;
import com.sportlink.sportlink.account.ROLE;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class CompanyAccount extends Account {

    private String name;
    private String address;
    private String phone;
    private String contactEmail;
    private String websiteUrl;

    public CompanyAccount(String loginEmail, String username, String passwordEncrypted,
                          String name, String address, String phone, String contactEmail,
                          String websiteUrl) {
        // Call the all-args constructor of the Account class
        super(null, loginEmail, username, passwordEncrypted, ROLE.ROLE_COMPANY,"", ACCOUNT_STATUS.NOT_APPROVED);

        // Initialize the fields specific to UserAccount
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.contactEmail = contactEmail;
        this.websiteUrl = websiteUrl;
    }
}
