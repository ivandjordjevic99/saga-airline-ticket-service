package com.saga.airlinesystem.reservationservice.saga.commands;

import java.util.UUID;

public interface SagaCommand {

    UUID getCommandId();
}
