package com.saga.airlinesystem.reservationservice.saga.orchestrator;

import com.saga.airlinesystem.reservationservice.dto.ReservationRequestDto;
import com.saga.airlinesystem.reservationservice.exceptions.customexceptions.ResourceNotFoundException;
import com.saga.airlinesystem.reservationservice.model.Reservation;
import com.saga.airlinesystem.reservationservice.outboxevents.OutboxEventService;
import com.saga.airlinesystem.reservationservice.rabbitmq.messages.SeatReservationResultMessage;
import com.saga.airlinesystem.reservationservice.rabbitmq.messages.UpdateUserMilesResultMessage;
import com.saga.airlinesystem.reservationservice.rabbitmq.messages.UserValidationResultMessage;
import com.saga.airlinesystem.reservationservice.rabbitmq.messages.ValidateUserRequestMessage;
import com.saga.airlinesystem.reservationservice.repository.ReservationRepository;
import com.saga.airlinesystem.reservationservice.saga.commands.*;
import com.saga.airlinesystem.reservationservice.saga.model.SagaInstance;
import com.saga.airlinesystem.reservationservice.saga.model.SagaState;
import com.saga.airlinesystem.reservationservice.saga.model.SagaTransactionType;
import com.saga.airlinesystem.reservationservice.saga.repository.SagaInstanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static com.saga.airlinesystem.reservationservice.rabbitmq.RabbitMQContsants.*;

@Service
@RequiredArgsConstructor
public class CreateReservationSagaOrchestrator {

    private final SagaInstanceRepository sagaInstanceRepository;
    private final ReservationRepository reservationRepository;
    private final OutboxEventService outboxEventService;
    private final CommandBus commandBus;

    @Transactional
    public void startSaga(UUID reservationId, ReservationRequestDto reservationRequestDto) {
        SagaInstance saga = new SagaInstance(SagaTransactionType.CREATE_RESERVATION, reservationId);
        sagaInstanceRepository.save(saga);

        Reservation reservation = new Reservation(
                reservationId,
                reservationRequestDto.getEmail(),
                reservationRequestDto.getFlightId(),
                reservationRequestDto.getSeatNumber());
        reservationRepository.save(reservation);

        saga.transitionTo(SagaState.RESERVATION_CREATED);
        ValidateUserRequestMessage payload = new ValidateUserRequestMessage(reservationId.toString(), reservation.getEmail());
        outboxEventService.persistOutboxEvent(TICKET_RESERVATION_EXCHANGE, USER_VALIDATION_REQUEST_KEY, payload);
    }

    @Transactional
    public void onUserValidated(UserValidationResultMessage payload) {
        SagaInstance sagaInstance = sagaInstanceRepository.findByReservationId(
                UUID.fromString(payload.getReservationId())).orElseThrow(() -> new ResourceNotFoundException("Saga instance not found"));
        sagaInstance.transitionTo(SagaState.USER_VALIDATED);

        commandBus.send(new ReserveSeatCommand(payload.getReservationId()));
    }

    @Transactional
    public void onSeatReserved(SeatReservationResultMessage payload) {
        SagaInstance sagaInstance = sagaInstanceRepository.findByReservationId(
                UUID.fromString(payload.getReservationId())).orElseThrow(() -> new ResourceNotFoundException("Saga instance not found"));
        sagaInstance.transitionTo(SagaState.SEAT_RESERVED);

        commandBus.send(new PrepareForPaymentCommand(payload.getReservationId(), payload.getMiles()));
    }

    @Transactional
    public void onReservationPayed(Reservation reservation) {
        SagaInstance sagaInstance = sagaInstanceRepository.findByReservationId(
                reservation.getId()).orElseThrow(() -> new ResourceNotFoundException("Saga instance not found"));
        sagaInstance.transitionTo(SagaState.RESERVATION_PAYED);

        commandBus.send(new UpdateUserMilesCommand(reservation));
    }

    @Transactional
    public void onMilesUpdated(UpdateUserMilesResultMessage payload) {
        SagaInstance sagaInstance = sagaInstanceRepository.findByReservationId(UUID.fromString(payload.getReservationId())).orElseThrow(
                () -> new ResourceNotFoundException("Saga instance not found"));
        sagaInstance.transitionTo(SagaState.MILES_UPDATED);

        commandBus.send(new FinishCreateReservationSagaCommand(payload.getReservationId()));
    }
}
