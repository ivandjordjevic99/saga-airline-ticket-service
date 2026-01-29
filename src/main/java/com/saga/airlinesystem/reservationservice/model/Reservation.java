package com.saga.airlinesystem.reservationservice.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "reservations")
@Getter
@Setter
public class Reservation {

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
    private ReservationStatus status;

    public Reservation(UUID id, String email, UUID flightId, String seatNumber) {
        this.id = id;
        this.email = email;
        this.flightId = flightId;
        this.seatNumber = seatNumber;
        OffsetDateTime now = OffsetDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
        this.status = ReservationStatus.IN_PROGRESS;
        this.miles = 0;
    }

    public Reservation() {

    }

    public void setStatus(ReservationStatus status) {
        this.updatedAt = OffsetDateTime.now();
        this.status = status;
    }
}
