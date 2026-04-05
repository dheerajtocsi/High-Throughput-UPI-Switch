package com.enterprise.upi.common.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransactionEvent implements Serializable {
    private String transactionId;
    private String customerVpa;
    private String merchantVpa;
    private BigDecimal amount;
    private String status;
    private LocalDateTime eventTimestamp;
    private int retryCount;

    public TransactionEvent() {}

    public TransactionEvent(String transactionId, String customerVpa, String merchantVpa, BigDecimal amount, String status, LocalDateTime eventTimestamp, int retryCount) {
        this.transactionId = transactionId;
        this.customerVpa = customerVpa;
        this.merchantVpa = merchantVpa;
        this.amount = amount;
        this.status = status;
        this.eventTimestamp = eventTimestamp;
        this.retryCount = retryCount;
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

    public static TransactionEventBuilder builder() {
        return new TransactionEventBuilder();
    }

    public static class TransactionEventBuilder {
        private String transactionId;
        private String customerVpa;
        private String merchantVpa;
        private BigDecimal amount;
        private String status;
        private LocalDateTime eventTimestamp;
        private int retryCount;

        public TransactionEventBuilder transactionId(String transactionId) {
            this.transactionId = transactionId;
            return this;
        }

        public TransactionEventBuilder customerVpa(String customerVpa) {
            this.customerVpa = customerVpa;
            return this;
        }

        public TransactionEventBuilder merchantVpa(String merchantVpa) {
            this.merchantVpa = merchantVpa;
            return this;
        }

        public TransactionEventBuilder amount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public TransactionEventBuilder status(String status) {
            this.status = status;
            return this;
        }

        public TransactionEventBuilder eventTimestamp(LocalDateTime eventTimestamp) {
            this.eventTimestamp = eventTimestamp;
            return this;
        }

        public TransactionEventBuilder retryCount(int retryCount) {
            this.retryCount = retryCount;
            return this;
        }

        public TransactionEvent build() {
            return new TransactionEvent(transactionId, customerVpa, merchantVpa, amount, status, eventTimestamp, retryCount);
        }
    }
}
