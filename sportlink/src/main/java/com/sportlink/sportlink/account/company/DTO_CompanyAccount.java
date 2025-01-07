package com.sportlink.sportlink.account;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DTO_CompanyAccount {
    private String name;
    private String address;
    private String phone;
    private String contactEmail;
    private String websiteUrl;
    private String image;
}
