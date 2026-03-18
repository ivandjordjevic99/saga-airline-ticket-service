package com.saga.airlinesystem.airlineticketservice.saga.handlers;

import com.saga.airlinesystem.airlineticketservice.exceptions.customexceptions.ResourceNotFoundException;
import com.saga.airlinesystem.airlineticketservice.model.TicketOrder;
import com.saga.airlinesystem.airlineticketservice.model.TicketOrderStatus;
import com.saga.airlinesystem.airlineticketservice.outboxevents.OutboxEventService;
import com.saga.airlinesystem.airlineticketservice.rabbitmq.messages.ReserveSeatRequestMessage;
import com.saga.airlinesystem.airlineticketservice.repository.TicketOrderRepository;
import com.saga.airlinesystem.airlineticketservice.saga.commands.ReserveSeatCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static com.saga.airlinesystem.airlineticketservice.rabbitmq.RabbitMQContsants.RESERVE_SEAT_REQUEST_KEY;
import static com.saga.airlinesystem.airlineticketservice.rabbitmq.RabbitMQContsants.TICKET_BOOKING_EXCHANGE;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReserveSeatCommandHandler implements CommandHandler<ReserveSeatCommand> {

    private final OutboxEventService outboxEventService;
    private final TicketOrderRepository ticketOrderRepository;

    @Override
    @Transactional
    public void handle(ReserveSeatCommand command) {
        TicketOrder ticketOrder = ticketOrderRepository.findById(UUID.fromString(command.getTicketOrderId()))
                .orElseThrow(() -> new ResourceNotFoundException("Ticket order with id " + command.getTicketOrderId() + " not found"));
        if(!ticketOrder.getStatus().equals(TicketOrderStatus.CREATED)) {
            log.warn("Rejecting this command: Seat reservation already requested for this ticket order {}", command.getTicketOrderId());
            return;
        }
        log.info("Sending seat ticketOrder request to flight service for ticketOrder {}", ticketOrder.getId());
        ReserveSeatRequestMessage message = new ReserveSeatRequestMessage(
                command.getTicketOrderId(), ticketOrder.getFlightId().toString(), ticketOrder.getSeatNumber()
        );
        outboxEventService.saveOutboxEvent(TICKET_BOOKING_EXCHANGE, RESERVE_SEAT_REQUEST_KEY, message);
        ticketOrder.setStatus(TicketOrderStatus.SEAT_RESERVATION_REQUESTED);
    }
}
