package com.saga.airlinesystem.airlineticketservice.rabbitmq;

public class RabbitMQContsants {

    // exchanges
    public static final String TICKET_RESERVATION_EXCHANGE = "ticket-reservation.exchange";

    // queue
    public static final String RESERVATION_QUEUE = "reservation.events";

    // user validation routing keys
    public static final String USER_VALIDATION_TOPIC = "user.validation.*";
    public static final String USER_VALIDATION_REQUEST_KEY = "request.user.validation";
    public static final String USER_VALIDATED_KEY = "user.validation.validated";
    public static final String USER_VALIDATION_FAILED_KEY = "user.validation.failed";

    // flight routing keys
    public static final String FLIGHT_SEAT_TOPIC = "flight.seat.*";
    public static final String RESERVE_SEAT_REQUEST_KEY = "request.flight.seat.reserve";
    public static final String SEAT_RESERVED_KEY = "flight.seat.reserved";
    public static final String SEAT_RESERVATION_FAILED_KEY = "flight.seat.reservation_failed";
    public static final String RELEASE_SEAT_REQUEST_KEY = "request.flight.seat.release";

    // user miles routing keys
    public static final String USER_MILES_TOPIC = "user.miles.*";
    public static final String UPDATE_USER_MILES_REQUEST_KEY = "request.user.update-miles";

}
