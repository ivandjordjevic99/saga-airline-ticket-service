package com.saga.airlinesystem.reservationservice.saga.handlers;

import com.saga.airlinesystem.reservationservice.exceptions.customexceptions.ResourceNotFoundException;
import com.saga.airlinesystem.reservationservice.model.Reservation;
import com.saga.airlinesystem.reservationservice.model.ReservationStatus;
import com.saga.airlinesystem.reservationservice.repository.ReservationRepository;
import com.saga.airlinesystem.reservationservice.saga.commands.DeleteReservationCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class DeleteReservationCommandHandler implements CommandHandler<DeleteReservationCommand> {

    private final ReservationRepository reservationRepository;

    @Override
    @Transactional
    public void handle(DeleteReservationCommand command) {
        Reservation reservation = reservationRepository.findById(command.getReservationId()).orElseThrow(
                () -> new ResourceNotFoundException("Reservation not found")
        );
        reservation.setStatus(ReservationStatus.DELETED);
        reservationRepository.save(reservation);
        log.info("Reservation {} has been deleted", reservation.getId());
    }
}
