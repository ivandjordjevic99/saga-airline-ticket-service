package com.saga.airlinesystem.airlineticketservice.saga.handlers;

import com.saga.airlinesystem.airlineticketservice.exceptions.customexceptions.ResourceNotFoundException;
import com.saga.airlinesystem.airlineticketservice.model.Reservation;
import com.saga.airlinesystem.airlineticketservice.model.ReservationStatus;
import com.saga.airlinesystem.airlineticketservice.repository.ReservationRepository;
import com.saga.airlinesystem.airlineticketservice.saga.commands.PrepareForPaymentCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class PrepareForPaymentCommandHandler implements CommandHandler<PrepareForPaymentCommand> {

    private final ReservationRepository reservationRepository;

    @Override
    @Transactional
    public void handle(PrepareForPaymentCommand command) {
        Reservation reservation = reservationRepository.findById(UUID.fromString(command.getReservationId()))
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found"));
        OffsetDateTime expiresAt = OffsetDateTime.now().plusSeconds(50);
        reservation.setMiles(command.getMiles());
        reservation.setExpiresAt(expiresAt);

        log.info("Changing reservation {} status to WAITING_FOR_PAYMENT", reservation.getId());
        reservation.setStatus(ReservationStatus.WAITING_FOR_PAYMENT);
    }
}
