package com.saga.airlinesystem.reservationservice.rabbitmq;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class RabbitProducer {

    private final RabbitTemplate rabbitTemplate;

    public RabbitProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendEvent(String exchange, String routingKey, String payload) {
        rabbitTemplate.convertAndSend(exchange, routingKey, payload);
    }
}
