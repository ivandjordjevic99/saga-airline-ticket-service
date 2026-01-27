package com.saga.airlinesystem.reservationservice.service.impl;

import com.saga.airlinesystem.reservationservice.dto.ReservationRequestDto;
import com.saga.airlinesystem.reservationservice.dto.ReservationResponseDto;
import com.saga.airlinesystem.reservationservice.dto.ReservationUpdatePaymentRequest;
import com.saga.airlinesystem.reservationservice.dto.ReservationUpdatePaymentResponse;
import com.saga.airlinesystem.reservationservice.exceptions.customexceptions.PaymentNotProcessedException;
import com.saga.airlinesystem.reservationservice.exceptions.customexceptions.ResourceNotFoundException;
import com.saga.airlinesystem.reservationservice.model.Reservation;
import com.saga.airlinesystem.reservationservice.model.ReservationStatus;
import com.saga.airlinesystem.reservationservice.outboxevents.OutboxEventService;
import com.saga.airlinesystem.reservationservice.rabbitmq.messages.ValidateUserCommand;
import com.saga.airlinesystem.reservationservice.repository.ReservationRepository;
import com.saga.airlinesystem.reservationservice.saga.model.SagaInstance;
import com.saga.airlinesystem.reservationservice.saga.model.SagaTransactionType;
import com.saga.airlinesystem.reservationservice.saga.repository.SagaInstanceRepository;
import com.saga.airlinesystem.reservationservice.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.UUID;

import static com.saga.airlinesystem.reservationservice.rabbitmq.RabbitMQContsants.*;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final SagaInstanceRepository sagaInstanceRepository;
    private final OutboxEventService outboxEventService;

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
        sagaInstanceRepository.save(sagaInstance);
        System.out.println("Saga instance saved to database");

        ValidateUserCommand payload = new ValidateUserCommand(reservationResponseDto.getId().toString(), reservationResponseDto.getEmail());
        outboxEventService.persistOutboxEvent(TICKET_RESERVATION_EXCHANGE, USER_VALIDATION_REQUEST_KEY, payload);

        return reservationResponseDto;
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
