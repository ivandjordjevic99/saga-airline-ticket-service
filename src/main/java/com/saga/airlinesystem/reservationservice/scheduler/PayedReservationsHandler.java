package com.saga.airlinesystem.reservationservice.scheduler;

import com.saga.airlinesystem.reservationservice.model.Reservation;
import com.saga.airlinesystem.reservationservice.model.ReservationStatus;
import com.saga.airlinesystem.reservationservice.repository.ReservationRepository;
import com.saga.airlinesystem.reservationservice.saga.orchestrator.CreateReservationSagaOrchestrator;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
@Transactional
public class PayedReservationsHandler {

    private final ReservationRepository reservationRepository;
    private final CreateReservationSagaOrchestrator createReservationSagaOrchestrator;

    @Scheduled(fixedRate = 3000)
    @Transactional
    public void process() {
        List<Reservation> reservations = reservationRepository.findTop10ByStatusOrderByUpdatedAtAsc(ReservationStatus.PAYED);

        for (Reservation reservation : reservations) {
            createReservationSagaOrchestrator.onReservationPayed(reservation);
        }
    }

}