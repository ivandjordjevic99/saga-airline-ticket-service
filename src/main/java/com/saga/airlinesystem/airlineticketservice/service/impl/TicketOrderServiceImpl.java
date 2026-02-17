package com.saga.airlinesystem.airlineticketservice.service.impl;

import com.saga.airlinesystem.airlineticketservice.dto.*;
import com.saga.airlinesystem.airlineticketservice.exceptions.customexceptions.PaymentNotProcessedException;
import com.saga.airlinesystem.airlineticketservice.model.TicketOrder;
import com.saga.airlinesystem.airlineticketservice.model.TicketOrderStatus;
import com.saga.airlinesystem.airlineticketservice.repository.TicketOrderRepository;
import com.saga.airlinesystem.airlineticketservice.saga.orchestrator.OrderTicketSagaOrchestrator;
import com.saga.airlinesystem.airlineticketservice.service.TicketOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;


@Service
@RequiredArgsConstructor
@Slf4j
public class TicketOrderServiceImpl implements TicketOrderService {

    private final TicketOrderRepository ticketOrderRepository;
    private final OrderTicketSagaOrchestrator orderTicketSagaOrchestrator;

    @Override
    @Transactional
    public TicketOrderPollingResponseDto createTicketOrder(TicketOrderRequestDto ticketOrderRequestDto) {
        UUID ticketOrderId = UUID.randomUUID();
        log.info("Create order ticket request from: {}, seat number: {}, flight id: {}, ticket order id: {}",
                ticketOrderRequestDto.getEmail(),
                ticketOrderRequestDto.getSeatNumber(),
                ticketOrderRequestDto.getFlightId(),
                ticketOrderId);
        orderTicketSagaOrchestrator.startSaga(ticketOrderId, ticketOrderRequestDto);
        TicketOrderPollingResponseDto response = new TicketOrderPollingResponseDto();
        response.setId(ticketOrderId);
        return response;
    }

    @Override
    @Transactional
    public TicketOrderUpdatePaymentResponse processPayment(TicketOrderUpdatePaymentRequest ticketOrderUpdatePaymentRequest) {
        log.info("Processing payment for ticket order: {}", ticketOrderUpdatePaymentRequest.getTicketOrderId());
        UUID ticketOrderId = UUID.fromString(ticketOrderUpdatePaymentRequest.getTicketOrderId());
        TicketOrder lockedTicketOrder = ticketOrderRepository.findByIdWithLock(ticketOrderId)
                .orElseThrow(() -> new PaymentNotProcessedException("TicketOrder not found"));

        TicketOrderUpdatePaymentResponse ticketOrderUpdatePaymentResponse = new TicketOrderUpdatePaymentResponse();
        ticketOrderUpdatePaymentResponse.setTicketOrderId(ticketOrderId.toString());

        if(lockedTicketOrder.getStatus().equals(TicketOrderStatus.WAITING_FOR_PAYMENT)) {
            log.info("Changing ticked order {} status to PAYED", lockedTicketOrder.getId());
            lockedTicketOrder.setStatus(TicketOrderStatus.PAYED);
            ticketOrderRepository.save(lockedTicketOrder);
            orderTicketSagaOrchestrator.onTicketOrderPayed(lockedTicketOrder.getId());
        } else {
            throw new PaymentNotProcessedException();
        }
        return ticketOrderUpdatePaymentResponse;
    }

}
