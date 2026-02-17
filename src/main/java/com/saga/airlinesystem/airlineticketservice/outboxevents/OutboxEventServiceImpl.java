package com.saga.airlinesystem.airlineticketservice.outboxevents;

import com.saga.airlinesystem.airlineticketservice.rabbitmq.messages.BaseMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
@Slf4j
public class OutboxEventServiceImpl implements OutboxEventService {

    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;

    @Override
    public void persistOutboxEvent(String exchange, String routingKey, BaseMessage payload) {
        OutboxEvent outboxEvent = new OutboxEvent(exchange, routingKey, objectMapper.writeValueAsString(payload));
        outboxEventRepository.save(outboxEvent);
        log.info("Outbox event with exchange {}, routing key {}, message {} saved successfully",
                exchange, routingKey, payload.getId());
    }
}
