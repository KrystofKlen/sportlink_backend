package com.sportlink.sportlink.account;

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
    private String image;

    public CompanyAccount(String loginEmail, String username, String passwordEncrypted, String salt,
                       String name, String address, String phone, String contactEmail,
                       String websiteUrl, String image) {
        // Call the all-args constructor of the Account class
        super(null, loginEmail, username, passwordEncrypted, salt, ROLE.COMPANY);

        // Initialize the fields specific to UserAccount
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.contactEmail = contactEmail;
        this.websiteUrl = websiteUrl;
        this.image = image;
    }
}
