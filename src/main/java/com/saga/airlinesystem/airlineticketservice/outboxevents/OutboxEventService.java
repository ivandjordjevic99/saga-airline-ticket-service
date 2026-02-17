package com.saga.airlinesystem.airlineticketservice.outboxevents;

import com.saga.airlinesystem.airlineticketservice.rabbitmq.messages.BaseMessage;

public interface OutboxEventService {

    void saveOutboxEvent(String exchange, String routingKey, BaseMessage payload);
}
