package com.saga.airlinesystem.airlineticketservice.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "ticket_orders")
@Getter
@Setter
@Slf4j
public class TicketOrder {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String email;

    @Column(name = "flight_id", nullable = false)
    private UUID flightId;

    @Column(name = "seat_number", nullable = false)
    private String seatNumber;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @Column(name = "miles")
    private Integer miles;

    @Column(name = "expires_at")
    private OffsetDateTime expiresAt;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private TicketOrderStatus status;

    public TicketOrder(UUID id, String email, UUID flightId, String seatNumber) {
        this.id = id;
        this.email = email;
        this.flightId = flightId;
        this.seatNumber = seatNumber;
        OffsetDateTime now = OffsetDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
        this.status = TicketOrderStatus.IN_PROGRESS;
        this.miles = 0;
    }

    public TicketOrder() {

    }

    public void setStatus(TicketOrderStatus status) {
        this.updatedAt = OffsetDateTime.now();
        this.status = status;
    }
}
