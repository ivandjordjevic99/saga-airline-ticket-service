package com.saga.airlinesystem.reservationservice.saga.commands;

import com.saga.airlinesystem.reservationservice.model.Reservation;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UpdateUserMilesCommand implements SagaCommand {

    private final Reservation reservation;
    private final UUID commandId;

    public UpdateUserMilesCommand(Reservation reservation) {
        this.commandId = UUID.randomUUID();
        this.reservation = reservation;
    }

    @Override
    public UUID getCommandId() {
        return this.commandId;
    }


}
