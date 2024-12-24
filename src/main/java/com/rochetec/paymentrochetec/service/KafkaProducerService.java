package com.rochetec.paymentrochetec.service;

public interface KafkaProducerService {

    public void sendMessage(String topic, String key, String value);
}
