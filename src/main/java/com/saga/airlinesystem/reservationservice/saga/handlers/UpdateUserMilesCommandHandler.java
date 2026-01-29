package com.saga.airlinesystem.reservationservice.saga.handlers;

import com.saga.airlinesystem.reservationservice.outboxevents.OutboxEventService;
import com.saga.airlinesystem.reservationservice.rabbitmq.messages.UpdateUserMilesRequestMessage;
import com.saga.airlinesystem.reservationservice.saga.commands.UpdateUserMilesCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import static com.saga.airlinesystem.reservationservice.rabbitmq.RabbitMQContsants.TICKET_RESERVATION_EXCHANGE;
import static com.saga.airlinesystem.reservationservice.rabbitmq.RabbitMQContsants.UPDATE_USER_MILES_REQUEST_KEY;

@Component
@RequiredArgsConstructor
public class UpdateUserMilesCommandHandler implements CommandHandler<UpdateUserMilesCommand> {

    private final OutboxEventService outboxEventService;

    @Override
    @Transactional
    public void handle(UpdateUserMilesCommand command) {
        UpdateUserMilesRequestMessage updateUserMilesRequestMessage = new UpdateUserMilesRequestMessage(
                command.getReservation().getId().toString(),
                command.getReservation().getEmail(),
                command.getReservation().getMiles());
        outboxEventService.persistOutboxEvent(TICKET_RESERVATION_EXCHANGE, UPDATE_USER_MILES_REQUEST_KEY, updateUserMilesRequestMessage);
    }
}
