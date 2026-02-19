package com.saga.airlinesystem.airlineticketservice.service;

import com.saga.airlinesystem.airlineticketservice.dto.*;

import java.util.UUID;

public interface TicketOrderService {

    CreateTicketOrderResponseDto createTicketOrder(TicketOrderRequestDto ticketOrderRequestDto);
    TicketOrderUpdatePaymentResponse processPayment(TicketOrderUpdatePaymentRequest ticketOrderUpdatePaymentRequest);
    TicketOrderResponseDto getTicketOrderById(UUID id);
}
