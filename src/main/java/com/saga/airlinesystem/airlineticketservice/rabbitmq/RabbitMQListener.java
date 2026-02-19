package com.saga.airlinesystem.airlineticketservice.rabbitmq;

import com.saga.airlinesystem.airlineticketservice.rabbitmq.messages.SeatReservationResultMessage;
import com.saga.airlinesystem.airlineticketservice.rabbitmq.messages.PassengerValidationResultMessage;
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

    @RabbitListener(queues = TICKET_QUEUE)
    public void handleMessage(
            String payload,
            @Header(AmqpHeaders.RECEIVED_ROUTING_KEY) String routingKey
    ) {
        switch (routingKey) {
            case PASSENGER_VALIDATED_KEY:
                handlePassengerValidated(payload);
                break;
            case SEAT_RESERVED_KEY:
                handleSeatReserved(payload);
                break;
            case PASSENGER_VALIDATION_FAILED_KEY:
                handlePassengerValidationFailed(payload);
                break;
            case SEAT_RESERVATION_FAILED_KEY:
                handleSeatReservationFailed(payload);
                break;
            default:
                throw new IllegalArgumentException("Unknown routing key" + routingKey);
        }
    }

    private void handlePassengerValidationFailed(String payload) {
        PassengerValidationResultMessage passengerValidationResultMessage = objectMapper.readValue(payload, PassengerValidationResultMessage.class);
        log.info("Received passenger validation result message for ticket order {}: {}",
                passengerValidationResultMessage.getTicketOrderId(), passengerValidationResultMessage.getResolution());
        orderTicketSagaOrchestrator.onSagaFailed(UUID.fromString(passengerValidationResultMessage.getTicketOrderId()));
    }

    private void handlePassengerValidated(String payload) {
        PassengerValidationResultMessage message = objectMapper.readValue(payload, PassengerValidationResultMessage.class);
        log.info("Received passenger validation result message for ticket order {}: Passenger validation successful",
                message.getTicketOrderId());
        orderTicketSagaOrchestrator.onPassengerValidated(message);
    }

    private void handleSeatReserved(String payload) {
        SeatReservationResultMessage message = objectMapper.readValue(payload, SeatReservationResultMessage.class);
        log.info("Received seat reservation successful event for ticket order {}", message.getTicketOrderId());
        orderTicketSagaOrchestrator.onSeatReserved(message);
    }

    private void handleSeatReservationFailed(String payload) {
        SeatReservationResultMessage message = objectMapper.readValue(payload, SeatReservationResultMessage.class);
        log.info("Received seat reservation failed event for ticket order {}: {}",
                message.getTicketOrderId(), message.getResolution());
        orderTicketSagaOrchestrator.onSagaFailed(UUID.fromString(message.getTicketOrderId()));
    }

}
