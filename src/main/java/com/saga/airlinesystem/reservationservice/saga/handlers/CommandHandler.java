package com.saga.airlinesystem.reservationservice.saga.handlers;

import com.saga.airlinesystem.reservationservice.saga.commands.SagaCommand;

public interface CommandHandler<C extends SagaCommand> {

    void handle(C command);
}
