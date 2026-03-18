package com.saga.airlinesystem.airlineticketservice.saga.orchestrator;

import com.saga.airlinesystem.airlineticketservice.dto.TicketOrderRequestDto;
import com.saga.airlinesystem.airlineticketservice.exceptions.customexceptions.ResourceNotFoundException;
import com.saga.airlinesystem.airlineticketservice.model.TicketOrder;
import com.saga.airlinesystem.airlineticketservice.outboxevents.OutboxEventService;
import com.saga.airlinesystem.airlineticketservice.rabbitmq.messages.SeatReservationResultMessage;
import com.saga.airlinesystem.airlineticketservice.rabbitmq.messages.PassengerValidationResultMessage;
import com.saga.airlinesystem.airlineticketservice.rabbitmq.messages.ValidatePassengerRequestMessage;
import com.saga.airlinesystem.airlineticketservice.repository.TicketOrderRepository;
import com.saga.airlinesystem.airlineticketservice.saga.commands.*;
import com.saga.airlinesystem.airlineticketservice.saga.model.SagaInstance;
import com.saga.airlinesystem.airlineticketservice.saga.model.SagaState;
import com.saga.airlinesystem.airlineticketservice.saga.model.SagaTransactionType;
import com.saga.airlinesystem.airlineticketservice.saga.repository.SagaInstanceRepository;
import com.saga.airlinesystem.airlineticketservice.saga.simulations.SimulationsUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static com.saga.airlinesystem.airlineticketservice.rabbitmq.RabbitMQContsants.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderTicketSagaOrchestrator {

    private final SagaInstanceRepository sagaInstanceRepository;
    private final TicketOrderRepository ticketOrderRepository;
    private final OutboxEventService outboxEventService;
    private final SimulationsUtil simulationsUtil;
    private final CommandBus commandBus;
    private static final List<SagaState> FLOW = List.of(
            SagaState.STARTED,
            SagaState.TICKET_ORDER_CREATED,
            SagaState.PASSENGER_VALIDATED,
            SagaState.SEAT_RESERVED,
            SagaState.TICKET_ORDER_PAYED,
            SagaState.FINISHED
    );

    @Transactional
    public void startSaga(UUID ticketOrderId, TicketOrderRequestDto ticketOrderRequestDto) {
        SagaInstance saga = new SagaInstance(SagaTransactionType.ORDER_TICKET, ticketOrderId);
        sagaInstanceRepository.save(saga);
        log.info("Saga instance {} for ticketOrder {} created", saga.getId(), ticketOrderId);

        TicketOrder ticketOrder = new TicketOrder(
                ticketOrderId,
                ticketOrderRequestDto.getEmail(),
                ticketOrderRequestDto.getFlightId(),
                ticketOrderRequestDto.getSeatNumber());
        ticketOrderRepository.save(ticketOrder);
        log.info("TicketOrder {} created", ticketOrder.getId());

        log.info("Transitioning saga instance {} to TICKET_ORDER_CREATED", saga.getId());
        simulationsUtil.simulateDelay();
        saga.transitionTo(SagaState.TICKET_ORDER_CREATED);

        ValidatePassengerRequestMessage payload = new ValidatePassengerRequestMessage(ticketOrderId.toString(), ticketOrder.getEmail());
        outboxEventService.saveOutboxEvent(TICKET_BOOKING_EXCHANGE, PASSENGER_VALIDATION_REQUEST_KEY, payload);
    }

    @Transactional
    public void onPassengerValidated(PassengerValidationResultMessage payload) {
        try {
            SagaInstance sagaInstance = sagaInstanceRepository.findByAggregateId(
                    UUID.fromString(payload.getTicketOrderId())).orElseThrow(() -> new ResourceNotFoundException(
                            "Saga instance for ticker order " + payload.getTicketOrderId() + " not found"));
            log.info("Transitioning saga instance {} to PASSENGER_VALIDATED", sagaInstance.getId());
            simulationsUtil.simulateDelay();
            sagaInstance.transitionTo(SagaState.PASSENGER_VALIDATED);
            commandBus.send(new ReserveSeatCommand(payload.getTicketOrderId()));
        } catch (ResourceNotFoundException e) {
            log.error(e.getMessage());
        }
    }

    @Transactional
    public void onSeatReserved(SeatReservationResultMessage payload) {
        try {
            SagaInstance sagaInstance = sagaInstanceRepository.findByAggregateId(
                    UUID.fromString(payload.getTicketOrderId())).orElseThrow(() -> new ResourceNotFoundException("Saga instance not found"));
            log.info("Transitioning saga instance {} to SEAT_RESERVED", sagaInstance.getId());
            simulationsUtil.simulateDelay();
            sagaInstance.transitionTo(SagaState.SEAT_RESERVED);

            commandBus.send(new PrepareForPaymentCommand(payload.getTicketOrderId(), payload.getMiles()));
        } catch (ResourceNotFoundException e) {
            log.error(e.getMessage());
        }
    }

    @Transactional
    @Async
    public void onTicketOrderPayed(UUID ticketOrderId) {
        SagaInstance sagaInstance = sagaInstanceRepository.findByAggregateId(
                ticketOrderId).orElseThrow(() -> new ResourceNotFoundException("Saga instance not found"));
        log.info("Transitioning saga instance {} to TICKET_ORDER_PAYED", sagaInstance.getId());
        simulationsUtil.simulateDelay();
        sagaInstance.transitionTo(SagaState.TICKET_ORDER_PAYED);

        commandBus.send(new UpdatePassengerMilesCommand(ticketOrderId));
    }

    @Async
    public void onSagaFailed(UUID ticketOrderId) {
        try {
            SagaInstance sagaInstance = sagaInstanceRepository.findByAggregateId(ticketOrderId).orElseThrow(
                    () -> new ResourceNotFoundException("Saga instance for ticket order " + ticketOrderId + " not found")
            );
            compensate(sagaInstance);
        } catch (ResourceNotFoundException e) {
            log.error(e.getMessage());
        }

    }

    private void compensate(SagaInstance sagaInstance) {
        int lastIndex = FLOW.indexOf(sagaInstance.getState());
        log.info("Saga instance {} failed on state {}. Transitioning saga instance to FAILED", sagaInstance.getId(), sagaInstance.getState());
        simulationsUtil.simulateDelay();
        sagaInstance.transitionTo(SagaState.FAILED);
        sagaInstanceRepository.saveAndFlush(sagaInstance);

        for (int i = lastIndex; i >= 0; i--) {
            SagaState state = FLOW.get(i);
            sendCompensationFor(state, sagaInstance);
        }
        simulationsUtil.simulateDelay();
        sagaInstance.transitionTo(SagaState.COMPENSATED);
        sagaInstanceRepository.save(sagaInstance);
        log.info("Saga {} compensated", sagaInstance.getId());
    }

    private void sendCompensationFor(SagaState sagaState, SagaInstance saga) {
        switch (sagaState) {
            case TICKET_ORDER_CREATED:
                log.info("Compensation for {} - {}: Marking ticket order as failed", saga.getId(), sagaState);
                commandBus.send(new MarkTicketOrderAsFailedCommand(saga.getAggregateId()));
                break;
            case SEAT_RESERVED:
                log.info("Compensation for {} - {}: Releasing the seat", saga.getId(), sagaState);
                commandBus.send(new ReleaseSeatCommand(saga.getAggregateId()));
                break;
            default:
                log.info("Compensation for {} - {}: No compensation", saga.getId(), sagaState);
        }
    }
}
