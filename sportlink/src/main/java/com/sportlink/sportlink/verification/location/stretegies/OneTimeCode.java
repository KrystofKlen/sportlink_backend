package com.sportlink.sportlink.verification.location.stretegies;

import com.sportlink.sportlink.verification.I_VerificationStrategy;
import com.sportlink.sportlink.verification.location.contexts.OneTimeCodeContext;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class OneTimeCode implements I_VerificationStrategy {

    OneTimeCodeContext context;

    @Override
    public boolean verify() {
        return context.getLocationId() == context.getLocationIdInCode()
                && context.getUserId() == context.getUserIdInCode();
    }
}
