package com.saga.airlinesystem.airlineticketservice.rabbitmq;

import com.saga.airlinesystem.airlineticketservice.exceptions.customexceptions.EventAlreadyReceivedException;
import com.saga.airlinesystem.airlineticketservice.inboxevents.InboxEventService;
import com.saga.airlinesystem.airlineticketservice.inboxevents.InboxEventType;
import com.saga.airlinesystem.airlineticketservice.rabbitmq.messages.SeatReservationResultMessage;
import com.saga.airlinesystem.airlineticketservice.rabbitmq.messages.PassengerValidationResultMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;


import static com.saga.airlinesystem.airlineticketservice.rabbitmq.RabbitMQContsants.*;


@Service
@RequiredArgsConstructor
@Slf4j
public class RabbitMQListener {

    private final ObjectMapper objectMapper;
    private final InboxEventService inboxEventService;

    @RabbitListener(queues = TICKET_QUEUE)
    public void handleMessage(
            String payload,
            @Header(AmqpHeaders.RECEIVED_ROUTING_KEY) String routingKey
    ) {
        try {
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
        } catch (EventAlreadyReceivedException e) {
            log.warn(e.getMessage());
        } catch (JacksonException | IllegalArgumentException e) {
            log.error(e.getMessage());
        }

    }

    private void handlePassengerValidationFailed(String payload) {
        PassengerValidationResultMessage message = objectMapper.readValue(payload, PassengerValidationResultMessage.class);
        inboxEventService.saveInboxEvent(message.getId(), payload, InboxEventType.PASSENGER_VALIDATION_FAILED);
        log.info("Received passenger validation result message for ticket order {}: {}",
                message.getTicketOrderId(), message.getResolution());
    }

    private void handlePassengerValidated(String payload) {
        PassengerValidationResultMessage message = objectMapper.readValue(payload, PassengerValidationResultMessage.class);
        inboxEventService.saveInboxEvent(message.getId(), payload, InboxEventType.PASSENGER_VALIDATED);
        log.info("Received passenger validation result message for ticket order {}: Passenger validation successful",
                message.getTicketOrderId());
    }

    private void handleSeatReserved(String payload) {
        SeatReservationResultMessage message = objectMapper.readValue(payload, SeatReservationResultMessage.class);
        inboxEventService.saveInboxEvent(message.getId(), payload, InboxEventType.SEAT_RESERVED);
        log.info("Received seat reservation successful event for ticket order {}", message.getTicketOrderId());
    }

    private void handleSeatReservationFailed(String payload) {
        SeatReservationResultMessage message = objectMapper.readValue(payload, SeatReservationResultMessage.class);
        inboxEventService.saveInboxEvent(message.getId(), payload, InboxEventType.SEAT_RESERVATION_FAILED);
        log.info("Received seat reservation failed event for ticket order {}: {}",
                message.getTicketOrderId(), message.getResolution());
    }

}
