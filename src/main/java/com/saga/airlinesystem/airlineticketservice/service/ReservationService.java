package com.saga.airlinesystem.airlineticketservice.service;

import com.saga.airlinesystem.airlineticketservice.dto.ReservationPollingResponseDto;
import com.saga.airlinesystem.airlineticketservice.dto.ReservationRequestDto;
import com.saga.airlinesystem.airlineticketservice.dto.ReservationUpdatePaymentRequest;
import com.saga.airlinesystem.airlineticketservice.dto.ReservationUpdatePaymentResponse;

public interface ReservationService {

    ReservationPollingResponseDto createReservation(ReservationRequestDto reservationRequestDto);
    ReservationUpdatePaymentResponse processPayment(ReservationUpdatePaymentRequest reservationUpdatePaymentRequest);
}
