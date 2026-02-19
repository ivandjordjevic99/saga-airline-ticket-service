package com.saga.airlinesystem.airlineticketservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.UUID;

@Data
public class CreateTicketOrderResponseDto {

    @JsonProperty("ticket_order_id")
    private UUID ticketOrderId;
}
