package com.sportlink.sportlink.voucher;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class DTO_Voucher {
    private long id;
    @NotNull
    private String title;
    @NotNull
    private String description;
    @NotNull
    private String currency;
    @NotNull
    @Size(min = 1)
    private int price;
    @NotNull
    @Future
    private LocalDate expirationDate;

    private VOUCHER_STATE state;
    @NotNull
    private String code;
}
