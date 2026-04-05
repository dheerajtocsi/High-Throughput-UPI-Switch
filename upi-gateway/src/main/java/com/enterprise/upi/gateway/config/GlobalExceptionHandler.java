package com.enterprise.upi.gateway.config;

import com.enterprise.upi.common.dto.GenericResponse;
import com.enterprise.upi.common.exception.UpiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(UpiException.class)
    public ResponseEntity<GenericResponse<Void>> handleUpiException(UpiException ex) {
        log.error("UPI Exception: {} - {}", ex.getErrorCode(), ex.getMessage());
        HttpStatus status = ex.getErrorCode().equals("UPI-409") ? HttpStatus.CONFLICT : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(GenericResponse.error(ex.getMessage(), ex.getErrorCode()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<GenericResponse<Void>> handleGenericException(Exception ex) {
        log.error("Internal Server Error: ", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(GenericResponse.error("Internal server error occurred", "UPI-500"));
    }
}
