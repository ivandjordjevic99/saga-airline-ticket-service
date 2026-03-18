package com.saga.airlinesystem.airlineticketservice.model;

public enum TicketOrderStatus {

    CREATED,
    SEAT_RESERVATION_REQUESTED,
    WAITING_FOR_PAYMENT,
    EXPIRED,
    PAYED,
    TICKETED,
    FAILED
}
