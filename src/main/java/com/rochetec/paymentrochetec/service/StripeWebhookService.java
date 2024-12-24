package com.rochetec.paymentrochetec.service;

import com.stripe.model.Event;
import com.stripe.exception.SignatureVerificationException;

public interface StripeWebhookService {
    
    /**
     * 构造Stripe事件对象
     */
    Event constructEvent(String payload, String sigHeader, String webhookSecret) 
        throws SignatureVerificationException;
    
    /**
     * 处理Webhook事件
     */
    void handleWebhookEvent(Event event);
} 