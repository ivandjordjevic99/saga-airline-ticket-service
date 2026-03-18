package com.saga.airlinesystem.airlineticketservice.rabbitmq.messages;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@ToString(callSuper = true)
@RequiredArgsConstructor
public class ReleaseSeatMessage extends BaseMessage {

    private final String ticketOrderId;

    @JsonCreator
    public ReleaseSeatMessage(
            @JsonProperty("id") UUID id,
            @JsonProperty("timestamp") Instant timestamp,
            @JsonProperty("ticketOrderId") String ticketOrderId
    ) {
        super(id, timestamp);
        this.ticketOrderId = ticketOrderId;
    }
}
