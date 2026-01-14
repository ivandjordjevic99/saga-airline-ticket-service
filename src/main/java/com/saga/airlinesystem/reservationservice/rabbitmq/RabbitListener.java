package com.saga.airlinesystem.reservationservice.rabbitmq;

import com.saga.airlinesystem.reservationservice.dto.ReservationResponseDto;
import com.saga.airlinesystem.reservationservice.saga.orchestrator.CreateReservationSagaOrchestrator;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
public class RabbitListener {

    private final ObjectMapper objectMapper;
    private final CreateReservationSagaOrchestrator createReservationSagaOrchestrator;

    @org.springframework.amqp.rabbit.annotation.RabbitListener(queues = RabbitConfiguration.RESERVATION_QUEUE)
    public void handle(String payload, @Header("amqp_receivedRoutingKey") String routingKey) {
        ReservationResponseDto reservationResponseDto = objectMapper.readValue(payload, ReservationResponseDto.class);
        switch (routingKey) {
            case "user.validated":
                System.out.println("reservation.user_ok" + reservationResponseDto.getEmail());
                break;
            default:
                System.out.println("Nesto");
        }
    }

}
