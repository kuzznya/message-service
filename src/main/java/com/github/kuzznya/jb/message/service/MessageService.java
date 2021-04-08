package com.github.kuzznya.jb.message.service;

import com.github.kuzznya.jb.message.model.Message;
import com.github.kuzznya.jb.message.model.MessageTemplate;
import com.github.kuzznya.jb.message.model.MessageVariable;

import java.util.List;
import java.util.Optional;

public interface MessageService {
    MessageTemplate save(MessageTemplate template);
    void deleteTemplate(String id);
    Optional<MessageTemplate> getTemplate(String id);
    Message processMessage(String templateId, List<MessageVariable> variables);
    Message send(String templateId, List<MessageVariable> variables);
}
