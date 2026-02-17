package com.saga.airlinesystem.airlineticketservice.saga.model;

public enum SagaState {

    STARTED,
    TICKET_ORDER_CREATED,
    USER_VALIDATED,
    SEAT_RESERVED,
    TICKET_ORDER_PAYED,
    FINISHED,
    FAILED,
    COMPENSATED
}
