package com.rochetec.paymentrochetec;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableDubbo
@EnableDiscoveryClient
@EnableKafka
public class PaymentRochetecApplication {

    public static void main(String[] args) {
        SpringApplication.run(PaymentRochetecApplication.class, args);
    }

}
