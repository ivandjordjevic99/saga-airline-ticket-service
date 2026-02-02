package com.saga.airlinesystem.reservationservice.service.impl;

import com.saga.airlinesystem.reservationservice.dto.*;
import com.saga.airlinesystem.reservationservice.exceptions.customexceptions.PaymentNotProcessedException;
import com.saga.airlinesystem.reservationservice.exceptions.customexceptions.ResourceNotFoundException;
import com.saga.airlinesystem.reservationservice.model.Reservation;
import com.saga.airlinesystem.reservationservice.model.ReservationStatus;
import com.saga.airlinesystem.reservationservice.repository.ReservationRepository;
import com.saga.airlinesystem.reservationservice.saga.orchestrator.CreateReservationSagaOrchestrator;
import com.saga.airlinesystem.reservationservice.service.ReservationService;
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
        } else {
            throw new PaymentNotProcessedException();
        }
        return reservationUpdatePaymentResponse;
    }

}
