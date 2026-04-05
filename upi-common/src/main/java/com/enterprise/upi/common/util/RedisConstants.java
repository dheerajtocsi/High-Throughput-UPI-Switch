package com.enterprise.upi.common.util;

public class RedisConstants {
    public static final String IDEMPOTENCY_PREFIX = "upi:idempotency:";
    public static final String BALANCE_PREFIX = "upi:balance:";
    public static final long IDEMPOTENCY_EXPIRY_MINUTES = 30;
}
