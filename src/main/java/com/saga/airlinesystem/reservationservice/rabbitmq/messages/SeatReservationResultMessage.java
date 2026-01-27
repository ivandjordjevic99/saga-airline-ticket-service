package com.saga.airlinesystem.reservationservice.rabbitmq.messages;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
@RequiredArgsConstructor
public class SeatReservationResultMessage extends BaseMessage {

    private final String reservationId;
    private String resolution;
}
