package com.saga.airlinesystem.airlineticketservice.rabbitmq;

import com.saga.airlinesystem.airlineticketservice.rabbitmq.messages.SeatReservationResultMessage;
import com.saga.airlinesystem.airlineticketservice.rabbitmq.messages.UserValidationResultMessage;
import com.saga.airlinesystem.airlineticketservice.saga.orchestrator.OrderTicketSagaOrchestrator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

import static com.saga.airlinesystem.airlineticketservice.rabbitmq.RabbitMQContsants.*;


@Service
@RequiredArgsConstructor
@Slf4j
public class RabbitMQListener {

    private final OrderTicketSagaOrchestrator orderTicketSagaOrchestrator;
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
            case USER_VALIDATION_FAILED_KEY:
                handleUserValidationFailed(payload);
                break;
            case SEAT_RESERVATION_FAILED_KEY:
                handleSeatReservationFailed(payload);
                break;
            default:
                throw new IllegalArgumentException("Unknown routing key" + routingKey);
        }
    }

    private void handleUserValidationFailed(String payload) {
        UserValidationResultMessage userValidationResultMessage = objectMapper.readValue(payload, UserValidationResultMessage.class);
        log.info("Received user validation result message for reservation {}: {}",
                userValidationResultMessage.getTicketOrderId(), userValidationResultMessage.getResolution());
        orderTicketSagaOrchestrator.onSagaFailed(UUID.fromString(userValidationResultMessage.getTicketOrderId()));
    }

    private void handleUserValidated(String payload) {
        UserValidationResultMessage message = objectMapper.readValue(payload, UserValidationResultMessage.class);
        log.info("Received user validation result message for reservation {}: User validation successful",
                message.getTicketOrderId());
        orderTicketSagaOrchestrator.onUserValidated(message);
    }

    private void handleSeatReserved(String payload) {
        SeatReservationResultMessage message = objectMapper.readValue(payload, SeatReservationResultMessage.class);
        log.info("Received seat reservation successful event for reservation {}", message.getTicketOrderId());
        orderTicketSagaOrchestrator.onSeatReserved(message);
    }

    private void handleSeatReservationFailed(String payload) {
        SeatReservationResultMessage message = objectMapper.readValue(payload, SeatReservationResultMessage.class);
        log.info("Received seat reservation failed event for reservation {}: {}",
                message.getTicketOrderId(), message.getResolution());
        orderTicketSagaOrchestrator.onSagaFailed(UUID.fromString(message.getTicketOrderId()));
    }

}
