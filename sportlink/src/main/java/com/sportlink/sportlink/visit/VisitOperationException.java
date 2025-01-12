package com.sportlink.sportlink.visit;

import com.sportlink.sportlink.utils.RESULT_CODE;

public class VisitOperationException extends RuntimeException {
    private final RESULT_CODE resultCode;

    public VisitOperationException(String message, RESULT_CODE resultCode) {
        super(message);
        this.resultCode = resultCode;
    }

    public RESULT_CODE getResultCode() {
        return resultCode;
    }
}
