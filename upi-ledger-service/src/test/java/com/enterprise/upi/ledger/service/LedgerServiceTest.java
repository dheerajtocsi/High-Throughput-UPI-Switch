package com.enterprise.upi.ledger.service;

import com.enterprise.upi.common.dto.TransactionEvent;
import com.enterprise.upi.common.dto.TransactionStatus;
import com.enterprise.upi.ledger.model.TransactionEntity;
import com.enterprise.upi.ledger.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LedgerServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private LedgerService ledgerService;

    @Test
    void processStatusUpdate_ShouldSaveToRepositoryAndCache_WhenSuccess() {
        TransactionEvent event = TransactionEvent.builder()
                .transactionId("TXN123")
                .customerVpa("user@upi")
                .merchantVpa("merchant@bank")
                .amount(new BigDecimal("100.00"))
                .status(TransactionStatus.SUCCESS.name())
                .eventTimestamp(LocalDateTime.now())
                .build();

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        ledgerService.processStatusUpdate(event);

        verify(transactionRepository, times(1)).save(any(TransactionEntity.class));
        verify(valueOperations, times(1)).set(anyString(), anyString());
    }
}
