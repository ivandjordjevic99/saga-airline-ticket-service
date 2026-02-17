package com.saga.airlinesystem.airlineticketservice.controller;

import com.saga.airlinesystem.airlineticketservice.dto.*;
import com.saga.airlinesystem.airlineticketservice.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping
    public ResponseEntity<ReservationPollingResponseDto> createReservation(@RequestBody ReservationRequestDto reservationRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reservationService.createReservation(reservationRequestDto));
    }

    @PutMapping("/process-payment")
    public ResponseEntity<ReservationUpdatePaymentResponse> updateReservationPayment(@RequestBody ReservationUpdatePaymentRequest requestDto) {
        return ResponseEntity.status(HttpStatus.OK).body(reservationService.processPayment(requestDto));
    }
}
