package com.saga.airlinesystem.reservationservice.saga.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "saga_instances")
@Getter
@Setter
@Slf4j
public class SagaInstance {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "transaction_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private SagaTransactionType transactionType;

    @Column(name = "reservation_id", nullable = false)
    private UUID reservationId;

    @Column(name = "state", nullable = false)
    @Enumerated(EnumType.STRING)
    private SagaState state;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    public SagaInstance() {
        this.state = SagaState.STARTED;
    }

    public SagaInstance(SagaTransactionType transactionType, UUID reservationId) {
        this.transactionType = transactionType;
        this.reservationId = reservationId;
        this.state = SagaState.STARTED;
        this.updatedAt = OffsetDateTime.now();
    }

    public void transitionTo(SagaState sagaState) {
        this.state = sagaState;
        this.updatedAt = OffsetDateTime.now();
    }
}
