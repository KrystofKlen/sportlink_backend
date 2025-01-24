package com.sportlink.sportlink.account.account;

import lombok.Data;

@Data
public class DTO_LoginResponse {
    String accessToken;
    String refreshToken;
    Long accountId;
}
