package com.saga.airlinesystem.airlineticketservice.repository;

import com.saga.airlinesystem.airlineticketservice.model.Reservation;
import com.saga.airlinesystem.airlineticketservice.model.ReservationStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReservationRepository extends JpaRepository<Reservation, UUID> {

    List<Reservation> findTop10ByStatusAndExpiresAtIsNotNullOrderByExpiresAtAsc(ReservationStatus status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select r from Reservation r where r.id = :id")
    Optional<Reservation> findByIdWithLock(@Param("id") UUID id);
}

