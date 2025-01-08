package com.sportlink.sportlink.verification.reward.conditions;

import com.sportlink.sportlink.verification.I_VerificationStrategy;
import com.sportlink.sportlink.verification.reward.contexts.ComparisonContext;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ClaimLimit implements I_VerificationStrategy{

    ComparisonContext context;

    @Override
    public boolean verify() {
        return context.getGiven() < context.getBoundary();
    }
}
