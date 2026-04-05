package com.enterprise.upi.gateway.controller;

import com.enterprise.upi.common.dto.GenericResponse;
import com.enterprise.upi.common.dto.TransactionEvent;
import com.enterprise.upi.common.dto.TransactionRequest;
import com.enterprise.upi.common.dto.TransactionResponse;
import com.enterprise.upi.common.dto.TransactionStatus;
import com.enterprise.upi.gateway.producer.TransactionProducer;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/upi")
public class TransactionController {

    private static final Logger log = LoggerFactory.getLogger(TransactionController.class);
    private final TransactionProducer transactionProducer;

    public TransactionController(TransactionProducer transactionProducer) {
        this.transactionProducer = transactionProducer;
    }

    @PostMapping("/pay")
    public ResponseEntity<GenericResponse<TransactionResponse>> processPayment(
            @Valid @RequestBody TransactionRequest request) {
        
        log.info("Received UPI Pay request: {}", request.getTransactionId());

        // Create Kafka Event
        TransactionEvent event = TransactionEvent.builder()
                .transactionId(request.getTransactionId())
                .customerVpa(request.getCustomerVpa())
                .merchantVpa(request.getMerchantVpa())
                .amount(request.getAmount())
                .status(TransactionStatus.PROCESSING.name())
                .eventTimestamp(LocalDateTime.now())
                .retryCount(0)
                .build();

        // Async Produce to Kafka
        transactionProducer.sendTransactionEvent(event);

        TransactionResponse response = TransactionResponse.builder()
                .transactionId(request.getTransactionId())
                .status(TransactionStatus.PROCESSING.name())
                .message("Transaction is being processed asynchronously")
                .responseCode("00")
                .build();

        return ResponseEntity.accepted().body(GenericResponse.success(response));
    }
}
