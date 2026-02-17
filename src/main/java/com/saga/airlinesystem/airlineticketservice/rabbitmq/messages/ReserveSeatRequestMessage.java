package com.saga.airlinesystem.reservationservice.rabbitmq.messages;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString(callSuper = true)
@RequiredArgsConstructor
public class ReserveSeatRequestMessage extends BaseMessage {

    private final String reservationId;
    private final String flightId;
    private final String seatNumber;
}
