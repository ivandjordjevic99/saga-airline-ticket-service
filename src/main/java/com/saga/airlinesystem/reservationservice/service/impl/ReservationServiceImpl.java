package com.saga.airlinesystem.reservationservice.service.impl;

import com.saga.airlinesystem.reservationservice.dto.ReservationRequestDto;
import com.saga.airlinesystem.reservationservice.dto.ReservationResponseDto;
import com.saga.airlinesystem.reservationservice.exceptions.customexceptions.ResourceNotFoundException;
import com.saga.airlinesystem.reservationservice.model.Reservation;
import com.saga.airlinesystem.reservationservice.outboxevents.OutboxEvent;
import com.saga.airlinesystem.reservationservice.outboxevents.OutboxEventRepository;
import com.saga.airlinesystem.reservationservice.repository.ReservationRepository;
import com.saga.airlinesystem.reservationservice.saga.model.SagaInstance;
import com.saga.airlinesystem.reservationservice.saga.model.SagaTransactionType;
import com.saga.airlinesystem.reservationservice.saga.repository.SagaInstanceRepository;
import com.saga.airlinesystem.reservationservice.service.ReservationService;
import com.saga.airlinesystem.reservationservice.util.OutboxEventMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final OutboxEventRepository outboxEventRepository;
    private final SagaInstanceRepository sagaInstanceRepository;
    private final OutboxEventMapper outboxEventMapper;

    @Override
    @Transactional
    public ReservationResponseDto createReservation(ReservationRequestDto reservationRequestDto) {
        System.out.println("Creating reservation");
        Reservation reservation = new Reservation();
        reservation.setEmail(reservationRequestDto.getEmail());
        reservation.setFlightId(reservationRequestDto.getFlightId());
        reservation.setSeatNumber(reservationRequestDto.getSeatNumber());

        Reservation savedReservation = this.reservationRepository.save(reservation);
        System.out.println("Reservation saved to database");
        ReservationResponseDto reservationResponseDto = toDto(savedReservation);

        SagaInstance sagaInstance = new SagaInstance();
        sagaInstance.setReservationId(reservationResponseDto.getId());
        sagaInstance.setTransactionType(SagaTransactionType.CREATE_RESERVATION);
        sagaInstance.setUpdatedAt(OffsetDateTime.now());
        sagaInstance.setState("STARTED");
        sagaInstance.setLastStep("RESERVATION_CREATED");

        System.out.println("Saga instance saved to database");
        sagaInstanceRepository.save(sagaInstance);

        OutboxEvent outboxEvent = outboxEventMapper.mapToOutboxEvent(toDto(reservation));
        outboxEventRepository.save(outboxEvent);
        System.out.println("Outbox event saved to database");

        return reservationResponseDto;
    }

    @Override
    public ReservationResponseDto getReservationById(UUID reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId).orElseThrow(
                () -> new ResourceNotFoundException("Reservation not found"));
        return toDto(reservation);
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
