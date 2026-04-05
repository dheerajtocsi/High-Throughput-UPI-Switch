package com.enterprise.upi.gateway;

import com.enterprise.upi.common.dto.TransactionStatus;
import com.enterprise.upi.common.dto.TransactionRequest;
import com.enterprise.upi.gateway.producer.TransactionProducer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * End-to-End System Integration Test proving the logic for:
 * 1. Gateway REST Reception
 * 2. Kafka Event Generation
 * 3. Topic Message Flow
 * 
 * This test uses Embedded Kafka to bypass the sandbox Docker restrictions.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}")
@AutoConfigureMockMvc
@EmbeddedKafka(partitions = 1, topics = { "upi.transactions.initiate", "upi.transactions.status" })
public class E2ESystemIntegrationTest {

    private static final Logger log = LoggerFactory.getLogger(E2ESystemIntegrationTest.class);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private StringRedisTemplate redisTemplate;

    private static final java.util.concurrent.BlockingQueue<com.enterprise.upi.common.dto.TransactionEvent> records = new java.util.concurrent.LinkedBlockingQueue<>();

    @org.springframework.kafka.annotation.KafkaListener(topics = "upi.transactions.initiate", groupId = "test-group", properties = {
            "auto.offset.reset=earliest" })
    public void listen(String message) throws Exception {
        log.info("Test Listener received raw message: {}", message);
        com.enterprise.upi.common.dto.TransactionEvent event = objectMapper.readValue(message,
                com.enterprise.upi.common.dto.TransactionEvent.class);
        records.add(event);
    }

    @Test
    void fullTransactionLifecycle_LogicVerification() throws Exception {
        // Step 1: Create a transaction request
        String txnId = UUID.randomUUID().toString();
        TransactionRequest request = TransactionRequest.builder()
                .transactionId(txnId)
                .customerVpa("dheeraj.kumar@finneonet")
                .merchantVpa("test@upi")
                .amount(new BigDecimal("500.00"))
                .build();

        // Step 2: Trigger the Gateway REST API
        mockMvc.perform(post("/api/v1/upi/pay")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value(TransactionStatus.PROCESSING.name()));

        // Step 3: Verify the Kafka Topic received the message (The "Full Flow" proof)
        com.enterprise.upi.common.dto.TransactionEvent received = records.poll(10,
                java.util.concurrent.TimeUnit.SECONDS);

        org.junit.jupiter.api.Assertions.assertNotNull(received, "The message did not reach the Kafka topic!");
        org.junit.jupiter.api.Assertions.assertEquals(txnId, received.getTransactionId());
        log.info("Full flow verified: REST -> Gateway -> Kafka Topic");
    }
}
