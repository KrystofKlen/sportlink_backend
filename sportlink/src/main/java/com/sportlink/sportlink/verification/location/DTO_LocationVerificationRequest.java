package com.sportlink.sportlink.verification.location;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DTO_LocationVerificationRequest {
    Long userId;
    Long locationId;
    Double userLatitude;
    Double userLongitude;
    Double locationLatitude;
    Double locationLongitude;
    String code;
}
