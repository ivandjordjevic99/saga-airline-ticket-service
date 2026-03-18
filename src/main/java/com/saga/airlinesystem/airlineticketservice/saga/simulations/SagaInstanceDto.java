package com.saga.airlinesystem.airlineticketservice.saga.simulations;

import com.saga.airlinesystem.airlineticketservice.saga.model.SagaState;
import com.saga.airlinesystem.airlineticketservice.saga.model.SagaTransactionType;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
public class SagaInstanceDto {
    private UUID id;
    private SagaTransactionType transactionType;
    private UUID aggregateId;
    private SagaState state;
    private OffsetDateTime updatedAt;
}
