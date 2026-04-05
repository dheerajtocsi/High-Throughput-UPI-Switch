package com.enterprise.upi.routing.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
public class KafkaRetryConfig {

    private static final Logger log = LoggerFactory.getLogger(KafkaRetryConfig.class);

    public static final String STATUS_TOPIC = "upi.transactions.status";

    @Bean
    public CommonErrorHandler errorHandler(KafkaTemplate<Object, Object> template) {
        // Retry logic: 3 attempts with 2s interval
        return new DefaultErrorHandler(
                new DeadLetterPublishingRecoverer(template),
                new FixedBackOff(2000L, 3)
        );
    }
}
