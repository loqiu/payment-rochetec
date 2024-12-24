package com.rochetec.paymentrochetec.enums;

/**
 * 支付状态枚举
 */
public enum PaymentStatus {
    
    PENDING("PENDING", "待支付"),
    PROCESSING("PROCESSING", "处理中"),
    SUCCEEDED("SUCCEEDED", "支付成功"),
    FAILED("FAILED", "支付失败"),
    CANCELED("CANCELED", "已取消"),
    REFUNDED("REFUNDED", "已退款");
    
    private final String code;
    private final String description;
    
    PaymentStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getDescription() {
        return description;
    }
} 