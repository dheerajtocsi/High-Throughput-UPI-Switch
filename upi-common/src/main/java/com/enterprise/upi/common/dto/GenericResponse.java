package com.enterprise.upi.common.dto;

public class GenericResponse<T> {
    private boolean success;
    private String message;
    private String errorCode;
    private T data;

    public GenericResponse() {}

    public GenericResponse(boolean success, String message, String errorCode, T data) {
        this.success = success;
        this.message = message;
        this.errorCode = errorCode;
        this.data = data;
    }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getErrorCode() { return errorCode; }
    public void setErrorCode(String errorCode) { this.errorCode = errorCode; }

    public T getData() { return data; }
    public void setData(T data) { this.data = data; }

    public static <T> GenericResponse<T> success(T data) {
        return new GenericResponseBuilder<T>()
                .success(true)
                .message("Operation successful")
                .data(data)
                .build();
    }

    public static <T> GenericResponse<T> error(String message, String errorCode) {
        return new GenericResponseBuilder<T>()
                .success(false)
                .message(message)
                .errorCode(errorCode)
                .build();
    }

    public static <T> GenericResponseBuilder<T> builder() {
        return new GenericResponseBuilder<>();
    }

    public static class GenericResponseBuilder<T> {
        private boolean success;
        private String message;
        private String errorCode;
        private T data;

        public GenericResponseBuilder<T> success(boolean success) {
            this.success = success;
            return this;
        }

        public GenericResponseBuilder<T> message(String message) {
            this.message = message;
            return this;
        }

        public GenericResponseBuilder<T> errorCode(String errorCode) {
            this.errorCode = errorCode;
            return this;
        }

        public GenericResponseBuilder<T> data(T data) {
            this.data = data;
            return this;
        }

        public GenericResponse<T> build() {
            return new GenericResponse<>(success, message, errorCode, data);
        }
    }
}
