package com.saga.airlinesystem.reservationservice.saga.handlers;

import com.saga.airlinesystem.reservationservice.exceptions.customexceptions.ResourceNotFoundException;
import com.saga.airlinesystem.reservationservice.model.Reservation;
import com.saga.airlinesystem.reservationservice.model.ReservationStatus;
import com.saga.airlinesystem.reservationservice.repository.ReservationRepository;
import com.saga.airlinesystem.reservationservice.saga.commands.FinishCreateReservationSagaCommand;
import com.saga.airlinesystem.reservationservice.saga.model.SagaInstance;
import com.saga.airlinesystem.reservationservice.saga.model.SagaState;
import com.saga.airlinesystem.reservationservice.saga.repository.SagaInstanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class FinishCreateReservationSagaCommandHandler implements CommandHandler<FinishCreateReservationSagaCommand> {

    private final ReservationRepository reservationRepository;
    private final SagaInstanceRepository sagaInstanceRepository;

    @Override
    @Transactional
    public void handle(FinishCreateReservationSagaCommand command) {
        Reservation reservation = reservationRepository.findById(UUID.fromString(command.getReservationId()))
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found"));
        reservation.setStatus(ReservationStatus.TICKETED);

        SagaInstance sagaInstance = sagaInstanceRepository.findByReservationId(UUID.fromString(command.getReservationId())).orElseThrow(
                () -> new ResourceNotFoundException("Saga instance not found"));
        sagaInstance.transitionTo(SagaState.FINISHED);
    }
}
