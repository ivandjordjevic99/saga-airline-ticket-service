package com.saga.airlinesystem.airlineticketservice.outboxevents;

import com.saga.airlinesystem.airlineticketservice.rabbitmq.RabbitProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class OutboxProcessorTask {

    private final OutboxEventRepository outboxEventRepository;
    private final RabbitProducer reservationProducer;

    @Scheduled(fixedRate = 5000)
    @Transactional
    public void process() {
        List<OutboxEvent> outboxes = outboxEventRepository.findTop10ByStatusOrderByCreatedAtDesc(OutboxEventStatus.PENDING);

        for (OutboxEvent outbox : outboxes) {
            reservationProducer.sendEvent(outbox.getExchange(), outbox.getRoutingKey(), outbox.getPayload());
            outbox.setStatus(OutboxEventStatus.SENT);
            log.info("Outbox event {} sent to {} exchange, routing key {}", outbox.getId(), outbox.getExchange(), outbox.getRoutingKey());
        }
    }
}
