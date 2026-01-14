package com.saga.airlinesystem.reservationservice.service;

import com.saga.airlinesystem.reservationservice.dto.ReservationRequestDto;
import com.saga.airlinesystem.reservationservice.dto.ReservationResponseDto;

import java.util.UUID;

public interface ReservationService {

    ReservationResponseDto createReservation(ReservationRequestDto reservationRequestDto);
    ReservationResponseDto getReservationById(UUID reservationId);
}
