package com.sportlink.sportlink.registration;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DTO_UserRegistration {
    private String loginEmail;
    private String username;
    private String salt;
    private String password;
    private String firstName;
    private String lastName;
    private Date dateOfBirth;
}
