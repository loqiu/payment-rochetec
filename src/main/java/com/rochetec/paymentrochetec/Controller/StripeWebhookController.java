package com.rochetec.paymentrochetec.controller;

import com.rochetec.paymentrochetec.service.StripeWebhookService;
import com.stripe.model.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StripeWebhookController {
    
    private static final Logger logger = LoggerFactory.getLogger(StripeWebhookController.class);
    
    @Value("${stripe.webhook.secret}")
    private String webhookSecret;
    
    @Autowired
    private StripeWebhookService stripeWebhookService;
    
    @PostMapping("/api/payment/webhook")
    public ResponseEntity<String> handleStripeWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader) {
        
        logger.info("收到Stripe Webhook请求 - Signature: {}", sigHeader);
        
        try {
            Event event = stripeWebhookService.constructEvent(payload, sigHeader, webhookSecret);
            logger.info("Webhook事件类型: {}, 事件ID: {}", event.getType(), event.getId());
            
            stripeWebhookService.handleWebhookEvent(event);
            
            return ResponseEntity.ok().body("Webhook processed successfully");
        } catch (Exception e) {
            logger.error("处理Webhook请求时发生错误", e);
            return ResponseEntity.badRequest().body("Webhook processing failed");
        }
    }
} 