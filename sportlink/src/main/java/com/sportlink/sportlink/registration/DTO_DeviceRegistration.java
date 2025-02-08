package com.sportlink.sportlink.registration;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class DTO_DeviceRegistration {
    String loginEmail;
    String username;
    String password;
    Long locationId;
}
