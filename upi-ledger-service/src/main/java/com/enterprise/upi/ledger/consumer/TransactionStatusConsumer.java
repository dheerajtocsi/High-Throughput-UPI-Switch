package com.enterprise.upi.ledger.consumer;

import com.enterprise.upi.common.dto.TransactionEvent;
import com.enterprise.upi.ledger.service.LedgerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class TransactionStatusConsumer {

    private static final Logger log = LoggerFactory.getLogger(TransactionStatusConsumer.class);
    private final LedgerService ledgerService;

    public TransactionStatusConsumer(LedgerService ledgerService) {
        this.ledgerService = ledgerService;
    }

    @KafkaListener(topics = "upi.transactions.status", groupId = "upi-ledger-group")
    public void consumeStatus(TransactionEvent event) {
        log.info("Ledger Consumed status for transaction: {} [Status: {}]", 
            event.getTransactionId(), event.getStatus());
        
        try {
            ledgerService.processStatusUpdate(event);
        } catch (Exception e) {
            log.error("Failed to update ledger for transaction: {}.", event.getTransactionId(), e);
        }
    }
}
