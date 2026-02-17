package com.saga.airlinesystem.reservationservice.saga.commands;

import lombok.Getter;

import java.util.UUID;

@Getter
public class UpdateUserMilesCommand implements SagaCommand {

    private final UUID reservationId;
    private final UUID commandId;

    public UpdateUserMilesCommand(UUID reservationId) {
        this.commandId = UUID.randomUUID();
        this.reservationId = reservationId;
    }

    @Override
    public UUID getCommandId() {
        return this.commandId;
    }


}
