package com.saga.airlinesystem.reservationservice.service;

import com.saga.airlinesystem.reservationservice.dto.ReservationRequestDto;
import com.saga.airlinesystem.reservationservice.dto.ReservationResponseDto;
import com.saga.airlinesystem.reservationservice.dto.ReservationUpdatePaymentRequest;
import com.saga.airlinesystem.reservationservice.dto.ReservationUpdatePaymentResponse;
import com.saga.airlinesystem.reservationservice.model.Reservation;

import java.util.UUID;

public interface ReservationService {

    ReservationResponseDto createReservation(ReservationRequestDto reservationRequestDto);
    Reservation getReservationById(UUID reservationId);
    ReservationUpdatePaymentResponse processPayment(ReservationUpdatePaymentRequest reservationUpdatePaymentRequest);
}
