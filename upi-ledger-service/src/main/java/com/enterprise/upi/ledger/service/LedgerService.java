package com.enterprise.upi.ledger.service;

import com.enterprise.upi.common.dto.TransactionEvent;
import com.enterprise.upi.common.util.RedisConstants;
import com.enterprise.upi.ledger.model.TransactionEntity;
import com.enterprise.upi.ledger.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class LedgerService {

    private static final Logger log = LoggerFactory.getLogger(LedgerService.class);
    private final TransactionRepository transactionRepository;
    private final StringRedisTemplate redisTemplate;

    public LedgerService(TransactionRepository transactionRepository, StringRedisTemplate redisTemplate) {
        this.transactionRepository = transactionRepository;
        this.redisTemplate = redisTemplate;
    }

    @Transactional
    public void processStatusUpdate(TransactionEvent event) {
        log.info("Processing ledger update for transaction: {}", event.getTransactionId());

        TransactionEntity entity = TransactionEntity.builder()
                .transactionId(event.getTransactionId())
                .customerVpa(event.getCustomerVpa())
                .merchantVpa(event.getMerchantVpa())
                .amount(event.getAmount())
                .status(event.getStatus())
                .eventTimestamp(event.getEventTimestamp())
                .retryCount(event.getRetryCount())
                .createdAt(LocalDateTime.now())
                .build();

        // ACID Persistence in Oracle
        transactionRepository.save(entity);

        // Update Redis Cache for Balance Optimization (Simulated)
        if ("SUCCESS".equals(event.getStatus())) {
            updateBalanceCache(event);
        }
    }

    private void updateBalanceCache(TransactionEvent event) {
        String key = RedisConstants.BALANCE_PREFIX + event.getCustomerVpa();
        // In a real system, we'd decrement balance. Here we just update a 'last_updated' timestamp
        redisTemplate.opsForValue().set(key, LocalDateTime.now().toString());
        log.debug("Balance cache updated for: {}", event.getCustomerVpa());
    }
}
