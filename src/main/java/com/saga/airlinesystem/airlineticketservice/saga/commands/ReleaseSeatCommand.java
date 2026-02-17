package com.saga.airlinesystem.airlineticketservice.saga.commands;

import lombok.Getter;

import java.util.UUID;

@Getter
public class ReleaseSeatCommand implements SagaCommand {

    private final UUID ticketOrderId;
    private final UUID commandId;

    public ReleaseSeatCommand(UUID ticketOrderId) {
        this.ticketOrderId = ticketOrderId;
        this.commandId = UUID.randomUUID();
    }

    @Override
    public UUID getCommandId() {
        return this.commandId;
    }
}
