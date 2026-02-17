package com.saga.airlinesystem.airlineticketservice.rabbitmq.messages;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
@RequiredArgsConstructor
public class UpdateUserMilesResultMessage extends BaseMessage {

    private final String ticketOrderId;
    private String resolution;
}
