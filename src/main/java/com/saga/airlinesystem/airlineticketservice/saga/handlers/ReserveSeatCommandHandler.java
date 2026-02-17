package com.saga.airlinesystem.airlineticketservice.saga.handlers;

import com.saga.airlinesystem.airlineticketservice.exceptions.customexceptions.ResourceNotFoundException;
import com.saga.airlinesystem.airlineticketservice.model.Reservation;
import com.saga.airlinesystem.airlineticketservice.outboxevents.OutboxEventService;
import com.saga.airlinesystem.airlineticketservice.rabbitmq.messages.ReserveSeatRequestMessage;
import com.saga.airlinesystem.airlineticketservice.repository.ReservationRepository;
import com.saga.airlinesystem.airlineticketservice.saga.commands.ReserveSeatCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static com.saga.airlinesystem.airlineticketservice.rabbitmq.RabbitMQContsants.RESERVE_SEAT_REQUEST_KEY;
import static com.saga.airlinesystem.airlineticketservice.rabbitmq.RabbitMQContsants.TICKET_RESERVATION_EXCHANGE;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReserveSeatCommandHandler implements CommandHandler<ReserveSeatCommand> {

    private final OutboxEventService outboxEventService;
    private final ReservationRepository reservationRepository;

    @Override
    @Transactional
    public void handle(ReserveSeatCommand command) {
        Reservation reservation = reservationRepository.findById(UUID.fromString(command.getReservationId()))
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found"));

        log.info("Sending seat reservation request to flight service for reservation {}", reservation.getId());
        ReserveSeatRequestMessage message = new ReserveSeatRequestMessage(
                command.getReservationId(), reservation.getFlightId().toString(), reservation.getSeatNumber()
        );
        outboxEventService.persistOutboxEvent(TICKET_RESERVATION_EXCHANGE, RESERVE_SEAT_REQUEST_KEY, message);
    }
}
