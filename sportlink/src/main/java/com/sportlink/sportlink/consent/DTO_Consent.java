package com.sportlink.sportlink.consent;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class DTO_Consent {

    private Long accountId;

    private String agreement;

    private LocalDateTime startDate;

    private LocalDateTime endDate;
}
