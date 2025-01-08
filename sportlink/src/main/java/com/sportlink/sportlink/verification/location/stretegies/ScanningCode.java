package com.sportlink.sportlink.verification.location.stretegies;

import com.sportlink.sportlink.verification.I_VerificationStrategy;
import com.sportlink.sportlink.verification.location.contexts.CodeScanContext;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ScanningCode implements I_VerificationStrategy {

    CodeScanContext context;

    @Override
    public boolean verify() {
        return context.getEntityIdScanned() == context.getEntityIdExpected();
    }
}
