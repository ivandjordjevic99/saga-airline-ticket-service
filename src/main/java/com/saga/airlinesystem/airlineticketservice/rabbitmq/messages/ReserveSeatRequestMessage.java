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
public class ReserveSeatRequestMessage extends BaseMessage {

    private final String ticketOrderId;
    private final String flightId;
    private final String seatNumber;

    @JsonCreator
    public ReserveSeatRequestMessage(
            @JsonProperty("id") UUID id,
            @JsonProperty("timestamp") Instant timestamp,
            @JsonProperty("ticketOrderId") String ticketOrderId,
            @JsonProperty("flightId") String flightId,
            @JsonProperty("seatNumber") String seatNumber
    ) {
        super(id, timestamp);
        this.ticketOrderId = ticketOrderId;
        this.flightId = flightId;
        this.seatNumber = seatNumber;
    }
}
