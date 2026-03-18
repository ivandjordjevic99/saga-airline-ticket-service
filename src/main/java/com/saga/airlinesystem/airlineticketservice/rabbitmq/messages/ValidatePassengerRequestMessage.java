package com.saga.airlinesystem.airlineticketservice.rabbitmq.messages;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.time.Instant;
import java.util.UUID;

@Getter
@ToString(callSuper = true)
@RequiredArgsConstructor
public class ValidatePassengerRequestMessage extends BaseMessage {

    private final String ticketOrderId;
    private final String email;

    @JsonCreator
    public ValidatePassengerRequestMessage(
            @JsonProperty("id") UUID id,
            @JsonProperty("timestamp") Instant timestamp,
            @JsonProperty("ticketOrderId") String ticketOrderId,
            @JsonProperty("resolution") String email
    ) {
        super(id, timestamp);
        this.ticketOrderId = ticketOrderId;
        this.email = email;
    }
}
