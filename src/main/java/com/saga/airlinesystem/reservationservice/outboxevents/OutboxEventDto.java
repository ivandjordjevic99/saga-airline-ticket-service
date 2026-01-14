package com.saga.airlinesystem.reservationservice.outboxevents;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
public class OutboxEventDto {

    @JsonProperty("id")
    private UUID id;

    @JsonProperty("payload")
    private String payload;

    @JsonProperty("status")
    private OutboxEventStatus status;

    @JsonProperty("created_at")
    private OffsetDateTime createdAt;
}
