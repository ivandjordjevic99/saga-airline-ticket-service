package com.saga.airlinesystem.airlineticketservice.saga.commands;

import java.util.UUID;

public interface SagaCommand {

    UUID getCommandId();
}
