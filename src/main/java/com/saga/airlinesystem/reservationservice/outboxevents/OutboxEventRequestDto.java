package com.saga.airlinesystem.reservationservice.outboxevents;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class OutboxEventRequestDto {

    @JsonProperty("payload")
    private String payload;

}
