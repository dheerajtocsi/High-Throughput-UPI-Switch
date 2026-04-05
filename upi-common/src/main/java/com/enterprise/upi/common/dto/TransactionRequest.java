package com.enterprise.upi.common.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransactionRequest implements Serializable {
    private String transactionId;
    private String merchantId;
    private String customerVpa;
    private String merchantVpa;
    private BigDecimal amount;
    private String currency; // Default INR
    private String remarks;
    private LocalDateTime timestamp;

    public TransactionRequest() {}

    public TransactionRequest(String transactionId, String merchantId, String customerVpa, String merchantVpa, BigDecimal amount, String currency, String remarks, LocalDateTime timestamp) {
        this.transactionId = transactionId;
        this.merchantId = merchantId;
        this.customerVpa = customerVpa;
        this.merchantVpa = merchantVpa;
        this.amount = amount;
        this.currency = currency;
        this.remarks = remarks;
        this.timestamp = timestamp;
    }

    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }

    public String getMerchantId() { return merchantId; }
    public void setMerchantId(String merchantId) { this.merchantId = merchantId; }

    public String getCustomerVpa() { return customerVpa; }
    public void setCustomerVpa(String customerVpa) { this.customerVpa = customerVpa; }

    public String getMerchantVpa() { return merchantVpa; }
    public void setMerchantVpa(String merchantVpa) { this.merchantVpa = merchantVpa; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public static TransactionRequestBuilder builder() {
        return new TransactionRequestBuilder();
    }

    public static class TransactionRequestBuilder {
        private String transactionId;
        private String merchantId;
        private String customerVpa;
        private String merchantVpa;
        private BigDecimal amount;
        private String currency;
        private String remarks;
        private LocalDateTime timestamp;

        public TransactionRequestBuilder transactionId(String transactionId) {
            this.transactionId = transactionId;
            return this;
        }

        public TransactionRequestBuilder merchantId(String merchantId) {
            this.merchantId = merchantId;
            return this;
        }

        public TransactionRequestBuilder customerVpa(String customerVpa) {
            this.customerVpa = customerVpa;
            return this;
        }

        public TransactionRequestBuilder merchantVpa(String merchantVpa) {
            this.merchantVpa = merchantVpa;
            return this;
        }

        public TransactionRequestBuilder amount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public TransactionRequestBuilder currency(String currency) {
            this.currency = currency;
            return this;
        }

        public TransactionRequestBuilder remarks(String remarks) {
            this.remarks = remarks;
            return this;
        }

        public TransactionRequestBuilder timestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public TransactionRequest build() {
            return new TransactionRequest(transactionId, merchantId, customerVpa, merchantVpa, amount, currency, remarks, timestamp);
        }
    }
}
