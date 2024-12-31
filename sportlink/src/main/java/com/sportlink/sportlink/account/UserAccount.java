package com.sportlink.sportlink.account;

import jakarta.persistence.Entity;
import lombok.Data;

import java.util.Date;

@Entity
@Data
public class UserAccount extends Account {

    private String firstName;
    private String lastName;
    private Date dateOfBirth;
    private String profilePic;

    public UserAccount() {
        super();
        super.setRole(ROLE.USER);
    }
}
