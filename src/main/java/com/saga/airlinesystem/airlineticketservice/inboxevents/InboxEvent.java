package com.saga.airlinesystem.airlineticketservice.inboxevents;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "inbox_events")
@Getter
@Setter
@NoArgsConstructor
public class InboxEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "message_id", nullable = false, unique = true)
    private UUID messageId;

    @Column(name = "event_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private InboxEventType inboxEventType;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private InboxEventStatus status;

    @Column(name = "received_at", nullable = false)
    private OffsetDateTime receivedAt;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    @Column(name = "payload", columnDefinition = "TEXT", nullable = false)
    private String payload;

    @Column(name = "retry_count", nullable = false)
    private Integer retryCount;

    public void incrementRetryCount() {
        this.retryCount++;
    }

    public void setStatus(InboxEventStatus status) {
        this.status = status;
        this.updatedAt = OffsetDateTime.now();
    }
}
