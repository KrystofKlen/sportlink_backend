package com.sportlink.sportlink.account.company;

import com.sportlink.sportlink.account.ROLE;
import com.sportlink.sportlink.account.account.ACCOUNT_STATUS;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DTO_CompanyAccountDetails {
    private Long id;
    private String loginEmail;
    private String username;
    private ROLE role;
    private String profilePicUUID;
    private ACCOUNT_STATUS status;
    private String name;
    private String address;
    private String phone;
    private String contactEmail;
    private String websiteUrl;
}
