package com.saga.airlinesystem.airlineticketservice.inboxevents;

import java.util.UUID;

public interface InboxEventService {

    void saveInboxEvent(UUID messageId, String payload, InboxEventType inboxEventType);
}
