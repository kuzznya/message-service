package com.github.kuzznya.jb.message.service;

import com.github.kuzznya.jb.message.model.MessageTemplate;
import com.github.kuzznya.jb.message.model.MessageVariable;

import java.util.List;

public interface TemplateResolver {
    String resolve(MessageTemplate template, List<MessageVariable> variables);
}
