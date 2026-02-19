package com.saga.airlinesystem.airlineticketservice.rabbitmq;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.saga.airlinesystem.airlineticketservice.rabbitmq.RabbitMQContsants.*;

@Configuration
public class RabbitConfiguration {

    @Bean
    public TopicExchange ticketBookingExchange() {
        return new TopicExchange(TICKET_BOOKING_EXCHANGE);
    }

    @Bean
    public Queue ticketQueue() {
        return new Queue(TICKET_QUEUE, true);
    }

    @Bean
    public Binding passengerValidationBinding(Queue ticketQueue, TopicExchange ticketBookingExchange) {
        return BindingBuilder
                .bind(ticketQueue)
                .to(ticketBookingExchange)
                .with(PASSENGER_VALIDATION_TOPIC);
    }

    @Bean
    public Binding passengerMilesBinding(Queue ticketQueue, TopicExchange ticketBookingExchange) {
        return BindingBuilder
                .bind(ticketQueue)
                .to(ticketBookingExchange)
                .with(PASSENGER_MILES_TOPIC);
    }

    @Bean
    public Binding flightSeatBinding(Queue ticketQueue, TopicExchange ticketBookingExchange) {
        return BindingBuilder
                .bind(ticketQueue)
                .to(ticketBookingExchange)
                .with(FLIGHT_SEAT_TOPIC);
    }
}
