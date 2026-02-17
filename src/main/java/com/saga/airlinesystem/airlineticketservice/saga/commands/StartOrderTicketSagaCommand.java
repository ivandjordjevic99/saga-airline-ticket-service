package com.saga.airlinesystem.airlineticketservice.saga.commands;

import lombok.Data;

import java.util.UUID;

@Data
public class StartOrderTicketSagaCommand implements SagaCommand{

    private final UUID commandId;
    private final String email;
    private final String ticketOrderId;

    public StartOrderTicketSagaCommand(String ticketOrderId, String email) {
        this.ticketOrderId = ticketOrderId;
        this.email = email;
        this.commandId = UUID.randomUUID();
    }

    @Override
    public UUID getCommandId() {
        return commandId;
    }
}
