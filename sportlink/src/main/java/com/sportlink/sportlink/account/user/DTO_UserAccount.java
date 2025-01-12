package com.sportlink.sportlink.account.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DTO_UserAccount {
    private Long userId;
    private String username;
    private String firstName;
    private String lastName;
    private Date dateOfBirth;
    private String profilePicUUID;
}
