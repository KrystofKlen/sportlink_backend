package com.sportlink.sportlink.account;

import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class UserAccount extends Account {

    private String firstName;
    private String lastName;
    private Date dateOfBirth;
    private String profilePic;

    public UserAccount(String loginEmail, String username, String passwordEncrypted, String salt,
                       String firstName, String lastName, Date dateOfBirth, String profilePic) {
        // Call the all-args constructor of the Account class
        super(null,loginEmail, username, passwordEncrypted, salt, ROLE.USER);

        // Initialize the fields specific to UserAccount
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.profilePic = profilePic;
    }
}
