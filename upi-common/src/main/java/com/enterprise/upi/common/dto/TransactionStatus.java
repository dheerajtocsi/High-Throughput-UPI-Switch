package com.enterprise.upi.common.dto;

public enum TransactionStatus {
    PENDING,
    PROCESSING,
    SUCCESS,
    FAILED,
    RETRY_IN_PROGRESS,
    TIMED_OUT
}
