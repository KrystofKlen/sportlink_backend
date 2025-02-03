package com.sportlink.sportlink.voucher;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RedeemRequest {
    Long voucherId;
    String voucherCode;
    String otp;
}
