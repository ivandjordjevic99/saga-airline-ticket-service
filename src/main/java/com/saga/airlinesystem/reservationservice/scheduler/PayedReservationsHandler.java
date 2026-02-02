package com.saga.airlinesystem.reservationservice.scheduler;

import com.saga.airlinesystem.reservationservice.model.Reservation;
import com.saga.airlinesystem.reservationservice.model.ReservationStatus;
import com.saga.airlinesystem.reservationservice.repository.ReservationRepository;
import com.saga.airlinesystem.reservationservice.saga.orchestrator.CreateReservationSagaOrchestrator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
@Transactional
@Slf4j
public class PayedReservationsHandler {

    private final ReservationRepository reservationRepository;
    private final CreateReservationSagaOrchestrator createReservationSagaOrchestrator;

    @Scheduled(fixedRate = 3000)
    @Transactional
    public void process() {
        List<Reservation> reservations = reservationRepository.findTop10ByStatusOrderByUpdatedAtAsc(ReservationStatus.PAYED);

        for (Reservation reservation : reservations) {
            log.info("Changing reservation {} status to UPDATING_MILES", reservation.getId());
            reservation.setStatus(ReservationStatus.UPDATING_MILES);
            createReservationSagaOrchestrator.onReservationPayed(reservation);
        }
    }

}