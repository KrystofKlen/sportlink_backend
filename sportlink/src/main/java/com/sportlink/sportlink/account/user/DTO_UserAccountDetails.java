package com.sportlink.sportlink.account.user;

import com.sportlink.sportlink.account.ROLE;
import com.sportlink.sportlink.account.account.ACCOUNT_STATUS;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DTO_UserAccountDetails {
    private Long id;
    private String loginEmail;
    private String username;
    private ROLE role;
    private String profilePicUUID;
    private ACCOUNT_STATUS status;
    private String firstName;
    private String lastName;
    private Date dateOfBirth;
}
