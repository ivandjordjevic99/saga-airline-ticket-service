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
public class SeatReservationResultMessage extends BaseMessage {

    private final String ticketOrderId;
    private Integer miles;
    private String resolution;

    @JsonCreator
    public SeatReservationResultMessage(
            @JsonProperty("id") UUID id,
            @JsonProperty("timestamp") Instant timestamp,
            @JsonProperty("ticketOrderId") String ticketOrderId,
            @JsonProperty("resolution") String resolution
    ) {
        super(id, timestamp);
        this.ticketOrderId = ticketOrderId;
        this.resolution = resolution;
    }

    public SeatReservationResultMessage(
            @JsonProperty("id") UUID id,
            @JsonProperty("timestamp") Instant timestamp,
            @JsonProperty("ticketOrderId") String ticketOrderId,
            @JsonProperty("resolution") String resolution,
            @JsonProperty("miles") Integer miles
    ) {
        super(id, timestamp);
        this.ticketOrderId = ticketOrderId;
        this.resolution = resolution;
        this.miles = miles;
    }
}
