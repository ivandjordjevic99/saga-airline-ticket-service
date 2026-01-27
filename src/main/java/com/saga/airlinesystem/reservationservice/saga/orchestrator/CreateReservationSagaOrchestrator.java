package com.saga.airlinesystem.reservationservice.saga.orchestrator;

import com.saga.airlinesystem.reservationservice.dto.ReservationRequestDto;
import com.saga.airlinesystem.reservationservice.dto.ReservationResponseDto;
import com.saga.airlinesystem.reservationservice.exceptions.customexceptions.ResourceNotFoundException;

import com.saga.airlinesystem.reservationservice.model.Reservation;
import com.saga.airlinesystem.reservationservice.model.ReservationStatus;
import com.saga.airlinesystem.reservationservice.outboxevents.OutboxEventService;
import com.saga.airlinesystem.reservationservice.rabbitmq.RabbitProducer;
import com.saga.airlinesystem.reservationservice.rabbitmq.messages.BaseMessage;
import com.saga.airlinesystem.reservationservice.rabbitmq.messages.ReserveSeatCommand;
import com.saga.airlinesystem.reservationservice.rabbitmq.messages.SeatReservationResultMessage;
import com.saga.airlinesystem.reservationservice.rabbitmq.messages.UserValidationResultMessage;
import com.saga.airlinesystem.reservationservice.saga.model.CreateReservationSagaStates;
import com.saga.airlinesystem.reservationservice.saga.model.SagaInstance;
import com.saga.airlinesystem.reservationservice.saga.repository.SagaInstanceRepository;
import com.saga.airlinesystem.reservationservice.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.time.OffsetDateTime;
import java.util.UUID;

import static com.saga.airlinesystem.reservationservice.rabbitmq.RabbitMQContsants.*;

@Service
@RequiredArgsConstructor
public class CreateReservationSagaOrchestrator {

    private final ReservationService reservationService;
    private final SagaInstanceRepository sagaInstanceRepository;
    private final OutboxEventService outboxEventService;
    private final ObjectMapper objectMapper;

    public ReservationResponseDto startSaga(ReservationRequestDto reservationRequestDto) {
        System.out.println("Starting saga");
        return reservationService.createReservation(reservationRequestDto);
    }

    public void reserveSeat(String reservationId) {
        SagaInstance sagaInstance = sagaInstanceRepository.findByReservationId(UUID.fromString(reservationId)).orElseThrow(
                () -> new ResourceNotFoundException("Saga instance not found"));
        sagaInstance.setLastStep(CreateReservationSagaStates.USER_VALIDATED.toString());
        System.out.println("Saga last step updated to: " + sagaInstance.getLastStep());

        Reservation reservation = reservationService.getReservationById(UUID.fromString(reservationId));

        ReserveSeatCommand message = new ReserveSeatCommand(
                reservationId, reservation.getFlightId().toString(), reservation.getEmail(), reservation.getSeatNumber());
        outboxEventService.persistOutboxEvent(TICKET_RESERVATION_EXCHANGE, RESERVE_SEAT_REQUEST_KEY, message);
    }

    public void processSeatReserved(String reservationId) {
        SagaInstance sagaInstance = sagaInstanceRepository.findByReservationId(UUID.fromString(reservationId)).orElseThrow(
                () -> new ResourceNotFoundException("Saga instance not found"));
        sagaInstance.setLastStep(CreateReservationSagaStates.SEAT_RESERVED.toString());
        System.out.println("Saga last step updated to: " + sagaInstance.getLastStep());

        Reservation reservation = reservationService.getReservationById(UUID.fromString(reservationId));
        OffsetDateTime expiresAt = OffsetDateTime.now().plusMinutes(2);
        reservation.setExpiresAt(expiresAt);
        reservation.setStatus(ReservationStatus.WAITING_FOR_PAYMENT);
        System.out.println("Reservation updated to: " + reservation.getStatus() + ", expiresAt: " + expiresAt);
    }

    @Transactional
    public void handleEvent(String event, String payload) {
        switch (event) {
            case USER_VALIDATED_KEY, USER_VALIDATION_FAILED_KEY:
                UserValidationResultMessage userValidationResultMessage = objectMapper.readValue(payload, UserValidationResultMessage.class);
                if(event.equals(USER_VALIDATED_KEY)) {
                    reserveSeat(userValidationResultMessage.getReservationId());
                } else {
                    // TODO: Pripremi za polling
                    System.out.println("User validation error: " + userValidationResultMessage.getResolution());
                }
                break;
            case SEAT_RESERVED_KEY, SEAT_RESERVATION_FAILED_KEY:
                SeatReservationResultMessage seatReservationResultMessage = objectMapper.readValue(payload, SeatReservationResultMessage.class);
                if(event.equals(SEAT_RESERVED_KEY)) {
                    processSeatReserved(seatReservationResultMessage.getReservationId());
                } else {
                    // TODO: Pripremi za polling
                    System.out.println("Seat reservation error:" + seatReservationResultMessage.getResolution());
                }
                break;
            default:
                System.out.println("Invalid event: " + event);
        }
    }
}
