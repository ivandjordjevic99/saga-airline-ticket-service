package com.saga.airlinesystem.airlineticketservice.saga.handlers;

import com.saga.airlinesystem.airlineticketservice.exceptions.customexceptions.ResourceNotFoundException;
import com.saga.airlinesystem.airlineticketservice.model.TicketOrder;
import com.saga.airlinesystem.airlineticketservice.model.TicketOrderStatus;
import com.saga.airlinesystem.airlineticketservice.repository.TicketOrderRepository;
import com.saga.airlinesystem.airlineticketservice.saga.commands.PrepareForPaymentCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class PrepareForPaymentCommandHandler implements CommandHandler<PrepareForPaymentCommand> {

    private final TicketOrderRepository ticketOrderRepository;

    @Override
    @Transactional
    public void handle(PrepareForPaymentCommand command) {
        TicketOrder ticketOrder = ticketOrderRepository.findById(UUID.fromString(command.getTicketOrderId()))
                .orElseThrow(() -> new ResourceNotFoundException("TicketOrder not found"));
        OffsetDateTime expiresAt = OffsetDateTime.now().plusSeconds(50);
        ticketOrder.setMiles(command.getMiles());
        ticketOrder.setExpiresAt(expiresAt);

        log.info("Changing ticketOrder {} status to WAITING_FOR_PAYMENT", ticketOrder.getId());
        ticketOrder.setStatus(TicketOrderStatus.WAITING_FOR_PAYMENT);
    }
}
