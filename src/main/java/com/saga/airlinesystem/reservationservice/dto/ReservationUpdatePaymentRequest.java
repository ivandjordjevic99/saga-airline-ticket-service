package com.saga.airlinesystem.reservationservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ReservationUpdatePaymentRequest {

    @JsonProperty("reservation_id")
    private final String reservationId;
}
