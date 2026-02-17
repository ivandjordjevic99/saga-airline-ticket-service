package com.saga.airlinesystem.reservationservice.saga.orchestrator;

import com.saga.airlinesystem.reservationservice.dto.ReservationRequestDto;
import com.saga.airlinesystem.reservationservice.exceptions.customexceptions.ResourceNotFoundException;
import com.saga.airlinesystem.reservationservice.model.Reservation;
import com.saga.airlinesystem.reservationservice.outboxevents.OutboxEventService;
import com.saga.airlinesystem.reservationservice.rabbitmq.messages.SeatReservationResultMessage;
import com.saga.airlinesystem.reservationservice.rabbitmq.messages.UserValidationResultMessage;
import com.saga.airlinesystem.reservationservice.rabbitmq.messages.ValidateUserRequestMessage;
import com.saga.airlinesystem.reservationservice.repository.ReservationRepository;
import com.saga.airlinesystem.reservationservice.saga.commands.*;
import com.saga.airlinesystem.reservationservice.saga.model.SagaInstance;
import com.saga.airlinesystem.reservationservice.saga.model.SagaState;
import com.saga.airlinesystem.reservationservice.saga.model.SagaTransactionType;
import com.saga.airlinesystem.reservationservice.saga.repository.SagaInstanceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static com.saga.airlinesystem.reservationservice.rabbitmq.RabbitMQContsants.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreateReservationSagaOrchestrator {

    private final SagaInstanceRepository sagaInstanceRepository;
    private final ReservationRepository reservationRepository;
    private final OutboxEventService outboxEventService;
    private final CommandBus commandBus;
    private static final List<SagaState> FLOW = List.of(
            SagaState.STARTED,
            SagaState.RESERVATION_CREATED,
            SagaState.USER_VALIDATED,
            SagaState.SEAT_RESERVED,
            SagaState.RESERVATION_PAYED,
            SagaState.FINISHED
    );

    @Transactional
    public void startSaga(UUID reservationId, ReservationRequestDto reservationRequestDto) {
        SagaInstance saga = new SagaInstance(SagaTransactionType.CREATE_RESERVATION, reservationId);
        sagaInstanceRepository.save(saga);
        log.info("Saga instance {} for reservation {} created", saga.getId(), reservationId);

        Reservation reservation = new Reservation(
                reservationId,
                reservationRequestDto.getEmail(),
                reservationRequestDto.getFlightId(),
                reservationRequestDto.getSeatNumber());
        reservationRepository.save(reservation);
        log.info("Reservation {} created", reservation.getId());

        log.info("Transitioning saga instance {} to RESERVATION_CREATED", saga.getId());
        saga.transitionTo(SagaState.RESERVATION_CREATED);

        ValidateUserRequestMessage payload = new ValidateUserRequestMessage(reservationId.toString(), reservation.getEmail());
        outboxEventService.persistOutboxEvent(TICKET_RESERVATION_EXCHANGE, USER_VALIDATION_REQUEST_KEY, payload);
    }

    @Transactional
    public void onUserValidated(UserValidationResultMessage payload) {
        SagaInstance sagaInstance = sagaInstanceRepository.findByReservationId(
                UUID.fromString(payload.getReservationId())).orElseThrow(() -> new ResourceNotFoundException("Saga instance not found"));
        log.info("Transitioning saga instance {} to USER_VALIDATED", sagaInstance.getId());
        sagaInstance.transitionTo(SagaState.USER_VALIDATED);

        commandBus.send(new ReserveSeatCommand(payload.getReservationId()));
    }

    @Transactional
    public void onSeatReserved(SeatReservationResultMessage payload) {
        SagaInstance sagaInstance = sagaInstanceRepository.findByReservationId(
                UUID.fromString(payload.getReservationId())).orElseThrow(() -> new ResourceNotFoundException("Saga instance not found"));
        log.info("Transitioning saga instance {} to SEAT_RESERVED", sagaInstance.getId());
        sagaInstance.transitionTo(SagaState.SEAT_RESERVED);

        commandBus.send(new PrepareForPaymentCommand(payload.getReservationId(), payload.getMiles()));
    }

    @Transactional
    @Async
    public void onReservationPayed(UUID reservationId) {
        SagaInstance sagaInstance = sagaInstanceRepository.findByReservationId(
                reservationId).orElseThrow(() -> new ResourceNotFoundException("Saga instance not found"));
        log.info("Transitioning saga instance {} to RESERVATION_PAYED", sagaInstance.getId());
        sagaInstance.transitionTo(SagaState.RESERVATION_PAYED);

        commandBus.send(new UpdateUserMilesCommand(reservationId));
    }

    @Async
    public void onSagaFailed(UUID reservationId) {
        SagaInstance sagaInstance = sagaInstanceRepository.findByReservationId(reservationId).orElseThrow(
                () -> new ResourceNotFoundException("Saga instance not found")
        );
        compensate(sagaInstance);
    }

    private void compensate(SagaInstance sagaInstance) {
        int lastIndex = FLOW.indexOf(sagaInstance.getState());
        log.info("Saga instance {} failed on state {}. Transitioning saga instance to FAILED", sagaInstance.getId(), sagaInstance.getState());
        sagaInstance.transitionTo(SagaState.FAILED);
        sagaInstanceRepository.saveAndFlush(sagaInstance);

        for (int i = lastIndex; i >= 0; i--) {
            SagaState state = FLOW.get(i);
            sendCompensationFor(state, sagaInstance);
        }

        sagaInstance.transitionTo(SagaState.COMPENSATED);
        sagaInstanceRepository.save(sagaInstance);
        log.info("Saga {} compensated", sagaInstance.getId());
    }

    private void sendCompensationFor(SagaState sagaState, SagaInstance saga) {
        switch (sagaState) {
            case RESERVATION_CREATED:
                log.info("Compensation for {} - {}: Deleting the reservation", saga.getId(), sagaState);
                commandBus.send(new DeleteReservationCommand(saga.getReservationId()));
                break;
            case SEAT_RESERVED:
                log.info("Compensation for {} - {}: Releasing the seat", saga.getId(), sagaState);
                commandBus.send(new ReleaseSeatCommand(saga.getReservationId()));
                break;
            default:
                log.info("Compensation for {} - {}: No compensation", saga.getId(), sagaState);
        }
    }
}
