package com.rochetec.paymentrochetec.enums;

/**
 * 支付方式枚举
 */
public enum PaymentMethodType {
    
    CARD("card", "信用卡支付"),
    ALIPAY("alipay", "支付宝"),
    WECHAT_PAY("wechat_pay", "微信支付"),
    SEPA_DEBIT("sepa_debit", "欧元区银行转账"),
    IDEAL("ideal", "荷兰iDEAL支付"),
    SOFORT("sofort", "德国Sofort支付"),
    BANCONTACT("bancontact", "比利时Bancontact支付"),
    GIROPAY("giropay", "德国Giropay支付"),
    P24("p24", "波兰P24支付"),
    EPS("eps", "奥地利EPS支付"),
    GRABPAY("grabpay", "新加坡GrabPay支付"),
    FPX("fpx", "马来西亚FPX支付");
    
    private final String code;
    private final String description;
    
    PaymentMethodType(String code, String description) {
        this.code = code;
        this.description = description;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getDescription() {
        return description;
    }
    
    /**
     * 获取支付方式代码数组
     * @param types 支付方式枚举数组
     * @return 支付方式代码数组
     */
    public static String[] getCodes(PaymentMethodType... types) {
        if (types == null || types.length == 0) {
            return new String[]{CARD.getCode()}; // 默认使用信用卡支付
        }
        String[] codes = new String[types.length];
        for (int i = 0; i < types.length; i++) {
            codes[i] = types[i].getCode();
        }
        return codes;
    }
} 