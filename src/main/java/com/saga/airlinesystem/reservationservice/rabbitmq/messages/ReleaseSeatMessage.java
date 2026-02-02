package com.saga.airlinesystem.reservationservice.rabbitmq.messages;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
@RequiredArgsConstructor
public class ReleaseSeatMessage extends BaseMessage {

    private final String reservationId;
}
