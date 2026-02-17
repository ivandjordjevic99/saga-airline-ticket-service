package com.saga.airlinesystem.airlineticketservice.saga.commands;

import lombok.Getter;

import java.util.UUID;

@Getter
public class ReleaseSeatCommand implements SagaCommand {

    private final UUID reservationId;
    private final UUID commandId;

    public ReleaseSeatCommand(UUID reservationId) {
        this.reservationId = reservationId;
        this.commandId = UUID.randomUUID();
    }

    @Override
    public UUID getCommandId() {
        return this.commandId;
    }
}
