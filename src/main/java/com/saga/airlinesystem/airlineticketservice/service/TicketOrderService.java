package com.saga.airlinesystem.airlineticketservice.service;

import com.saga.airlinesystem.airlineticketservice.dto.TicketOrderPollingResponseDto;
import com.saga.airlinesystem.airlineticketservice.dto.TicketOrderRequestDto;
import com.saga.airlinesystem.airlineticketservice.dto.TicketOrderUpdatePaymentRequest;
import com.saga.airlinesystem.airlineticketservice.dto.TicketOrderUpdatePaymentResponse;

public interface TicketOrderService {

    TicketOrderPollingResponseDto createTicketOrder(TicketOrderRequestDto ticketOrderRequestDto);
    TicketOrderUpdatePaymentResponse processPayment(TicketOrderUpdatePaymentRequest ticketOrderUpdatePaymentRequest);
}
