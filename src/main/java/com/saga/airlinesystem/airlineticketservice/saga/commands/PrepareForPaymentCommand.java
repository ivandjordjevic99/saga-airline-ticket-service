package com.saga.airlinesystem.airlineticketservice.saga.commands;

import lombok.Getter;

import java.util.UUID;

@Getter
public class PrepareForPaymentCommand implements SagaCommand {

    private final String ticketOrderId;
    private final Integer miles;
    private final UUID commandId;

    public PrepareForPaymentCommand(String ticketOrderId, Integer miles) {
        this.commandId = UUID.randomUUID();
        this.ticketOrderId = ticketOrderId;
        this.miles = miles;
    }

    @Override
    public UUID getCommandId() {
        return commandId;
    }
}
