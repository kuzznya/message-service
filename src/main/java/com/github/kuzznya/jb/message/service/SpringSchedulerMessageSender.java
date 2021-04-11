package com.github.kuzznya.jb.message.service;

import com.github.kuzznya.jb.message.entity.ScheduledMessageEntity;
import com.github.kuzznya.jb.message.entity.VariableValueEntity;
import com.github.kuzznya.jb.message.exception.IllegalFormatException;
import com.github.kuzznya.jb.message.exception.NotFoundException;
import com.github.kuzznya.jb.message.model.MessageVariable;
import com.github.kuzznya.jb.message.model.ScheduledMessage;
import com.github.kuzznya.jb.message.repository.MessageRepository;
import com.github.kuzznya.jb.message.repository.TemplateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.convert.DurationStyle;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.scheduling.support.PeriodicTrigger;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SpringSchedulerMessageSender implements ScheduledMessageSender {

    private final MessageService messageService;
    private final TemplateRepository templateRepository;
    private final MessageRepository messageRepository;
    private final TaskScheduler scheduler;

    private final Map<UUID, ScheduledFuture<?>> scheduledTasks = new HashMap<>();

    @PostConstruct
    private void initialize() {
        messageRepository.findAll()
                .stream()
                .map(this::entityToModel)
                .forEach(this::schedule);
    }

    @Override
    public ScheduledMessage register(ScheduledMessage message) {
        messageService.processMessage(message.getTemplateId(), message.getVariables()); // assert that message can be processed
        var entity = modelToEntity(message);
        entity.setId(null);
        var savedModel = entityToModel(messageRepository.save(entity));
        schedule(savedModel);
        return savedModel;
    }

    @Override
    public List<ScheduledMessage> getScheduledMessages() {
        return messageRepository.findAll()
                .stream()
                .map(this::entityToModel)
                .collect(Collectors.toList());
    }

    @Override
    public void cancel(UUID id) {
        Optional.ofNullable(scheduledTasks.remove(id))
                .ifPresent(task -> task.cancel(true));
        try {
            messageRepository.deleteById(id);
        } catch (Exception ex) {
            log.warn("Scheduled message delete error", ex);
        }
    }


    private void schedule(ScheduledMessage message) {
        var future = scheduler.schedule(() -> sendMessage(message),
                createTrigger(message.getInterval()));
        scheduledTasks.put(message.getId(), future);
    }

    private void sendMessage(ScheduledMessage message) {
        if (!messageRepository.existsById(message.getId())) {
            cancel(message.getId());
        }
        try {
            messageService.send(message.getTemplateId(), message.getVariables());
        } catch (Exception ex) {
            log.error("Scheduled message sending error", ex);
        }
    }

    private Trigger createTrigger(String interval) {
        try {
            interval = interval.strip();

            if (interval.matches("\\d+[A-Za-z]*")) {
                long value = DurationStyle.detectAndParse(interval).toMillis();
                return new PeriodicTrigger(value, TimeUnit.MILLISECONDS);
            }
            return new CronTrigger(interval);
        } catch (Exception ex) {
            throw new IllegalFormatException(ex.getMessage(), ex);
        }
    }

    private ScheduledMessage entityToModel(ScheduledMessageEntity entity) {
        var variables = entity.getVariables()
                .stream()
                .map(valueEntity -> new MessageVariable(valueEntity.getKey(), valueEntity.getValue()))
                .collect(Collectors.toList());
        return new ScheduledMessage(entity.getId(), entity.getTemplate().getId(), variables, entity.getSendInterval());
    }

    private ScheduledMessageEntity modelToEntity(ScheduledMessage model) {
        var templateEntity = templateRepository.findById(model.getTemplateId())
                .orElseThrow(() -> new NotFoundException("Template with id " + model.getTemplateId() + " not found"));
        var variables = model.getVariables()
                .stream()
                .map(variable -> new VariableValueEntity(null, variable.getKey(), variable.getValue()))
                .collect(Collectors.toList());
        return new ScheduledMessageEntity(null, templateEntity, variables, model.getInterval());
    }
}
