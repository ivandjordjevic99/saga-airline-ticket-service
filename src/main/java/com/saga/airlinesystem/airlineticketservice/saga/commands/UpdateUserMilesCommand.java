package com.saga.airlinesystem.airlineticketservice.saga.commands;

import lombok.Getter;

import java.util.UUID;

@Getter
public class UpdateUserMilesCommand implements SagaCommand {

    private final UUID ticketOrderId;
    private final UUID commandId;

    public UpdateUserMilesCommand(UUID ticketOrderId) {
        this.commandId = UUID.randomUUID();
        this.ticketOrderId = ticketOrderId;
    }

    @Override
    public UUID getCommandId() {
        return this.commandId;
    }


}
