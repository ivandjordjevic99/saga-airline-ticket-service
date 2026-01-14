package com.saga.airlinesystem.reservationservice.repository;

import com.saga.airlinesystem.reservationservice.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ReservationRepository extends JpaRepository<Reservation, UUID> {
}

