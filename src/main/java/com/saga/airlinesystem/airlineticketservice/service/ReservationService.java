package com.saga.airlinesystem.reservationservice.service;

import com.saga.airlinesystem.reservationservice.dto.ReservationPollingResponseDto;
import com.saga.airlinesystem.reservationservice.dto.ReservationRequestDto;
import com.saga.airlinesystem.reservationservice.dto.ReservationUpdatePaymentRequest;
import com.saga.airlinesystem.reservationservice.dto.ReservationUpdatePaymentResponse;

public interface ReservationService {

    ReservationPollingResponseDto createReservation(ReservationRequestDto reservationRequestDto);
    ReservationUpdatePaymentResponse processPayment(ReservationUpdatePaymentRequest reservationUpdatePaymentRequest);
}
