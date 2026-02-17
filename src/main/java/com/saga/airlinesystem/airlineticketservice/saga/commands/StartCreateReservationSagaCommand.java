package com.saga.airlinesystem.airlineticketservice.saga.commands;

import lombok.Data;

import java.util.UUID;

@Data
public class StartCreateReservationSagaCommand implements SagaCommand{

    private final UUID commandId;
    private final String email;
    private final String reservationId;

    public StartCreateReservationSagaCommand(String reservationId, String email) {
        this.reservationId = reservationId;
        this.email = email;
        this.commandId = UUID.randomUUID();
    }

    @Override
    public UUID getCommandId() {
        return commandId;
    }
}
