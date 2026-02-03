package com.saga.airlinesystem.reservationservice.saga.handlers;

import com.saga.airlinesystem.reservationservice.exceptions.customexceptions.ResourceNotFoundException;
import com.saga.airlinesystem.reservationservice.model.Reservation;
import com.saga.airlinesystem.reservationservice.model.ReservationStatus;
import com.saga.airlinesystem.reservationservice.outboxevents.OutboxEventService;
import com.saga.airlinesystem.reservationservice.rabbitmq.messages.UpdateUserMilesRequestMessage;
import com.saga.airlinesystem.reservationservice.repository.ReservationRepository;
import com.saga.airlinesystem.reservationservice.saga.commands.UpdateUserMilesCommand;
import com.saga.airlinesystem.reservationservice.saga.model.SagaInstance;
import com.saga.airlinesystem.reservationservice.saga.model.SagaState;
import com.saga.airlinesystem.reservationservice.saga.repository.SagaInstanceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import static com.saga.airlinesystem.reservationservice.rabbitmq.RabbitMQContsants.TICKET_RESERVATION_EXCHANGE;
import static com.saga.airlinesystem.reservationservice.rabbitmq.RabbitMQContsants.UPDATE_USER_MILES_REQUEST_KEY;

@Component
@RequiredArgsConstructor
@Slf4j
public class UpdateUserMilesCommandHandler implements CommandHandler<UpdateUserMilesCommand> {

    private final OutboxEventService outboxEventService;
    private final ReservationRepository reservationRepository;
    private final SagaInstanceRepository sagaInstanceRepository;

    @Override
    @Transactional
    public void handle(UpdateUserMilesCommand command) {
        Reservation reservation = reservationRepository.findById(command.getReservationId()).orElseThrow(
                () -> new ResourceNotFoundException("Reservation with id " + command.getReservationId() + " not found")
        );
        UpdateUserMilesRequestMessage updateUserMilesRequestMessage = new UpdateUserMilesRequestMessage(
                reservation.getId().toString(),
                reservation.getEmail(),
                reservation.getMiles());
        log.info("Sending update miles request to user service for reservation {}", reservation.getId());
        outboxEventService.persistOutboxEvent(TICKET_RESERVATION_EXCHANGE, UPDATE_USER_MILES_REQUEST_KEY, updateUserMilesRequestMessage);

        log.info("Changing reservation {} status to TICKETED", reservation.getId());
        reservation.setStatus(ReservationStatus.TICKETED);
        reservationRepository.save(reservation);

        SagaInstance sagaInstance = sagaInstanceRepository.findByReservationId(reservation.getId()).orElseThrow(
                () -> new ResourceNotFoundException("SagaInstance with reservation id " + reservation.getId() + " not found")
        );
        log.info("Transitioning saga instance {} to FINISHED", sagaInstance.getId());
        sagaInstance.transitionTo(SagaState.FINISHED);
        sagaInstanceRepository.save(sagaInstance);
    }
}
