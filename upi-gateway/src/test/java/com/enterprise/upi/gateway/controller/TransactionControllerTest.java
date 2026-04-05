package com.enterprise.upi.gateway.controller;

import com.enterprise.upi.common.dto.TransactionRequest;
import com.enterprise.upi.gateway.producer.TransactionProducer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TransactionController.class)
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransactionProducer transactionProducer;

    @MockBean
    private org.springframework.data.redis.core.StringRedisTemplate redisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void processPayment_ShouldReturnAccepted() throws Exception {
        TransactionRequest request = TransactionRequest.builder()
                .transactionId(UUID.randomUUID().toString())
                .customerVpa("user@upi")
                .merchantVpa("merchant@bank")
                .amount(new BigDecimal("100.00"))
                .build();

        mockMvc.perform(post("/api/v1/upi/pay")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("PROCESSING"));

        verify(transactionProducer, times(1)).sendTransactionEvent(any());
    }
}
