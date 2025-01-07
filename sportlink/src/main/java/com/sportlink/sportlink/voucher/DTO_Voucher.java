package com.sportlink.sportlink.voucher;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class DTO_Voucher {
    private long id;
    private String title;
    private String description;
    private String currency;
    private int price;
    private LocalDate expirationDate;
}
