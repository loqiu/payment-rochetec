package com.rochetec.paymentrochetec.service.impl;

import com.rochetec.paymentrochetec.service.StripeWebhookService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.*;
import com.stripe.net.Webhook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class StripeWebhookServiceImpl implements StripeWebhookService {
    
    private static final Logger logger = LoggerFactory.getLogger(StripeWebhookServiceImpl.class);
    
    @Override
    public Event constructEvent(String payload, String sigHeader, String webhookSecret) 
            throws SignatureVerificationException {
        return Webhook.constructEvent(payload, sigHeader, webhookSecret);
    }
    
    @Override
    public void handleWebhookEvent(Event event) {
        switch (event.getType()) {
            case "payment_intent.succeeded":
                handlePaymentIntentSucceeded(event);
                break;
            case "payment_intent.payment_failed":
                handlePaymentIntentFailed(event);
                break;
            case "payment_intent.canceled":
                handlePaymentIntentCanceled(event);
                break;
            default:
                logger.info("未处理的Webhook事件类型: {}", event.getType());
        }
    }
    
    private void handlePaymentIntentSucceeded(Event event) {
        PaymentIntent paymentIntent = (PaymentIntent) event.getDataObjectDeserializer().getObject().get();
        logger.info("支付成功 - PaymentIntentId: {}, 金额: {}, 货币: {}", 
            paymentIntent.getId(), 
            paymentIntent.getAmount(),
            paymentIntent.getCurrency());
            
        // TODO: 更新订单状态为支付成功
        // TODO: 发送支付成功通知
    }
    
    private void handlePaymentIntentFailed(Event event) {
        PaymentIntent paymentIntent = (PaymentIntent) event.getDataObjectDeserializer().getObject().get();
        logger.error("支付失败 - PaymentIntentId: {}, 错误信息: {}", 
            paymentIntent.getId(),
            paymentIntent.getLastPaymentError() != null ? 
                paymentIntent.getLastPaymentError().getMessage() : "未知错误");
            
        // TODO: 更新订单状态为支付失败
        // TODO: 发送支付失败通知
    }
    
    private void handlePaymentIntentCanceled(Event event) {
        PaymentIntent paymentIntent = (PaymentIntent) event.getDataObjectDeserializer().getObject().get();
        logger.info("支付已取消 - PaymentIntentId: {}", paymentIntent.getId());
        
        // TODO: 更新订单状态为已取消
        // TODO: 发送支付取消通知
    }
} 