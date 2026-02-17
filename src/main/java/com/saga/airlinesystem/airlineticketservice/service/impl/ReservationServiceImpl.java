package com.saga.airlinesystem.airlineticketservice.service.impl;

import com.saga.airlinesystem.airlineticketservice.dto.*;
import com.saga.airlinesystem.airlineticketservice.exceptions.customexceptions.PaymentNotProcessedException;
import com.saga.airlinesystem.airlineticketservice.model.Reservation;
import com.saga.airlinesystem.airlineticketservice.model.ReservationStatus;
import com.saga.airlinesystem.airlineticketservice.repository.ReservationRepository;
import com.saga.airlinesystem.airlineticketservice.saga.orchestrator.CreateReservationSagaOrchestrator;
import com.saga.airlinesystem.airlineticketservice.service.ReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;


@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final CreateReservationSagaOrchestrator createReservationSagaOrchestrator;

    @Override
    @Transactional
    public ReservationPollingResponseDto createReservation(ReservationRequestDto reservationRequestDto) {
        UUID reservationId = UUID.randomUUID();
        log.info("Create reservation request from: {}, seat number: {}, flight id: {}, reservation id: {}",
                reservationRequestDto.getEmail(),
                reservationRequestDto.getSeatNumber(),
                reservationRequestDto.getFlightId(),
                reservationId);
        createReservationSagaOrchestrator.startSaga(reservationId, reservationRequestDto);
        ReservationPollingResponseDto response = new ReservationPollingResponseDto();
        response.setId(reservationId);
        return response;
    }

    @Override
    @Transactional
    public ReservationUpdatePaymentResponse processPayment(ReservationUpdatePaymentRequest reservationUpdatePaymentRequest) {
        log.info("Processing payment for reservation: {}", reservationUpdatePaymentRequest.getReservationId());
        UUID reservationId = UUID.fromString(reservationUpdatePaymentRequest.getReservationId());
        Reservation lockedReservation = reservationRepository.findByIdWithLock(reservationId)
                .orElseThrow(() -> new PaymentNotProcessedException("Reservation not found"));

        ReservationUpdatePaymentResponse reservationUpdatePaymentResponse = new ReservationUpdatePaymentResponse();
        reservationUpdatePaymentResponse.setReservationId(reservationId.toString());

        if(lockedReservation.getStatus().equals(ReservationStatus.WAITING_FOR_PAYMENT)) {
            log.info("Changing reservation {} status to PAYED", lockedReservation.getId());
            lockedReservation.setStatus(ReservationStatus.PAYED);
            reservationRepository.save(lockedReservation);
            createReservationSagaOrchestrator.onReservationPayed(lockedReservation.getId());
        } else {
            throw new PaymentNotProcessedException();
        }
        return reservationUpdatePaymentResponse;
    }

}
