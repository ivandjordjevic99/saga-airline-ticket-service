package com.saga.airlinesystem.reservationservice.saga.orchestrator;

import com.saga.airlinesystem.reservationservice.dto.ReservationRequestDto;
import com.saga.airlinesystem.reservationservice.dto.ReservationResponseDto;
import com.saga.airlinesystem.reservationservice.rabbitmq.RabbitProducer;
import com.saga.airlinesystem.reservationservice.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateReservationSagaOrchestrator {

    private final ReservationService reservationService;
    private final RabbitProducer rabbitProducer;

    public ReservationResponseDto startSaga(ReservationRequestDto reservationRequestDto) {
        System.out.println("Starting saga");
        return reservationService.createReservation(reservationRequestDto);
    }

    public void reserveSeat(ReservationResponseDto reservationResponseDto) {
        System.out.println("Reserving the seat");
        rabbitProducer.sendUserValidated(reservationResponseDto);
    }
}
