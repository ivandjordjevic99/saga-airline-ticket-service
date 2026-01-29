package com.saga.airlinesystem.reservationservice.saga.commands;

import lombok.Getter;

import java.util.UUID;

@Getter
public class PrepareForPaymentCommand implements SagaCommand {

    private final String reservationId;
    private final Integer miles;
    private final UUID commandId;

    public PrepareForPaymentCommand(String reservationId, Integer miles) {
        this.commandId = UUID.randomUUID();
        this.reservationId = reservationId;
        this.miles = miles;
    }

    @Override
    public UUID getCommandId() {
        return commandId;
    }
}
