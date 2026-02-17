package com.saga.airlinesystem.airlineticketservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.UUID;

@Data
public class TicketOrderRequestDto {

    @JsonProperty("email")
    private String email;

    @JsonProperty("flight_id")
    private UUID flightId;

    @JsonProperty("seat_number")
    private String seatNumber;
}
