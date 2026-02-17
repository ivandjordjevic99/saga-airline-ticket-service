package com.saga.airlinesystem.airlineticketservice.saga.handlers;

import com.saga.airlinesystem.airlineticketservice.exceptions.customexceptions.ResourceNotFoundException;
import com.saga.airlinesystem.airlineticketservice.model.TicketOrder;
import com.saga.airlinesystem.airlineticketservice.model.TicketOrderStatus;
import com.saga.airlinesystem.airlineticketservice.outboxevents.OutboxEventService;
import com.saga.airlinesystem.airlineticketservice.rabbitmq.messages.UpdateUserMilesRequestMessage;
import com.saga.airlinesystem.airlineticketservice.repository.TicketOrderRepository;
import com.saga.airlinesystem.airlineticketservice.saga.commands.UpdateUserMilesCommand;
import com.saga.airlinesystem.airlineticketservice.saga.model.SagaInstance;
import com.saga.airlinesystem.airlineticketservice.saga.model.SagaState;
import com.saga.airlinesystem.airlineticketservice.saga.repository.SagaInstanceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import static com.saga.airlinesystem.airlineticketservice.rabbitmq.RabbitMQContsants.TICKET_RESERVATION_EXCHANGE;
import static com.saga.airlinesystem.airlineticketservice.rabbitmq.RabbitMQContsants.UPDATE_USER_MILES_REQUEST_KEY;

@Component
@RequiredArgsConstructor
@Slf4j
public class UpdateUserMilesCommandHandler implements CommandHandler<UpdateUserMilesCommand> {

    private final OutboxEventService outboxEventService;
    private final TicketOrderRepository ticketOrderRepository;
    private final SagaInstanceRepository sagaInstanceRepository;

    @Override
    @Transactional
    public void handle(UpdateUserMilesCommand command) {
        TicketOrder ticketOrder = ticketOrderRepository.findById(command.getTicketOrderId()).orElseThrow(
                () -> new ResourceNotFoundException("TicketOrder with id " + command.getTicketOrderId() + " not found")
        );
        UpdateUserMilesRequestMessage updateUserMilesRequestMessage = new UpdateUserMilesRequestMessage(
                ticketOrder.getId().toString(),
                ticketOrder.getEmail(),
                ticketOrder.getMiles());
        log.info("Sending update miles request to user service for ticketOrder {}", ticketOrder.getId());
        outboxEventService.saveOutboxEvent(TICKET_RESERVATION_EXCHANGE, UPDATE_USER_MILES_REQUEST_KEY, updateUserMilesRequestMessage);

        log.info("Changing ticketOrder {} status to TICKETED", ticketOrder.getId());
        ticketOrder.setStatus(TicketOrderStatus.TICKETED);
        ticketOrderRepository.save(ticketOrder);

        SagaInstance sagaInstance = sagaInstanceRepository.findByAggregateId(ticketOrder.getId()).orElseThrow(
                () -> new ResourceNotFoundException("SagaInstance with ticketOrder id " + ticketOrder.getId() + " not found")
        );
        log.info("Transitioning saga instance {} to FINISHED", sagaInstance.getId());
        sagaInstance.transitionTo(SagaState.FINISHED);
        sagaInstanceRepository.save(sagaInstance);
    }
}
