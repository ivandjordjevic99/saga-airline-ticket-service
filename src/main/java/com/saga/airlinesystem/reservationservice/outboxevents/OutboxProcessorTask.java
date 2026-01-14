package com.saga.airlinesystem.reservationservice.outboxevents;

import com.saga.airlinesystem.reservationservice.rabbitmq.RabbitProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OutboxProcessorTask {

    private final OutboxEventRepository outboxEventRepository;
    private final RabbitProducer reservationProducer;

    @Scheduled(fixedRate = 5000)
    @Transactional
    public void process() {
        System.out.println("Processing outbox events");
        List<OutboxEvent> outboxes = outboxEventRepository.findTop10ByStatusOrderByCreatedAtDesc(OutboxEventStatus.PENDING);

        for (OutboxEvent outbox : outboxes) {
            System.out.println("Sending outbox event");
            reservationProducer.sendReservationCreated(outbox.getPayload());
            outbox.setStatus(OutboxEventStatus.SENT);
            System.out.println("Outbox event sent");
        }
    }
}
