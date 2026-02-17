package com.saga.airlinesystem.airlineticketservice.rabbitmq.messages;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString(callSuper = true)
@RequiredArgsConstructor
public class ValidateUserRequestMessage extends BaseMessage {

    private final String ticketOrderId;
    private final String email;
}
