package com.enterprise.upi.common.dto;

public class TransactionResponse {
    private String transactionId;
    private String status;
    private String responseCode;
    private String message;

    public TransactionResponse() {}

    public TransactionResponse(String transactionId, String status, String responseCode, String message) {
        this.transactionId = transactionId;
        this.status = status;
        this.responseCode = responseCode;
        this.message = message;
    }

    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getResponseCode() { return responseCode; }
    public void setResponseCode(String responseCode) { this.responseCode = responseCode; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public static TransactionResponseBuilder builder() {
        return new TransactionResponseBuilder();
    }

    public static class TransactionResponseBuilder {
        private String transactionId;
        private String status;
        private String responseCode;
        private String message;

        public TransactionResponseBuilder transactionId(String transactionId) {
            this.transactionId = transactionId;
            return this;
        }

        public TransactionResponseBuilder status(String status) {
            this.status = status;
            return this;
        }

        public TransactionResponseBuilder responseCode(String responseCode) {
            this.responseCode = responseCode;
            return this;
        }

        public TransactionResponseBuilder message(String message) {
            this.message = message;
            return this;
        }

        public TransactionResponse build() {
            return new TransactionResponse(transactionId, status, responseCode, message);
        }
    }
}
