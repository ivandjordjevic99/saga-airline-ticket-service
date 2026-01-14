package com.saga.airlinesystem.reservationservice.controller;

import com.saga.airlinesystem.reservationservice.dto.ReservationRequestDto;
import com.saga.airlinesystem.reservationservice.dto.ReservationResponseDto;
import com.saga.airlinesystem.reservationservice.saga.orchestrator.CreateReservationSagaOrchestrator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final CreateReservationSagaOrchestrator createReservationSagaOrchestrator;

    @PostMapping
    public ResponseEntity<ReservationResponseDto> createReservation(@RequestBody ReservationRequestDto reservationRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(createReservationSagaOrchestrator.startSaga(reservationRequestDto));
    }
}
