package com.enterprise.upi.common.exception;

public class UpiException extends RuntimeException {
    private final String errorCode;

    public UpiException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
