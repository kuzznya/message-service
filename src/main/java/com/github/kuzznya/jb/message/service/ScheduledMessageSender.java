package com.github.kuzznya.jb.message.service;

import com.github.kuzznya.jb.message.model.ScheduledMessage;

import java.util.List;
import java.util.UUID;

public interface ScheduledMessageSender {
    ScheduledMessage register(ScheduledMessage message);
    List<ScheduledMessage> getScheduledMessages();
    void delete(UUID id);
}
