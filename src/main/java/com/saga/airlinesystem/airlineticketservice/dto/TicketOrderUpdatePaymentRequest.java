package com.saga.airlinesystem.airlineticketservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TicketOrderUpdatePaymentRequest {

    @JsonProperty("ticket_order_id")
    private final String ticketOrderId;
}
