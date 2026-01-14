package com.saga.airlinesystem.reservationservice.saga.repository;

import com.saga.airlinesystem.reservationservice.saga.model.SagaInstance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SagaInstanceRepository extends JpaRepository<SagaInstance, UUID> {
}
