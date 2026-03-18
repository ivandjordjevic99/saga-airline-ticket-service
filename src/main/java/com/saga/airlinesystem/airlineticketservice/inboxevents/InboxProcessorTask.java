package com.saga.airlinesystem.airlineticketservice.inboxevents;

import com.saga.airlinesystem.airlineticketservice.rabbitmq.messages.PassengerValidationResultMessage;
import com.saga.airlinesystem.airlineticketservice.rabbitmq.messages.SeatReservationResultMessage;
import com.saga.airlinesystem.airlineticketservice.saga.orchestrator.OrderTicketSagaOrchestrator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class InboxProcessorTask {

    private final InboxEventRepository inboxEventRepository;
    private final ObjectMapper objectMapper;
    private final OrderTicketSagaOrchestrator orderTicketSagaOrchestrator;

    @Scheduled(fixedDelay = 1000)
    public void process() {
        List<InboxEvent> inboxEvents = inboxEventRepository.findTop10ByStatusOrderByReceivedAtAsc(InboxEventStatus.PENDING);

        for (InboxEvent inboxEvent : inboxEvents) {
            inboxEvent.setStatus(InboxEventStatus.IN_PROGRESS);
            inboxEventRepository.save(inboxEvent);
            handleInboxEvent(inboxEvent);
            inboxEvent.setStatus(InboxEventStatus.PROCESSED);
            inboxEventRepository.save(inboxEvent);
        }
    }

    @Scheduled(fixedDelay = 5000)
    public void processStuckEvents() {
        List<InboxEvent> stuckEvents = inboxEventRepository.findByStatusAndUpdatedAtBefore(
                InboxEventStatus.IN_PROGRESS,
                OffsetDateTime.now().minusMinutes(3)
        );
        for (InboxEvent stuckEvent : stuckEvents) {
            if(stuckEvent.getRetryCount() < 5) {
                stuckEvent.setStatus(InboxEventStatus.PENDING);
                stuckEvent.incrementRetryCount();
                inboxEventRepository.save(stuckEvent);
                log.info("{} message moved to PENDING, retry count: {}", stuckEvent.getMessageId(), stuckEvent.getRetryCount());
            } else {
                stuckEvent.setStatus(InboxEventStatus.FAILED);
                inboxEventRepository.save(stuckEvent);
                log.error("{} message couldn't be processed after 5 times", stuckEvent.getMessageId());
            }
        }
    }

    private void handleInboxEvent(InboxEvent inboxEvent) {
        InboxEventType inboxEventType = inboxEvent.getInboxEventType();
        String payload = inboxEvent.getPayload();

        switch (inboxEventType) {
            case PASSENGER_VALIDATED:
                handlePassengerValidated(payload);
                break;
            case SEAT_RESERVED:
                handleSeatReserved(payload);
                break;
            case PASSENGER_VALIDATION_FAILED:
                handlePassengerValidationFailed(payload);
                break;
            case SEAT_RESERVATION_FAILED:
                handleSeatReservationFailed(payload);
                break;
            default:
                log.error("Invalid inbox event type: {}", inboxEventType);
        }
    }

    private void handlePassengerValidationFailed(String payload) {
        PassengerValidationResultMessage message = objectMapper.readValue(payload, PassengerValidationResultMessage.class);
        orderTicketSagaOrchestrator.onSagaFailed(UUID.fromString(message.getTicketOrderId()));
    }

    private void handlePassengerValidated(String payload) {
        PassengerValidationResultMessage message = objectMapper.readValue(payload, PassengerValidationResultMessage.class);
        orderTicketSagaOrchestrator.onPassengerValidated(message);

    }

    private void handleSeatReserved(String payload) {
        SeatReservationResultMessage message = objectMapper.readValue(payload, SeatReservationResultMessage.class);
        orderTicketSagaOrchestrator.onSeatReserved(message);
    }

    private void handleSeatReservationFailed(String payload) {
        SeatReservationResultMessage message = objectMapper.readValue(payload, SeatReservationResultMessage.class);
        orderTicketSagaOrchestrator.onSagaFailed(UUID.fromString(message.getTicketOrderId()));
    }
}
