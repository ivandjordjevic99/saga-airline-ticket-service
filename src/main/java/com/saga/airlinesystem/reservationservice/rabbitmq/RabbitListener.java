package com.saga.airlinesystem.reservationservice.rabbitmq;

import com.saga.airlinesystem.reservationservice.saga.orchestrator.CreateReservationSagaOrchestrator;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import static com.saga.airlinesystem.reservationservice.rabbitmq.RabbitMQContsants.*;

@Service
@RequiredArgsConstructor
public class RabbitListener {

    private final CreateReservationSagaOrchestrator createReservationSagaOrchestrator;

    @org.springframework.amqp.rabbit.annotation.RabbitListener(queues = RabbitMQContsants.RESERVATION_QUEUE)
    public void handle(String payload, @Header("amqp_receivedRoutingKey") String routingKey) {
        switch (routingKey) {
            case USER_VALIDATED_KEY, USER_VALIDATION_FAILED_KEY, SEAT_RESERVED_KEY, SEAT_RESERVATION_FAILED_KEY:
                createReservationSagaOrchestrator.handleEvent(routingKey, payload);
                break;
            default:
                System.out.println("Nepoznat routing key: " + routingKey);
        }
    }

}
