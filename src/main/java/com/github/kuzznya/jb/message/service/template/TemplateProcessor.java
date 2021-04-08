package com.github.kuzznya.jb.message.service.template;

import com.github.kuzznya.jb.message.model.MessageTemplate;
import com.github.kuzznya.jb.message.model.MessageVariable;

import java.util.List;

public interface TemplateProcessor {
    String process(MessageTemplate template, List<MessageVariable> variables);
}
