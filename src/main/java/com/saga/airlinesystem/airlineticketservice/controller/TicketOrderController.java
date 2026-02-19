package com.saga.airlinesystem.airlineticketservice.controller;

import com.saga.airlinesystem.airlineticketservice.dto.*;
import com.saga.airlinesystem.airlineticketservice.service.TicketOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/ticket-orders")
@RequiredArgsConstructor
public class TicketOrderController {

    private final TicketOrderService ticketOrderService;

    @PostMapping
    public ResponseEntity<CreateTicketOrderResponseDto> createTicketOrder(@RequestBody TicketOrderRequestDto ticketOrderRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ticketOrderService.createTicketOrder(ticketOrderRequestDto));
    }

    @PutMapping("/process-payment")
    public ResponseEntity<TicketOrderUpdatePaymentResponse> updateTicketOrderPayment(@RequestBody TicketOrderUpdatePaymentRequest requestDto) {
        return ResponseEntity.status(HttpStatus.OK).body(ticketOrderService.processPayment(requestDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TicketOrderResponseDto> getTicketOrderById(@PathVariable UUID id) {
        return ResponseEntity.status(HttpStatus.OK).body(ticketOrderService.getTicketOrderById(id));
    }
}
