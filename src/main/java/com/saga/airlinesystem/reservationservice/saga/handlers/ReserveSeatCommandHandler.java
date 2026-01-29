package com.saga.airlinesystem.reservationservice.saga.handlers;

import com.saga.airlinesystem.reservationservice.exceptions.customexceptions.ResourceNotFoundException;
import com.saga.airlinesystem.reservationservice.model.Reservation;
import com.saga.airlinesystem.reservationservice.outboxevents.OutboxEventService;
import com.saga.airlinesystem.reservationservice.rabbitmq.messages.ReserveSeatRequestMessage;
import com.saga.airlinesystem.reservationservice.repository.ReservationRepository;
import com.saga.airlinesystem.reservationservice.saga.commands.ReserveSeatCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static com.saga.airlinesystem.reservationservice.rabbitmq.RabbitMQContsants.RESERVE_SEAT_REQUEST_KEY;
import static com.saga.airlinesystem.reservationservice.rabbitmq.RabbitMQContsants.TICKET_RESERVATION_EXCHANGE;

@Component
@RequiredArgsConstructor
public class ReserveSeatCommandHandler implements CommandHandler<ReserveSeatCommand> {

    private final OutboxEventService outboxEventService;
    private final ReservationRepository reservationRepository;

    @Override
    @Transactional
    public void handle(ReserveSeatCommand command) {
        Reservation reservation = reservationRepository.findById(UUID.fromString(command.getReservationId()))
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found"));
        ReserveSeatRequestMessage message = new ReserveSeatRequestMessage(
                command.getReservationId(), reservation.getFlightId().toString(), reservation.getEmail(), reservation.getSeatNumber()
        );
        outboxEventService.persistOutboxEvent(TICKET_RESERVATION_EXCHANGE, RESERVE_SEAT_REQUEST_KEY, message);
    }
}
