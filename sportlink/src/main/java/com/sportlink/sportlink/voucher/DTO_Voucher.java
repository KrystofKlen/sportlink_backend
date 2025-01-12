package com.sportlink.sportlink.voucher;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class DTO_Voucher {
    private Long id;
    @NotNull
    private String title;
    @NotNull
    private String description;

    @NotNull
    private String currency;
    @NotNull
    @Size(min = 1)
    private Integer price;
    @NotNull
    @Future
    private LocalDate expirationDate;

    private VOUCHER_STATE state;
    @NotNull
    private String code;

    private List<String> imagesUUIDs;
}
