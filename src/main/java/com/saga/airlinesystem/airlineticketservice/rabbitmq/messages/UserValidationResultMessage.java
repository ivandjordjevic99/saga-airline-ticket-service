package com.saga.airlinesystem.reservationservice.rabbitmq.messages;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
@RequiredArgsConstructor
public class UserValidationResultMessage extends BaseMessage {

    private final String reservationId;
    private final String email;
    private String resolution;
}
