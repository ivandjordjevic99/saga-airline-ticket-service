package com.saga.airlinesystem.airlineticketservice.rabbitmq.messages;

import lombok.Getter;
import lombok.ToString;

import java.time.Instant;
import java.util.UUID;

@Getter
@ToString
public abstract class BaseMessage {

    private final UUID id;
    private final Instant timestamp;

    protected BaseMessage() {
        this.id = UUID.randomUUID();
        this.timestamp = Instant.now();
    }
}
