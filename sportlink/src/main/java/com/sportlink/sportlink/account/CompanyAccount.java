package com.sportlink.sportlink.account;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class CompanyAccount extends Account {

    private String name;
    private String address;
    private String phone;
    private String contactEmail;
    private String websiteUrl;
    private String image;

    public CompanyAccount() {
        super();
        super.setRole(ROLE.COMPANY);
    }
}
