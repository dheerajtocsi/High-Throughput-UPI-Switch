package com.enterprise.upi.gateway.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    public static final String TRANSACTIONS_TOPIC = "upi.transactions.initiate";
    public static final String DLQ_TOPIC = "upi.transactions.dead-letter";

    @Bean
    public NewTopic transactionsTopic() {
        return TopicBuilder.name(TRANSACTIONS_TOPIC)
                .partitions(6) // Partitioning for high-throughput
                .replicas(1)   // In production, use 3
                .build();
    }

    @Bean
    public NewTopic deadLetterTopic() {
        return TopicBuilder.name(DLQ_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }
}
