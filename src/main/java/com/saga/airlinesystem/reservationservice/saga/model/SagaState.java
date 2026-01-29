package com.saga.airlinesystem.reservationservice.saga.model;

public enum SagaState {

    STARTED,
    RESERVATION_CREATED,
    USER_VALIDATED,
    SEAT_RESERVED,
    RESERVATION_PAYED,
    MILES_UPDATED,
    FINISHED
}
