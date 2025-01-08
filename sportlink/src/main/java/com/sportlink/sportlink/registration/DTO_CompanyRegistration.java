package com.sportlink.sportlink.registration;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DTO_CompanyRegistration {
    private String loginEmail;
    private String username;
    private String password;
    private String name;
    private String address;
    private String phone;
    private String contactEmail;
    private String websiteUrl;
}
