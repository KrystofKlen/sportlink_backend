package com.sportlink.sportlink.visit;

import lombok.Data;

@Data
public class DTO_VisitRequest {
    Long locationId;
    Double userLatitude;
    Double userLongitude;
    String code;
}
