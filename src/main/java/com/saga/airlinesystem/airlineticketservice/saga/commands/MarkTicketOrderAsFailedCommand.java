package com.saga.airlinesystem.airlineticketservice.saga.commands;

import lombok.Getter;

import java.util.UUID;

@Getter
public class MarkTicketOrderAsFailedCommand implements SagaCommand {

    private final UUID ticketOrderId;
    private final UUID commandId;

    public MarkTicketOrderAsFailedCommand(UUID ticketOrderId) {
        this.ticketOrderId = ticketOrderId;
        this.commandId = UUID.randomUUID();
    }

    @Override
    public UUID getCommandId() {
        return commandId;
    }
}
