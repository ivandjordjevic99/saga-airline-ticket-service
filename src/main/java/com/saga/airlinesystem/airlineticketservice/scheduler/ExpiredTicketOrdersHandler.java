package com.saga.airlinesystem.airlineticketservice.scheduler;

import com.saga.airlinesystem.airlineticketservice.exceptions.customexceptions.ResourceNotFoundException;
import com.saga.airlinesystem.airlineticketservice.model.TicketOrder;
import com.saga.airlinesystem.airlineticketservice.model.TicketOrderStatus;
import com.saga.airlinesystem.airlineticketservice.repository.TicketOrderRepository;
import com.saga.airlinesystem.airlineticketservice.saga.orchestrator.OrderTicketSagaOrchestrator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ExpiredTicketOrdersHandler {

    private final TicketOrderRepository ticketOrderRepository;
    private final OrderTicketSagaOrchestrator orderTicketSagaOrchestrator;

    @Scheduled(fixedRate = 3000)
    @Transactional
    public void process() {
        List<TicketOrder> ticketOrders = ticketOrderRepository.findTop10ByStatusAndExpiresAtIsNotNullOrderByExpiresAtAsc(
                TicketOrderStatus.WAITING_FOR_PAYMENT);

        OffsetDateTime now = OffsetDateTime.now();

        for (TicketOrder ticketOrder : ticketOrders) {
            TicketOrder lockedTicketOrder = ticketOrderRepository.findByIdWithLock(ticketOrder.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("TicketOrder not found"));
            if(!lockedTicketOrder.getStatus().equals(TicketOrderStatus.WAITING_FOR_PAYMENT)) {
                continue;
            }

            if (lockedTicketOrder.getExpiresAt().isBefore(now)) {
                lockedTicketOrder.setStatus(TicketOrderStatus.EXPIRED);
                ticketOrderRepository.save(lockedTicketOrder);
                log.warn("TicketOrder {} expired", lockedTicketOrder.getId());
                orderTicketSagaOrchestrator.onSagaFailed(lockedTicketOrder.getId());
            }
        }
    }

}
