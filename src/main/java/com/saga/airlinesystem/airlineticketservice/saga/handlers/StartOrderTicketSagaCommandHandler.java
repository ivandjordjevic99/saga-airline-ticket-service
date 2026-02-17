package com.saga.airlinesystem.airlineticketservice.saga.handlers;

import com.saga.airlinesystem.airlineticketservice.outboxevents.OutboxEventService;
import com.saga.airlinesystem.airlineticketservice.rabbitmq.messages.ValidateUserRequestMessage;
import com.saga.airlinesystem.airlineticketservice.saga.commands.StartOrderTicketSagaCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import static com.saga.airlinesystem.airlineticketservice.rabbitmq.RabbitMQContsants.TICKET_RESERVATION_EXCHANGE;
import static com.saga.airlinesystem.airlineticketservice.rabbitmq.RabbitMQContsants.USER_VALIDATION_REQUEST_KEY;

@Component
@RequiredArgsConstructor
@Slf4j
public class StartOrderTicketSagaCommandHandler implements CommandHandler<StartOrderTicketSagaCommand> {

    private final OutboxEventService outboxEventService;

    @Override
    @Transactional
    public void handle(StartOrderTicketSagaCommand command) {
        ValidateUserRequestMessage payload = new ValidateUserRequestMessage(
                command.getTicketOrderId(), command.getEmail());
        log.info("Sending user validation request to user service for ticket order {}", command.getTicketOrderId());
        outboxEventService.saveOutboxEvent(TICKET_RESERVATION_EXCHANGE, USER_VALIDATION_REQUEST_KEY, payload);
    }
}
