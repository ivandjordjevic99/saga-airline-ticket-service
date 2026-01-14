package com.saga.airlinesystem.reservationservice.rabbitmq;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import static com.saga.airlinesystem.reservationservice.rabbitmq.RabbitConfiguration.TICKET_RESERVATION_EXCHANGE;

@Service
public class RabbitProducer {

    private final RabbitTemplate rabbitTemplate;

    public RabbitProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendReservationCreated(String reservationMessage) {
        rabbitTemplate.convertAndSend(TICKET_RESERVATION_EXCHANGE, "reservation.created", reservationMessage);
        System.out.println("Sent reservation: " + reservationMessage);
    }

    public void sendUserValidated(String reservationMessage) {
        rabbitTemplate.convertAndSend(TICKET_RESERVATION_EXCHANGE, "reservation.user_validated", reservationMessage);
        System.out.println("Sent reservation: " + reservationMessage);
    }
}
