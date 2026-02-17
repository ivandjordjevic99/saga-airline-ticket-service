package com.saga.airlinesystem.airlineticketservice.saga.repository;

import com.saga.airlinesystem.airlineticketservice.saga.model.SagaInstance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SagaInstanceRepository extends JpaRepository<SagaInstance, UUID> {

    Optional<SagaInstance> findByAggregateId(UUID id);
}
