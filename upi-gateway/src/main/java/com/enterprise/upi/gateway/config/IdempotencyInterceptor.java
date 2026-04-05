package com.enterprise.upi.gateway.config;

import com.enterprise.upi.common.exception.UpiException;
import com.enterprise.upi.common.util.RedisConstants;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.concurrent.TimeUnit;

@Component
public class IdempotencyInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(IdempotencyInterceptor.class);
    private final StringRedisTemplate redisTemplate;

    public IdempotencyInterceptor(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String transactionId = request.getHeader("X-Transaction-Id");
        
        if (transactionId == null || transactionId.isEmpty()) {
            // Alternatively, extract from body, but header is more performant for interceptor
            return true; 
        }

        String key = RedisConstants.IDEMPOTENCY_PREFIX + transactionId;
        Boolean isNew = redisTemplate.opsForValue().setIfAbsent(key, "PROCESSING", 
                RedisConstants.IDEMPOTENCY_EXPIRY_MINUTES, TimeUnit.MINUTES);

        if (Boolean.FALSE.equals(isNew)) {
            log.warn("Duplicate transaction detected: {}", transactionId);
            throw new UpiException("Duplicate transaction request", "UPI-409");
        }

        return true;
    }
}
