package com.enterprise.upi.routing.consumer;

import com.enterprise.upi.common.dto.TransactionEvent;
import com.enterprise.upi.common.dto.TransactionStatus;
import com.enterprise.upi.routing.config.KafkaRetryConfig;
import com.enterprise.upi.routing.service.RoutingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class TransactionConsumer {

    private static final Logger log = LoggerFactory.getLogger(TransactionConsumer.class);
    private final RoutingService routingService;
    private final KafkaTemplate<String, TransactionEvent> kafkaTemplate;

    public TransactionConsumer(RoutingService routingService, KafkaTemplate<String, TransactionEvent> kafkaTemplate) {
        this.routingService = routingService;
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(topics = "upi.transactions.initiate", groupId = "upi-routing-group")
    public void consumeTransaction(TransactionEvent event) {
        log.info("Consumed transaction: {} for routing", event.getTransactionId());

        boolean success = routingService.routeToBank(event);

        if (success) {
            event.setStatus(TransactionStatus.SUCCESS.name());
            log.info("Transaction SUCCESS: {}", event.getTransactionId());
        } else {
            // Throwing exception triggers the Kafka retry/DLT mechanism
            log.error("Transaction FAILED, triggering retry for: {}", event.getTransactionId());
            throw new RuntimeException("Bank Routing Failed for " + event.getTransactionId());
        }

        // Send Status update event to Ledger service
        event.setEventTimestamp(LocalDateTime.now());
        kafkaTemplate.send(KafkaRetryConfig.STATUS_TOPIC, event.getTransactionId(), event);
    }

    /**
     * Listener for Dead Letter Topic (DLQ)
     */
    @KafkaListener(topics = "upi.transactions.initiate.DLT", groupId = "upi-routing-group")
    public void handleDlt(TransactionEvent event) {
        log.error("CRITICAL: Transaction {} exceeded retries and moved to DLT", event.getTransactionId());
        event.setStatus(TransactionStatus.FAILED.name());
        event.setEventTimestamp(LocalDateTime.now());
        kafkaTemplate.send(KafkaRetryConfig.STATUS_TOPIC, event.getTransactionId(), event);
    }
}
