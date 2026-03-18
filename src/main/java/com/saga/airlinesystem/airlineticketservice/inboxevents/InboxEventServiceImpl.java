package com.saga.airlinesystem.airlineticketservice.inboxevents;

import com.saga.airlinesystem.airlineticketservice.exceptions.customexceptions.EventAlreadyReceivedException;
import com.saga.airlinesystem.airlineticketservice.exceptions.customexceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class InboxEventServiceImpl implements InboxEventService {

    private final InboxEventRepository inboxEventRepository;

    @Override
    public void saveInboxEvent(UUID messageId, String payload, InboxEventType inboxEventType) {
        try {
            log.info("Saving event: {}, type: {}", messageId, inboxEventType);
            InboxEvent newInboxEvent = new InboxEvent();
            newInboxEvent.setMessageId(messageId);
            newInboxEvent.setPayload(payload);
            newInboxEvent.setReceivedAt(OffsetDateTime.now());
            newInboxEvent.setInboxEventType(inboxEventType);
            newInboxEvent.setStatus(InboxEventStatus.PENDING);
            newInboxEvent.setRetryCount(0);

            inboxEventRepository.save(newInboxEvent);
            log.info("Event: {}, type: {} successfully saved", messageId, inboxEventType);
        } catch (DataIntegrityViolationException e){
            throw new EventAlreadyReceivedException("Event with message id " + messageId + " already received");
        }


    }

}
