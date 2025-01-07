package com.sportlink.sportlink.account;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class DTO_UserAccount {
    private Long userId;
    private String username;
    private String firstName;
    private String lastName;
    private Date dateOfBirth;
    private String profilePic;
}
