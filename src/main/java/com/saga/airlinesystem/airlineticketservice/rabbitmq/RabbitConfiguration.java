package com.saga.airlinesystem.airlineticketservice.rabbitmq;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.saga.airlinesystem.airlineticketservice.rabbitmq.RabbitMQContsants.*;

@Configuration
public class RabbitConfiguration {

    @Bean
    public TopicExchange ticketReservationExchange() {
        return new TopicExchange(TICKET_RESERVATION_EXCHANGE);
    }

    @Bean
    public Queue reservationQueue() {
        return new Queue(RESERVATION_QUEUE, true);
    }

    @Bean
    public Binding userValidationBinding(Queue reservationQueue, TopicExchange ticketReservationExchange) {
        return BindingBuilder
                .bind(reservationQueue)
                .to(ticketReservationExchange)
                .with(USER_VALIDATION_TOPIC);
    }

    @Bean
    public Binding userMilesBinding(Queue reservationQueue, TopicExchange ticketReservationExchange) {
        return BindingBuilder
                .bind(reservationQueue)
                .to(ticketReservationExchange)
                .with(USER_MILES_TOPIC);
    }

    @Bean
    public Binding flightSeatBinding(Queue reservationQueue, TopicExchange ticketReservationExchange) {
        return BindingBuilder
                .bind(reservationQueue)
                .to(ticketReservationExchange)
                .with(FLIGHT_SEAT_TOPIC);
    }
}
