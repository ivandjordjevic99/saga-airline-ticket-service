package com.saga.airlinesystem.airlineticketservice.repository;

import com.saga.airlinesystem.airlineticketservice.model.TicketOrder;
import com.saga.airlinesystem.airlineticketservice.model.TicketOrderStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TicketOrderRepository extends JpaRepository<TicketOrder, UUID> {

    List<TicketOrder> findTop10ByStatusAndExpiresAtIsNotNullOrderByExpiresAtAsc(TicketOrderStatus status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select r from TicketOrder r where r.id = :id")
    Optional<TicketOrder> findByIdWithLock(@Param("id") UUID id);
}

