package com.saga.airlinesystem.reservationservice.rabbitmq;

import com.saga.airlinesystem.reservationservice.rabbitmq.messages.SeatReservationResultMessage;
import com.saga.airlinesystem.reservationservice.rabbitmq.messages.UpdateUserMilesResultMessage;
import com.saga.airlinesystem.reservationservice.rabbitmq.messages.UserValidationResultMessage;
import com.saga.airlinesystem.reservationservice.saga.orchestrator.CreateReservationSagaOrchestrator;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import static com.saga.airlinesystem.reservationservice.rabbitmq.RabbitMQContsants.*;


@Service
@RequiredArgsConstructor
public class RabbitMQListener {

    private final CreateReservationSagaOrchestrator createReservationSagaOrchestrator;
    private final ObjectMapper objectMapper;

    @RabbitListener(queues = RESERVATION_QUEUE)
    public void handleMessage(
            String payload,
            @Header(AmqpHeaders.RECEIVED_ROUTING_KEY) String routingKey
    ) {
        switch (routingKey) {
            case USER_VALIDATED_KEY:
                handleUserValidated(payload);
                break;
            case SEAT_RESERVED_KEY:
                handleSeatReserved(payload);
                break;
            case MILES_UPDATED_KEY:
                handleMilesUpdated(payload);
                break;
            default:
                throw new IllegalArgumentException("Unknown" + routingKey);
        }
    }

    private void handleUserValidated(String payload) {
        UserValidationResultMessage message = objectMapper.readValue(payload, UserValidationResultMessage.class);
        createReservationSagaOrchestrator.onUserValidated(message);
    }

    private void handleSeatReserved(String payload) {
        SeatReservationResultMessage message = objectMapper.readValue(payload, SeatReservationResultMessage.class);
        createReservationSagaOrchestrator.onSeatReserved(message);
    }

    private void handleMilesUpdated(String payload) {
        UpdateUserMilesResultMessage message = objectMapper.readValue(payload, UpdateUserMilesResultMessage.class);
        createReservationSagaOrchestrator.onMilesUpdated(message);
    }

}
