package com.rochetec.paymentrochetec.enums;

/**
 * API响应状态码枚举
 */
public enum PayApiResponseCode {
    
    SUCCESS(200, "操作成功"),
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未授权"),
    FORBIDDEN(403, "禁止访问"),
    NOT_FOUND(404, "资源不存在"),
    INTERNAL_ERROR(500, "系统内部错误"),
    
    // 支付相关错误码 (1000-1999)
    PAYMENT_AMOUNT_INVALID(1001, "支付金额无效"),
    PAYMENT_CURRENCY_INVALID(1002, "支付货币类型无效"),
    PAYMENT_METHOD_INVALID(1003, "支付方式无效"),
    PAYMENT_STATUS_INVALID(1004, "支付状态无效"),
    PAYMENT_FAILED(1005, "支付失败"),
    
    // Stripe相关错误码 (2000-2999)
    STRIPE_API_ERROR(2001, "Stripe API调用失败"),
    STRIPE_AUTHENTICATION_ERROR(2002, "Stripe认证失败"),
    STRIPE_INVALID_REQUEST(2003, "Stripe无效请求"),
    STRIPE_RATE_LIMIT(2004, "Stripe请求频率限制"),
    STRIPE_CARD_ERROR(2005, "支付卡错误");
    
    private final int code;
    private final String message;
    
    PayApiResponseCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
    
    public int getCode() {
        return code;
    }
    
    public String getMessage() {
        return message;
    }
} 