package com.saga.airlinesystem.reservationservice.rabbitmq;

public class RabbitMQContsants {

    // exchanges
    public static final String TICKET_RESERVATION_EXCHANGE = "ticket-reservation.exchange";

    // queue
    public static final String RESERVATION_QUEUE = "reservation.events";

    // user routing keys
    public static final String USER_VALIDATION_TOPIC = "user.validation.*";
    public static final String USER_VALIDATION_REQUEST_KEY = "user.validation.request";
    public static final String USER_VALIDATED_KEY = "user.validation.validated";
    public static final String USER_VALIDATION_FAILED_KEY = "user.validation.failed";

    // flight routing keys
    public static final String RESERVE_SEAT_REQUEST_KEY = "request.flight.seat.reserve";

    public static final String FLIGHT_SEAT_TOPIC = "flight.seat.*";
    public static final String SEAT_RESERVED_KEY = "flight.seat.reserved";
    public static final String SEAT_RESERVATION_FAILED_KEY = "flight.seat.reservation_failed";

    public static final String PAYMENT_SUCCESSFUL_KEY = "payment.successful";
}
