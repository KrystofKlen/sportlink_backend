package com.sportlink.sportlink.voucher;

import com.sportlink.sportlink.balance.DTO_Balance;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class DTO_Voucher {
    private Long id;
    private DTO_Item item;
    private DTO_Balance price;
    private LocalDate expirationDate;
}
