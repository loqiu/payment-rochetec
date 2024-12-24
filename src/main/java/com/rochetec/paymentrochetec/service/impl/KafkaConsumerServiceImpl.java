package com.rochetec.paymentrochetec.service.impl;

import com.rochetec.paymentrochetec.constant.KafkaTopicConstant;
import com.rochetec.paymentrochetec.service.KafkaConsumerService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;


@Service
public class KafkaConsumerServiceImpl implements KafkaConsumerService {

    private static final Logger logger = LogManager.getLogger(KafkaConsumerServiceImpl.class);

    @KafkaListener(topics = KafkaTopicConstant.QUICKSTART_EVENTS, groupId = "${spring.kafka.consumer.group-id}")
    public void listen(String message) {
        try {
            logger.info("Received message: {}", message);
            // 消息处理逻辑
        } catch (Exception e) {
            logger.error("Error processing message: {}", message, e);
        }
    }
}
