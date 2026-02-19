package com.saga.airlinesystem.airlineticketservice.rabbitmq.messages;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
@RequiredArgsConstructor
public class UpdatePassengerMilesRequestMessage extends BaseMessage {

    private final String ticketOrderId;
    private final String email;
    private final Integer miles;
}
