package com.sportlink.sportlink.verification.location.stretegies;

import com.sportlink.sportlink.verification.I_VerificationStrategy;
import com.sportlink.sportlink.verification.location.contexts.VisitsLimitContext;
import com.sportlink.sportlink.visit.Visit;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class VisitsLimit implements I_VerificationStrategy {

    VisitsLimitContext context;

    @Override
    public boolean verify() {
        if( context.getVisitsToday() == null || context.getVisitsToday().isEmpty() ){
            return true;
        }
        List<Visit> visits = context.getVisitsToday();

        int cntToday = 0;
        for( Visit visit : visits ){
            if( visit.getLocation().getId() == context.getLocationId() ){
                cntToday++;
            }
        }

        return cntToday < context.getLimitPerDay();
    }
}
