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
public class PassengerValidationResultMessage extends BaseMessage {

    private final String ticketOrderId;
    private final String email;
    private String resolution;

    @JsonCreator
    public PassengerValidationResultMessage(
            @JsonProperty("id") UUID id,
            @JsonProperty("timestamp") Instant timestamp,
            @JsonProperty("ticketOrderId") String ticketOrderId,
            @JsonProperty("email") String email,
            @JsonProperty("resolution") String resolution
    ) {
        super(id, timestamp);
        this.ticketOrderId = ticketOrderId;
        this.email = email;
        this.resolution = resolution;
    }
}
