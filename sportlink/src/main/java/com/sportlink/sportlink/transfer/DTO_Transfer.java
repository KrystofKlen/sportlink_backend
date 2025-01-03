package com.sportlink.sportlink.transfer;

import com.sportlink.sportlink.account.DTO_UserAccount;
import com.sportlink.sportlink.balance.DTO_Balance;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DTO_Transfer {
    private Long id;
    private DTO_UserAccount receiver;
    private LocalDateTime timestamp;
    private DTO_Balance balanceTransfered;
}
