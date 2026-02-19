package com.saga.airlinesystem.airlineticketservice.service.impl;

import com.saga.airlinesystem.airlineticketservice.dto.*;
import com.saga.airlinesystem.airlineticketservice.exceptions.customexceptions.PaymentNotProcessedException;
import com.saga.airlinesystem.airlineticketservice.exceptions.customexceptions.ResourceNotFoundException;
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
    public CreateTicketOrderResponseDto createTicketOrder(TicketOrderRequestDto ticketOrderRequestDto) {
        UUID ticketOrderId = UUID.randomUUID();
        log.info("Create order ticket request from: {}, seat number: {}, flight id: {}, ticket order id: {}",
                ticketOrderRequestDto.getEmail(),
                ticketOrderRequestDto.getSeatNumber(),
                ticketOrderRequestDto.getFlightId(),
                ticketOrderId);
        orderTicketSagaOrchestrator.startSaga(ticketOrderId, ticketOrderRequestDto);
        CreateTicketOrderResponseDto response = new CreateTicketOrderResponseDto();
        response.setTicketOrderId(ticketOrderId);
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

    @Override
    public TicketOrderResponseDto getTicketOrderById(UUID ticketOrderId) {
        TicketOrder ticketOrder = ticketOrderRepository.findById(ticketOrderId).orElseThrow(
                () -> new ResourceNotFoundException("TicketOrder not found")
        );
        TicketOrderResponseDto ticketOrderResponseDto = new TicketOrderResponseDto();
        ticketOrderResponseDto.setId(ticketOrder.getId());
        ticketOrderResponseDto.setEmail(ticketOrder.getEmail());
        ticketOrderResponseDto.setSeatNumber(ticketOrder.getSeatNumber());
        ticketOrderResponseDto.setFlightId(ticketOrder.getFlightId());
        ticketOrderResponseDto.setStatus(ticketOrder.getStatus());
        ticketOrderResponseDto.setCreatedAt(ticketOrder.getCreatedAt());
        ticketOrderResponseDto.setUpdatedAt(ticketOrder.getUpdatedAt());
        ticketOrderResponseDto.setExpiresAt(ticketOrder.getExpiresAt());
        return ticketOrderResponseDto;
    }

}
