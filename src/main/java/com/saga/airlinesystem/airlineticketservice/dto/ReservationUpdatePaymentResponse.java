package com.saga.airlinesystem.reservationservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ReservationUpdatePaymentResponse {

    @JsonProperty("reservation_id")
    private String reservationId;
}
