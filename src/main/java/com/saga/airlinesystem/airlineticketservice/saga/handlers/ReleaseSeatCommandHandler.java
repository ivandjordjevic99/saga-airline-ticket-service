package com.saga.airlinesystem.reservationservice.saga.handlers;

import com.saga.airlinesystem.reservationservice.outboxevents.OutboxEventService;
import com.saga.airlinesystem.reservationservice.rabbitmq.messages.ReleaseSeatMessage;
import com.saga.airlinesystem.reservationservice.saga.commands.ReleaseSeatCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import static com.saga.airlinesystem.reservationservice.rabbitmq.RabbitMQContsants.RELEASE_SEAT_REQUEST_KEY;
import static com.saga.airlinesystem.reservationservice.rabbitmq.RabbitMQContsants.TICKET_RESERVATION_EXCHANGE;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReleaseSeatCommandHandler implements CommandHandler<ReleaseSeatCommand> {

    private final OutboxEventService outboxEventService;

    @Override
    @Transactional
    public void handle(ReleaseSeatCommand command) {
        ReleaseSeatMessage message = new ReleaseSeatMessage(command.getReservationId().toString());
        outboxEventService.persistOutboxEvent(TICKET_RESERVATION_EXCHANGE, RELEASE_SEAT_REQUEST_KEY, message);
    }
}
