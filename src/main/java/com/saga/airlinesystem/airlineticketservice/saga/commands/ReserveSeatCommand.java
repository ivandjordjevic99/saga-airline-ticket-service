package com.saga.airlinesystem.airlineticketservice.saga.commands;

import lombok.Getter;

import java.util.UUID;

@Getter
public class ReserveSeatCommand implements SagaCommand {

    private final String ticketOrderId;
    private final UUID commandId;

    public ReserveSeatCommand(String ticketOrderId) {
        this.commandId = UUID.randomUUID();
        this.ticketOrderId = ticketOrderId;
    }

    @Override
    public UUID getCommandId() {
        return commandId;
    }
}
