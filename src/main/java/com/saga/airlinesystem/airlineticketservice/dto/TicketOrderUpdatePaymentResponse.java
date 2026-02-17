package com.saga.airlinesystem.airlineticketservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TicketOrderUpdatePaymentResponse {

    @JsonProperty("ticket_order_id")
    private String ticketOrderId;
}
