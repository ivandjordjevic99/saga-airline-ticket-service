package com.saga.airlinesystem.airlineticketservice.rabbitmq;

public class RabbitMQContsants {

    // queue
    public static final String TICKET_QUEUE = "ticket.events";

    // exchanges
    public static final String TICKET_BOOKING_EXCHANGE = "ticket-booking.exchange";

    // passenger validation routing keys
    public static final String PASSENGER_VALIDATION_TOPIC = "passenger.validation.*";
    public static final String PASSENGER_VALIDATION_REQUEST_KEY = "request.passenger.validation";
    public static final String PASSENGER_VALIDATED_KEY = "passenger.validation.validated";
    public static final String PASSENGER_VALIDATION_FAILED_KEY = "passenger.validation.failed";

    // flight routing keys
    public static final String FLIGHT_SEAT_TOPIC = "flight.seat.*";
    public static final String RESERVE_SEAT_REQUEST_KEY = "request.flight.seat.reserve";
    public static final String SEAT_RESERVED_KEY = "flight.seat.reserved";
    public static final String SEAT_RESERVATION_FAILED_KEY = "flight.seat.reservation_failed";
    public static final String RELEASE_SEAT_REQUEST_KEY = "request.flight.seat.release";

    // passenger miles routing keys
    public static final String PASSENGER_MILES_TOPIC = "passenger.miles.*";
    public static final String UPDATE_PASSENGER_MILES_REQUEST_KEY = "request.passenger.update-miles";

}
