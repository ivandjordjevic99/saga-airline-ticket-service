package com.saga.airlinesystem.reservationservice.rabbitmq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RabbitProducer {

    private final RabbitTemplate rabbitTemplate;

    public RabbitProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendEvent(String exchange, String routingKey, String payload) {
        log.info("Sending event on exchange {} and routingKey {}", exchange, routingKey);
        rabbitTemplate.convertAndSend(exchange, routingKey, payload);
    }
}
