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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;


@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final CreateReservationSagaOrchestrator createReservationSagaOrchestrator;

    @Override
    @Transactional
    public ReservationPollingResponseDto createReservation(ReservationRequestDto reservationRequestDto) {
        UUID reservationId = UUID.randomUUID();
        createReservationSagaOrchestrator.startSaga(reservationId, reservationRequestDto);
        ReservationPollingResponseDto response = new ReservationPollingResponseDto();
        response.setId(reservationId);
        return response;
    }


    @Override
    public Reservation getReservationById(UUID reservationId) {
        return reservationRepository.findById(reservationId).orElseThrow(
                () -> new ResourceNotFoundException("Reservation not found"));
    }

    @Override
    @Transactional
    public ReservationUpdatePaymentResponse processPayment(ReservationUpdatePaymentRequest reservationUpdatePaymentRequest) {
        UUID reservationId = UUID.fromString(reservationUpdatePaymentRequest.getReservationId());
        Reservation lockedReservation = reservationRepository.findByIdWithLock(reservationId)
                .orElseThrow(() -> new PaymentNotProcessedException("Reservation not found"));

        ReservationUpdatePaymentResponse reservationUpdatePaymentResponse = new ReservationUpdatePaymentResponse();
        reservationUpdatePaymentResponse.setReservationId(reservationId.toString());

        if(lockedReservation.getStatus().equals(ReservationStatus.WAITING_FOR_PAYMENT)) {
            lockedReservation.setStatus(ReservationStatus.PAYED);
        } else {
            throw new PaymentNotProcessedException();
        }
        return reservationUpdatePaymentResponse;
    }

    private ReservationResponseDto toDto(Reservation reservation) {
        ReservationResponseDto reservationResponseDto = new ReservationResponseDto();

        reservationResponseDto.setEmail(reservation.getEmail());
        reservationResponseDto.setFlightId(reservation.getFlightId());
        reservationResponseDto.setSeatNumber(reservation.getSeatNumber());
        reservationResponseDto.setId(reservation.getId());
        reservationResponseDto.setCreatedAt(reservation.getCreatedAt());
        reservationResponseDto.setExpiresAt(reservation.getExpiresAt());
        reservationResponseDto.setStatus(reservation.getStatus());

        return reservationResponseDto;
    }
}
