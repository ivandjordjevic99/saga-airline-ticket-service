package com.saga.airlinesystem.reservationservice.outboxevents;

import com.saga.airlinesystem.reservationservice.rabbitmq.messages.BaseMessage;

public interface OutboxEventService {

    void persistOutboxEvent(String exchange, String routingKey, BaseMessage payload);
}
