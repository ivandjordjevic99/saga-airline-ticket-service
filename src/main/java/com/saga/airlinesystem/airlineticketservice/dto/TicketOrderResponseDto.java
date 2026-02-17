package com.saga.airlinesystem.airlineticketservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.saga.airlinesystem.airlineticketservice.model.TicketOrderStatus;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
public class TicketOrderResponseDto {

    private UUID id;

    private String email;

    @JsonProperty("flight_id")
    private UUID flightId;

    @JsonProperty("seat_number")
    private String seatNumber;

    @JsonProperty("created_at")
    private OffsetDateTime createdAt;

    @JsonProperty("updated_at")
    private OffsetDateTime updatedAt;

    @JsonProperty("expires_at")
    private OffsetDateTime expiresAt;

    private TicketOrderStatus status;
}
