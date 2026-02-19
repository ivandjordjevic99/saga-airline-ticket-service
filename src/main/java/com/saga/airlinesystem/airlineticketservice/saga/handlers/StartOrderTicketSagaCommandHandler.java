package com.saga.airlinesystem.airlineticketservice.saga.handlers;

import com.saga.airlinesystem.airlineticketservice.outboxevents.OutboxEventService;
import com.saga.airlinesystem.airlineticketservice.rabbitmq.messages.ValidatePassengerRequestMessage;
import com.saga.airlinesystem.airlineticketservice.saga.commands.StartOrderTicketSagaCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import static com.saga.airlinesystem.airlineticketservice.rabbitmq.RabbitMQContsants.TICKET_BOOKING_EXCHANGE;
import static com.saga.airlinesystem.airlineticketservice.rabbitmq.RabbitMQContsants.PASSENGER_VALIDATION_REQUEST_KEY;

@Component
@RequiredArgsConstructor
@Slf4j
public class StartOrderTicketSagaCommandHandler implements CommandHandler<StartOrderTicketSagaCommand> {

    private final OutboxEventService outboxEventService;

    @Override
    @Transactional
    public void handle(StartOrderTicketSagaCommand command) {
        ValidatePassengerRequestMessage payload = new ValidatePassengerRequestMessage(
                command.getTicketOrderId(), command.getEmail());
        log.info("Sending passenger validation request to passenger service for ticket order {}", command.getTicketOrderId());
        outboxEventService.saveOutboxEvent(TICKET_BOOKING_EXCHANGE, PASSENGER_VALIDATION_REQUEST_KEY, payload);
    }
}
