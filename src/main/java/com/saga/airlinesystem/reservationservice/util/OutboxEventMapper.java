package com.saga.airlinesystem.reservationservice.util;

import com.saga.airlinesystem.reservationservice.dto.ReservationResponseDto;
import com.saga.airlinesystem.reservationservice.outboxevents.OutboxEvent;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
public class OutboxEventMapper {

    private final ObjectMapper objectMapper;

    public OutboxEventMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public OutboxEvent mapToOutboxEvent(ReservationResponseDto reservation) {
        return new OutboxEvent(objectMapper.writeValueAsString(reservation));
    }

}
