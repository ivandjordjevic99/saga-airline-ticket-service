package com.saga.airlinesystem.airlineticketservice.saga.handlers;

import com.saga.airlinesystem.airlineticketservice.saga.commands.SagaCommand;

public interface CommandHandler<C extends SagaCommand> {

    void handle(C command);
}
