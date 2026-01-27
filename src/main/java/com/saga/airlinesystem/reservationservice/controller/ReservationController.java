package com.saga.airlinesystem.reservationservice.controller;

import com.saga.airlinesystem.reservationservice.dto.ReservationRequestDto;
import com.saga.airlinesystem.reservationservice.dto.ReservationResponseDto;
import com.saga.airlinesystem.reservationservice.dto.ReservationUpdatePaymentRequest;
import com.saga.airlinesystem.reservationservice.dto.ReservationUpdatePaymentResponse;
import com.saga.airlinesystem.reservationservice.saga.orchestrator.CreateReservationSagaOrchestrator;
import com.saga.airlinesystem.reservationservice.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final CreateReservationSagaOrchestrator createReservationSagaOrchestrator;
    private final ReservationService reservationService;

    @PostMapping
    public ResponseEntity<ReservationResponseDto> createReservation(@RequestBody ReservationRequestDto reservationRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(createReservationSagaOrchestrator.startSaga(reservationRequestDto));
    }

    @PutMapping("/process-payment")
    public ResponseEntity<ReservationUpdatePaymentResponse> updateReservationPayment(@RequestBody ReservationUpdatePaymentRequest requestDto) {
        return ResponseEntity.status(HttpStatus.OK).body(reservationService.processPayment(requestDto));
    }
}
