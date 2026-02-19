package com.saga.airlinesystem.airlineticketservice.saga.handlers;

import com.saga.airlinesystem.airlineticketservice.outboxevents.OutboxEventService;
import com.saga.airlinesystem.airlineticketservice.rabbitmq.messages.ReleaseSeatMessage;
import com.saga.airlinesystem.airlineticketservice.saga.commands.ReleaseSeatCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import static com.saga.airlinesystem.airlineticketservice.rabbitmq.RabbitMQContsants.RELEASE_SEAT_REQUEST_KEY;
import static com.saga.airlinesystem.airlineticketservice.rabbitmq.RabbitMQContsants.TICKET_BOOKING_EXCHANGE;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReleaseSeatCommandHandler implements CommandHandler<ReleaseSeatCommand> {

    private final OutboxEventService outboxEventService;

    @Override
    @Transactional
    public void handle(ReleaseSeatCommand command) {
        ReleaseSeatMessage message = new ReleaseSeatMessage(command.getTicketOrderId().toString());
        outboxEventService.saveOutboxEvent(TICKET_BOOKING_EXCHANGE, RELEASE_SEAT_REQUEST_KEY, message);
    }
}
