package com.sportlink.sportlink.verification.location.contexts;

import lombok.Data;

@Data
public class OneTimeCodeContext {
    long userIdInCode;
    long locationIdInCode;
    long userId;
    long locationId;
}
