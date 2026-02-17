package com.saga.airlinesystem.airlineticketservice.controller;

import com.saga.airlinesystem.airlineticketservice.dto.*;
import com.saga.airlinesystem.airlineticketservice.service.TicketOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ticket-order")
@RequiredArgsConstructor
public class TicketOrderController {

    private final TicketOrderService ticketOrderService;

    @PostMapping
    public ResponseEntity<TicketOrderPollingResponseDto> createTicketOrder(@RequestBody TicketOrderRequestDto ticketOrderRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ticketOrderService.createTicketOrder(ticketOrderRequestDto));
    }

    @PutMapping("/process-payment")
    public ResponseEntity<TicketOrderUpdatePaymentResponse> updateTicketOrderPayment(@RequestBody TicketOrderUpdatePaymentRequest requestDto) {
        return ResponseEntity.status(HttpStatus.OK).body(ticketOrderService.processPayment(requestDto));
    }
}
