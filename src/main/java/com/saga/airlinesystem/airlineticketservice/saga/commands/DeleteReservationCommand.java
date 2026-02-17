package com.saga.airlinesystem.reservationservice.saga.commands;

import lombok.Getter;

import java.util.UUID;

@Getter
public class DeleteReservationCommand implements SagaCommand {

    private final UUID reservationId;
    private final UUID commandId;

    public DeleteReservationCommand(UUID reservationId) {
        this.reservationId = reservationId;
        this.commandId = UUID.randomUUID();
    }

    @Override
    public UUID getCommandId() {
        return commandId;
    }
}
