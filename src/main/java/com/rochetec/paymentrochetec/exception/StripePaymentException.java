package com.rochetec.paymentrochetec.exception;

import com.stripe.exception.StripeException;
import com.rochetec.paymentrochetec.enums.PayApiResponseCode;

/**
 * Stripe支付异常类
 */
public class StripePaymentException extends PaymentException {
    
    private final Integer stripeErrorCode;
    private final String stripeErrorType;
    
    public StripePaymentException(PayApiResponseCode responseCode, String message) {
        super(responseCode.getCode(), message);
        this.stripeErrorCode = null;
        this.stripeErrorType = null;
    }
    
    public StripePaymentException(Integer errorCode, String message) {
        super(errorCode, message);
        this.stripeErrorCode = null;
        this.stripeErrorType = null;
    }
    
    public StripePaymentException(StripeException e) {
        super(mapStripeErrorCode(e), 
            String.format("Stripe支付错误: %s (错误码: %d)", e.getMessage(), parseStripeErrorCode(e.getCode())), 
            e);
        this.stripeErrorCode = parseStripeErrorCode(e.getCode());
        this.stripeErrorType = e.getStripeError().getType();
    }
    
    /**
     * 将Stripe错误码映射为系统错误码
     */
    private static int mapStripeErrorCode(StripeException e) {
        String errorType = e.getStripeError().getType();
        switch (errorType) {
            case "card_error":
                return PayApiResponseCode.STRIPE_CARD_ERROR.getCode();
            case "authentication_error":
                return PayApiResponseCode.STRIPE_AUTHENTICATION_ERROR.getCode();
            case "invalid_request_error":
                return PayApiResponseCode.STRIPE_INVALID_REQUEST.getCode();
            case "rate_limit_error":
                return PayApiResponseCode.STRIPE_RATE_LIMIT.getCode();
            default:
                return PayApiResponseCode.STRIPE_API_ERROR.getCode();
        }
    }
    
    /**
     * 解析Stripe错误码为Integer
     * Stripe错误码格式可能是：payment_intent_authentication_failure
     * 我们将其转换为数字编码：20001, 20002等
     */
    private static Integer parseStripeErrorCode(String stripeCode) {
        if (stripeCode == null) {
            return 20000; // 默认Stripe错误码
        }
        
        // 基础错误码
        int baseCode = 20000;
        
        // 根据错误类型返回不同的错误码
        switch (stripeCode) {
            case "payment_intent_authentication_failure":
                return baseCode + 1;
            case "payment_intent_payment_failure":
                return baseCode + 2;
            case "payment_intent_invalid_parameter":
                return baseCode + 3;
            case "payment_method_invalid":
                return baseCode + 4;
            case "payment_intent_unexpected_state":
                return baseCode + 5;
            case "payment_intent_payment_attempt_failed":
                return baseCode + 6;
            case "card_declined":
                return baseCode + 7;
            case "expired_card":
                return baseCode + 8;
            case "incorrect_cvc":
                return baseCode + 9;
            case "processing_error":
                return baseCode + 10;
            default:
                return baseCode;
        }
    }
    
    public Integer getStripeErrorCode() {
        return stripeErrorCode;
    }
    
    public String getStripeErrorType() {
        return stripeErrorType;
    }
    
    @Override
    public String toString() {
        if (stripeErrorCode != null) {
            return String.format("StripePaymentException[%d] %s (StripeError: %d, Type: %s, RequestId: %s, Time: %s)", 
                getErrorCode(), getMessage(), stripeErrorCode, stripeErrorType, getRequestId(), getTimestamp());
        }
        return super.toString();
    }
} 