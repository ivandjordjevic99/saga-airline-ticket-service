package com.saga.airlinesystem.airlineticketservice.saga.handlers;

import com.saga.airlinesystem.airlineticketservice.exceptions.customexceptions.ResourceNotFoundException;
import com.saga.airlinesystem.airlineticketservice.model.TicketOrder;
import com.saga.airlinesystem.airlineticketservice.model.TicketOrderStatus;
import com.saga.airlinesystem.airlineticketservice.repository.TicketOrderRepository;
import com.saga.airlinesystem.airlineticketservice.saga.commands.MarkTicketOrderAsFailedCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class MarkTicketOrderAsFailedCommandHandler implements CommandHandler<MarkTicketOrderAsFailedCommand> {

    private final TicketOrderRepository ticketOrderRepository;

    @Override
    @Transactional
    public void handle(MarkTicketOrderAsFailedCommand command) {
        TicketOrder ticketOrder = ticketOrderRepository.findById(command.getTicketOrderId()).orElseThrow(
                () -> new ResourceNotFoundException("TicketOrder not found")
        );
        ticketOrder.setStatus(TicketOrderStatus.FAILED);
        ticketOrderRepository.save(ticketOrder);
        log.info("TicketOrder {} has been marked as failed", ticketOrder.getId());
    }
}
