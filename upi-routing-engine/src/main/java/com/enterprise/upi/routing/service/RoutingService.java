package com.enterprise.upi.routing.service;

import com.enterprise.upi.common.dto.TransactionEvent;
import com.enterprise.upi.common.dto.TransactionStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class RoutingService {

    private static final Logger log = LoggerFactory.getLogger(RoutingService.class);

    private final Random random = new Random();

    /**
     * Simulates routing to an external PSP or Bank.
     * Includes a simulated high-load failure rate to demonstrate retry resilience.
     */
    public boolean routeToBank(TransactionEvent event) {
        log.info("Routing transaction {} to destination bank...", event.getTransactionId());
        
        // Simulate network latency (consistent with sub-200ms objective)
        try {
            Thread.sleep(50); 
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Simulate a 10% failure rate for demonstration of retries
        if (random.nextInt(100) < 10) {
            log.error("Bank gateway timeout/failure for transaction: {}", event.getTransactionId());
            return false;
        }

        log.info("Bank response SUCCESS for transaction: {}", event.getTransactionId());
        return true;
    }
}
