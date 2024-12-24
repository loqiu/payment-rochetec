package com.rochetec.paymentrochetec.exception;

/**
 * 支付错误码枚举
 */
public enum PaymentErrorCode {
    
    UNKNOWN(1000, "未知错误"),
    INVALID_AMOUNT(1001, "无效的支付金额"),
    INVALID_CURRENCY(1002, "无效的货币类型"),
    INVALID_PAYMENT_INTENT_ID(1003, "无效的支付意向ID"),
    INVALID_PAYMENT_STATUS(1004, "无效的支付状态"),
    PAYMENT_FAILED(1005, "支付失败"),
    SYSTEM_ERROR(500, "系统错误"),
    MISSING_REQUIRED_PARAMETERS(1006,"缺少必要参数"),;
    
    private final Integer code;
    private final String description;
    
    PaymentErrorCode(Integer code, String description) {
        this.code = code;
        this.description = description;
    }
    
    public Integer getCode() {
        return code;
    }
    
    public String getDescription() {
        return description;
    }
} 