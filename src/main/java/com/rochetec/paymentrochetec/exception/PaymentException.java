package com.rochetec.paymentrochetec.exception;

import com.rochetec.paymentrochetec.enums.PayApiResponseCode;
import java.time.LocalDateTime;

/**
 * 支付异常基类
 */
public class PaymentException extends RuntimeException {
    
    private final Integer errorCode;
    private final LocalDateTime timestamp;
    private final String requestId;
    
    public PaymentException(String message) {
        this(PayApiResponseCode.INTERNAL_ERROR.getCode(), message);
    }
    
    public PaymentException(Integer errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.timestamp = LocalDateTime.now();
        this.requestId = generateRequestId();
    }
    
    public PaymentException(String message, Throwable cause) {
        this(PayApiResponseCode.INTERNAL_ERROR.getCode(), message, cause);
    }
    
    public PaymentException(Integer errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.timestamp = LocalDateTime.now();
        this.requestId = generateRequestId();
    }
    
    public PaymentException(PayApiResponseCode responseCode) {
        this(responseCode.getCode(), responseCode.getMessage());
    }
    
    public PaymentException(PayApiResponseCode responseCode, String message) {
        this(responseCode.getCode(), message);
    }
    
    public PaymentException(PayApiResponseCode responseCode, Throwable cause) {
        this(responseCode.getCode(), responseCode.getMessage(), cause);
    }
    
    public Integer getErrorCode() {
        return errorCode;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public String getRequestId() {
        return requestId;
    }
    
    private String generateRequestId() {
        return String.format("PAY-%d", System.currentTimeMillis());
    }
    
    @Override
    public String toString() {
        return String.format("PaymentException[%d] %s (RequestId: %s, Time: %s)", 
            errorCode, getMessage(), requestId, timestamp);
    }
} 