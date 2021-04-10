package com.github.kuzznya.jb.message.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.kuzznya.jb.message.entity.MessageTemplateEntity;
import com.github.kuzznya.jb.message.exception.ConflictException;
import com.github.kuzznya.jb.message.exception.NotFoundException;
import com.github.kuzznya.jb.message.model.Message;
import com.github.kuzznya.jb.message.model.MessageTemplate;
import com.github.kuzznya.jb.message.model.MessageVariable;
import com.github.kuzznya.jb.message.repository.TemplateRepository;
import com.github.kuzznya.jb.message.service.sender.SenderService;
import com.github.kuzznya.jb.message.service.template.TemplateProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class DefaultMessageService implements MessageService {

    private final ObjectMapper objectMapper;
    private final TemplateProcessor templateProcessor;
    private final TemplateRepository templateRepository;
    private final SenderService senderService;

    @Override
    public MessageTemplate save(MessageTemplate template) {
        if (templateRepository.existsById(template.getId()))
            throw new ConflictException("Template with ID " + template.getId() + " already exists");
        var entity = objectMapper.convertValue(template, MessageTemplateEntity.class);
        var result = templateRepository.save(entity);
        return objectMapper.convertValue(result, MessageTemplate.class);
    }

    @Override
    public void deleteTemplate(String id) {
        templateRepository.deleteById(id);
    }

    @Override
    public Optional<MessageTemplate> getTemplate(String id) {
        return templateRepository.findById(id)
                .map(entity -> objectMapper.convertValue(entity, MessageTemplate.class));
    }

    @Override
    public Message processMessage(String templateId, List<MessageVariable> variables) {
        var template = getTemplate(templateId)
                .orElseThrow(() -> new NotFoundException("Template with id " + templateId + " not found"));
        return new Message(templateProcessor.process(template, variables));
    }

    @Override
    @Transactional
    public Message send(String templateId, List<MessageVariable> variables) {
        var template = getTemplate(templateId)
                .orElseThrow(() -> new NotFoundException("Template with id " + templateId + " not found"));
        var message = new Message(templateProcessor.process(template, variables));
        template.getRecipients().forEach(uri -> senderService.send(message.getMessage(), uri));
        return message;
    }
}
