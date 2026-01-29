package com.saga.airlinesystem.reservationservice.saga.handlers;

import com.saga.airlinesystem.reservationservice.exceptions.customexceptions.ResourceNotFoundException;
import com.saga.airlinesystem.reservationservice.model.Reservation;
import com.saga.airlinesystem.reservationservice.model.ReservationStatus;
import com.saga.airlinesystem.reservationservice.repository.ReservationRepository;
import com.saga.airlinesystem.reservationservice.saga.commands.PrepareForPaymentCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PrepareForPaymentCommandHandler implements CommandHandler<PrepareForPaymentCommand> {

    private final ReservationRepository reservationRepository;

    @Override
    @Transactional
    public void handle(PrepareForPaymentCommand command) {
        Reservation reservation = reservationRepository.findById(UUID.fromString(command.getReservationId()))
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found"));
        OffsetDateTime expiresAt = OffsetDateTime.now().plusMinutes(2);
        reservation.setMiles(command.getMiles());
        reservation.setExpiresAt(expiresAt);
        reservation.setStatus(ReservationStatus.WAITING_FOR_PAYMENT);
    }
}
