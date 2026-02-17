package com.saga.airlinesystem.airlineticketservice.saga.handlers;

import com.saga.airlinesystem.airlineticketservice.outboxevents.OutboxEventService;
import com.saga.airlinesystem.airlineticketservice.rabbitmq.messages.ValidateUserRequestMessage;
import com.saga.airlinesystem.airlineticketservice.saga.commands.StartCreateReservationSagaCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import static com.saga.airlinesystem.airlineticketservice.rabbitmq.RabbitMQContsants.TICKET_RESERVATION_EXCHANGE;
import static com.saga.airlinesystem.airlineticketservice.rabbitmq.RabbitMQContsants.USER_VALIDATION_REQUEST_KEY;

@Component
@RequiredArgsConstructor
@Slf4j
public class StartCreateReservationSagaCommandHandler implements CommandHandler<StartCreateReservationSagaCommand> {

    private final OutboxEventService outboxEventService;

    @Override
    @Transactional
    public void handle(StartCreateReservationSagaCommand command) {
        ValidateUserRequestMessage payload = new ValidateUserRequestMessage(
                command.getReservationId(), command.getEmail());
        log.info("Sending user validation request to user service for reservation {}", command.getReservationId());
        outboxEventService.persistOutboxEvent(TICKET_RESERVATION_EXCHANGE, USER_VALIDATION_REQUEST_KEY, payload);
    }
}
