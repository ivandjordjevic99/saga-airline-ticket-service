package com.saga.airlinesystem.reservationservice.scheduler;

import com.saga.airlinesystem.reservationservice.model.Reservation;
import com.saga.airlinesystem.reservationservice.model.ReservationStatus;
import com.saga.airlinesystem.reservationservice.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Transactional
public class ExpiredReservationsHandler {

    private final ReservationRepository reservationRepository;

    @Scheduled(fixedRate = 3000)
    @Transactional
    public void process() {
        List<Reservation> reservations = reservationRepository.findTop10ByStatusAndExpiresAtIsNotNullOrderByExpiresAtAsc(
                ReservationStatus.WAITING_FOR_PAYMENT);

        OffsetDateTime now = OffsetDateTime.now();

        for (Reservation reservation : reservations) {
            Reservation lockedReservation = reservationRepository.findByIdWithLock(reservation.getId())
                    .orElseThrow(() -> new RuntimeException("Reservation not found"));
            if(!lockedReservation.getStatus().equals(ReservationStatus.WAITING_FOR_PAYMENT)) {
                continue;
            }

            if (lockedReservation.getExpiresAt().isBefore(now)) {
                lockedReservation.setStatus(ReservationStatus.EXPIRED);
                System.out.println("Reservation expired: " + lockedReservation.getId());
            }
        }
    }

}
