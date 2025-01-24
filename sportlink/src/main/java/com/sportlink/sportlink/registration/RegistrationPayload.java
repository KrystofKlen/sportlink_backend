package com.sportlink.sportlink.registration;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class RegistrationPayload {
    private String loginEmail;
    private String username;
    private String password;
    private String firstName;
    private String lastName;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date dateOfBirth;
}
