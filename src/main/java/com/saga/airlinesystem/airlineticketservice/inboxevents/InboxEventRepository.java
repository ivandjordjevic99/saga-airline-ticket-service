package com.saga.airlinesystem.airlineticketservice.inboxevents;

import com.saga.airlinesystem.airlineticketservice.outboxevents.OutboxEvent;
import com.saga.airlinesystem.airlineticketservice.outboxevents.OutboxEventStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InboxEventRepository extends JpaRepository<InboxEvent, UUID> {

    List<InboxEvent> findTop10ByStatusOrderByReceivedAtAsc(InboxEventStatus status);
    List<InboxEvent> findByStatusAndUpdatedAtBefore(InboxEventStatus status, OffsetDateTime time);

}
