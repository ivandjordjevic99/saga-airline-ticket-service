package com.saga.airlinesystem.reservationservice.rabbitmq;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfiguration {

    public static final String RESERVATION_QUEUE = "reservation.events";
    public static final String TICKET_RESERVATION_EXCHANGE = "ticket-reservation.exchange";

    @Bean
    public TopicExchange ticketReservationExchange() {
        return new TopicExchange(TICKET_RESERVATION_EXCHANGE);
    }

    @Bean
    public Queue reservationQueue() {
        return new Queue(RESERVATION_QUEUE, true);
    }

    @Bean
    public Binding userValidatedBinding(Queue reservationQueue, TopicExchange ticketReservationExchange) {
        return BindingBuilder
                .bind(reservationQueue)
                .to(ticketReservationExchange)
                .with("user.validated");
    }

    @Bean
    public Binding seatBookedBinding(Queue reservationQueue, TopicExchange ticketReservationExchange) {
        return BindingBuilder
                .bind(reservationQueue)
                .to(ticketReservationExchange)
                .with("flight.seat_booked");
    }

    @Bean
    public Binding paymentSuccessfulBinding(Queue reservationQueue, TopicExchange ticketReservationExchange) {
        return BindingBuilder
                .bind(reservationQueue)
                .to(ticketReservationExchange)
                .with("payment.successful");
    }
}
