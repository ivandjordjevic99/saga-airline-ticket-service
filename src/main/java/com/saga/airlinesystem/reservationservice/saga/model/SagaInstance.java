package com.saga.airlinesystem.reservationservice.saga.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "saga_instances")
@Getter
@Setter
public class SagaInstance {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "transaction_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private SagaTransactionType transactionType;

    @Column(name = "reservation_id", nullable = false)
    private UUID reservationId;

    @Column(name = "last_step")
    private String lastStep;

    @Column(name = "state", nullable = false)
    private String state;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    public SagaInstance() {
        this.state = "STARTED";
    }
}
