package com.saga.airlinesystem.reservationservice.saga.commands;

import lombok.Getter;

import java.util.UUID;

@Getter
public class FinishCreateReservationSagaCommand implements SagaCommand {

    private final String reservationId;
    private final UUID commandId;

    public FinishCreateReservationSagaCommand(String reservationId) {
        this.commandId = UUID.randomUUID();
        this.reservationId = reservationId;
    }

    @Override
    public UUID getCommandId() {
        return commandId;
    }
}
