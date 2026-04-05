package com.enterprise.upi.gateway.producer;

import com.enterprise.upi.common.dto.TransactionEvent;
import com.enterprise.upi.gateway.config.KafkaConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class TransactionProducer {

    private static final Logger log = LoggerFactory.getLogger(TransactionProducer.class);
    private final KafkaTemplate<String, TransactionEvent> kafkaTemplate;

    public TransactionProducer(KafkaTemplate<String, TransactionEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendTransactionEvent(TransactionEvent event) {
        log.info("Producing transaction event for routing: {}", event.getTransactionId());
        kafkaTemplate.send(KafkaConfig.TRANSACTIONS_TOPIC, event.getTransactionId(), event)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.debug("Successfully sent transaction event to topic: {}", 
                            result.getRecordMetadata().topic());
                    } else {
                        log.error("Failed to send transaction event: {}", ex.getMessage());
                    }
                });
    }
}
