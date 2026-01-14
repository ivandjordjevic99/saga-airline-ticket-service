package com.saga.airlinesystem.reservationservice.saga.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.UUID;

@Data
public class ReservationCreatedDto {

    @JsonProperty("reservationId")
    private UUID reservationId;

    private String email;

    @JsonProperty("flight_id")
    private UUID flightId;

    @JsonProperty("seat_number")
    private String seatNumber;
}
