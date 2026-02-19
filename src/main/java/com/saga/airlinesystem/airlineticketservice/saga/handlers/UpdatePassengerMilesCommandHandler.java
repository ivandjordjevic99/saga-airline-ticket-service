package com.saga.airlinesystem.airlineticketservice.saga.handlers;

import com.saga.airlinesystem.airlineticketservice.exceptions.customexceptions.ResourceNotFoundException;
import com.saga.airlinesystem.airlineticketservice.model.TicketOrder;
import com.saga.airlinesystem.airlineticketservice.model.TicketOrderStatus;
import com.saga.airlinesystem.airlineticketservice.outboxevents.OutboxEventService;
import com.saga.airlinesystem.airlineticketservice.rabbitmq.messages.UpdatePassengerMilesRequestMessage;
import com.saga.airlinesystem.airlineticketservice.repository.TicketOrderRepository;
import com.saga.airlinesystem.airlineticketservice.saga.commands.UpdatePassengerMilesCommand;
import com.saga.airlinesystem.airlineticketservice.saga.model.SagaInstance;
import com.saga.airlinesystem.airlineticketservice.saga.model.SagaState;
import com.saga.airlinesystem.airlineticketservice.saga.repository.SagaInstanceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import static com.saga.airlinesystem.airlineticketservice.rabbitmq.RabbitMQContsants.TICKET_BOOKING_EXCHANGE;
import static com.saga.airlinesystem.airlineticketservice.rabbitmq.RabbitMQContsants.UPDATE_PASSENGER_MILES_REQUEST_KEY;

@Component
@RequiredArgsConstructor
@Slf4j
public class UpdatePassengerMilesCommandHandler implements CommandHandler<UpdatePassengerMilesCommand> {

    private final OutboxEventService outboxEventService;
    private final TicketOrderRepository ticketOrderRepository;
    private final SagaInstanceRepository sagaInstanceRepository;

    @Override
    @Transactional
    public void handle(UpdatePassengerMilesCommand command) {
        TicketOrder ticketOrder = ticketOrderRepository.findById(command.getTicketOrderId()).orElseThrow(
                () -> new ResourceNotFoundException("TicketOrder with id " + command.getTicketOrderId() + " not found")
        );
        UpdatePassengerMilesRequestMessage updatePassengerMilesRequestMessage = new UpdatePassengerMilesRequestMessage(
                ticketOrder.getId().toString(),
                ticketOrder.getEmail(),
                ticketOrder.getMiles());
        log.info("Sending update miles request to passenger service for ticketOrder {}", ticketOrder.getId());
        outboxEventService.saveOutboxEvent(TICKET_BOOKING_EXCHANGE, UPDATE_PASSENGER_MILES_REQUEST_KEY, updatePassengerMilesRequestMessage);

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
