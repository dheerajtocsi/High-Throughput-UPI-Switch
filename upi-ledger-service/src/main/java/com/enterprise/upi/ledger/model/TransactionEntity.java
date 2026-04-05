package com.enterprise.upi.ledger.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@Entity
@Table(name = "upi_transactions")
public class TransactionEntity {

    @Id
    @Column(name = "transaction_id", nullable = false)
    private String transactionId;

    @Column(name = "customer_vpa", nullable = false)
    private String customerVpa;

    @Column(name = "merchant_vpa", nullable = false)
    private String merchantVpa;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "event_timestamp", nullable = false)
    private LocalDateTime eventTimestamp;

    @Column(name = "retry_count")
    private int retryCount;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public TransactionEntity() {}

    public TransactionEntity(String transactionId, String customerVpa, String merchantVpa, BigDecimal amount, String status, LocalDateTime eventTimestamp, int retryCount, LocalDateTime createdAt) {
        this.transactionId = transactionId;
        this.customerVpa = customerVpa;
        this.merchantVpa = merchantVpa;
        this.amount = amount;
        this.status = status;
        this.eventTimestamp = eventTimestamp;
        this.retryCount = retryCount;
        this.createdAt = createdAt;
    }

    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }

    public String getCustomerVpa() { return customerVpa; }
    public void setCustomerVpa(String customerVpa) { this.customerVpa = customerVpa; }

    public String getMerchantVpa() { return merchantVpa; }
    public void setMerchantVpa(String merchantVpa) { this.merchantVpa = merchantVpa; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getEventTimestamp() { return eventTimestamp; }
    public void setEventTimestamp(LocalDateTime eventTimestamp) { this.eventTimestamp = eventTimestamp; }

    public int getRetryCount() { return retryCount; }
    public void setRetryCount(int retryCount) { this.retryCount = retryCount; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public static TransactionEntityBuilder builder() {
        return new TransactionEntityBuilder();
    }

    public static class TransactionEntityBuilder {
        private String transactionId;
        private String customerVpa;
        private String merchantVpa;
        private BigDecimal amount;
        private String status;
        private LocalDateTime eventTimestamp;
        private int retryCount;
        private LocalDateTime createdAt;

        public TransactionEntityBuilder transactionId(String transactionId) {
            this.transactionId = transactionId;
            return this;
        }

        public TransactionEntityBuilder customerVpa(String customerVpa) {
            this.customerVpa = customerVpa;
            return this;
        }

        public TransactionEntityBuilder merchantVpa(String merchantVpa) {
            this.merchantVpa = merchantVpa;
            return this;
        }

        public TransactionEntityBuilder amount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public TransactionEntityBuilder status(String status) {
            this.status = status;
            return this;
        }

        public TransactionEntityBuilder eventTimestamp(LocalDateTime eventTimestamp) {
            this.eventTimestamp = eventTimestamp;
            return this;
        }

        public TransactionEntityBuilder retryCount(int retryCount) {
            this.retryCount = retryCount;
            return this;
        }

        public TransactionEntityBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public TransactionEntity build() {
            return new TransactionEntity(transactionId, customerVpa, merchantVpa, amount, status, eventTimestamp, retryCount, createdAt);
        }
    }
}
