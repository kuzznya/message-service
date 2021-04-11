package com.github.kuzznya.jb.message.service;

import com.github.kuzznya.jb.message.entity.MessageTemplateEntity;
import com.github.kuzznya.jb.message.entity.ScheduledMessageEntity;
import com.github.kuzznya.jb.message.exception.IllegalFormatException;
import com.github.kuzznya.jb.message.exception.TemplateProcessingException;
import com.github.kuzznya.jb.message.model.Message;
import com.github.kuzznya.jb.message.model.ScheduledMessage;
import com.github.kuzznya.jb.message.repository.MessageRepository;
import com.github.kuzznya.jb.message.repository.TemplateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.autoconfigure.task.TaskSchedulingAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfiguration;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = {SchedulingConfiguration.class, TaskSchedulingAutoConfiguration.class, JacksonAutoConfiguration.class, SpringSchedulerMessageSender.class})
@EnableScheduling
class SpringSchedulerMessageSenderTest {

    @Autowired
    private ScheduledMessageSender scheduledMessageSender;
    @MockBean
    private TemplateRepository templateRepository;
    @MockBean
    private MessageRepository messageRepository;
    @MockBean
    private MessageService messageService;

    @BeforeEach
    void setUp() {
        Mockito.when(messageRepository.findAll()).thenReturn(Collections.emptyList());
    }

    @Test
    void register_WhenValidTemplate_ScheduleSending() {
        var entity = new MessageTemplateEntity("testId", "Template",
                Collections.emptyList(), Collections.emptyList(), Collections.emptyList());
        Mockito.when(messageService.processMessage(Mockito.any(), Mockito.any()))
                .thenReturn(new Message("OK"));
        Mockito.when(templateRepository.findById(Mockito.any()))
                .thenReturn(Optional.of(entity));
        Mockito.when(messageRepository.save(Mockito.any())).then(invocation -> {
            ScheduledMessageEntity entityToSave = invocation.getArgument(0);
            entityToSave.setId(UUID.randomUUID());
            return entityToSave;
        });

        AtomicBoolean called = new AtomicBoolean(false);
        Mockito.when(messageService.send(Mockito.any(), Mockito.any()))
                .then(invocation -> {
                    called.set(true);
                    return new Message("OK");
                });

        var msg = scheduledMessageSender.register(new ScheduledMessage(null, "testId", Collections.emptyList(), "2s"));

        try {
            Thread.sleep(2_100);
        } catch (Exception ignored) {
        }
        assertTrue(called.get());

        scheduledMessageSender.cancel(msg.getId());
    }

    @Test
    void register_WhenInvalidTemplate_ThrowException() {
        Mockito.when(messageService.processMessage(Mockito.any(), Mockito.any()))
                .thenThrow(new TemplateProcessingException());

        var message = new ScheduledMessage(null, "testId", Collections.emptyList(), "5s");
        assertThrows(TemplateProcessingException.class, () -> scheduledMessageSender.register(message));
    }

    @Test
    void register_WhenValidIntervals_ScheduleMessages() {
        var entity = new MessageTemplateEntity("testId", "Template",
                Collections.emptyList(), Collections.emptyList(), Collections.emptyList());
        Mockito.when(messageService.processMessage(Mockito.any(), Mockito.any()))
                .thenReturn(new Message("OK"));
        Mockito.when(templateRepository.findById(Mockito.any()))
                .thenReturn(Optional.of(entity));
        Mockito.when(messageRepository.save(Mockito.any()))
                .then(invocation -> {
                    ScheduledMessageEntity entityToSave = invocation.getArgument(0);
                    entityToSave.setId(UUID.randomUUID());
                    return entityToSave;
                });

        var msg1 = scheduledMessageSender.register(new ScheduledMessage(null, "testId", Collections.emptyList(), "2s"));
        var msg2 = scheduledMessageSender.register(new ScheduledMessage(null, "testId", Collections.emptyList(), "5m"));
        var msg3 = scheduledMessageSender.register(new ScheduledMessage(null, "testId", Collections.emptyList(), "10h"));
        var msg4 = scheduledMessageSender.register(new ScheduledMessage(null, "testId", Collections.emptyList(), "2000"));
        var msg5 = scheduledMessageSender.register(new ScheduledMessage(null, "testId", Collections.emptyList(), "* * * * * *"));
        var msg6 = scheduledMessageSender.register(new ScheduledMessage(null, "testId", Collections.emptyList(), "0/5 * * * * *"));
        var msg7 = scheduledMessageSender.register(new ScheduledMessage(null, "testId", Collections.emptyList(), "* 20 * * * *"));

        scheduledMessageSender.cancel(msg1.getId());
        scheduledMessageSender.cancel(msg2.getId());
        scheduledMessageSender.cancel(msg3.getId());
        scheduledMessageSender.cancel(msg4.getId());
        scheduledMessageSender.cancel(msg5.getId());
        scheduledMessageSender.cancel(msg6.getId());
        scheduledMessageSender.cancel(msg7.getId());
    }

    @Test
    void register_WhenInvalidInterval_ThrowException() {
        Mockito.when(messageService.processMessage(Mockito.any(), Mockito.any()))
                .thenReturn(new Message("OK"));
        var entity = new MessageTemplateEntity("testId", "OK",
                Collections.emptyList(), Collections.emptyList(), Collections.emptyList());
        Mockito.when(templateRepository.findById(Mockito.any()))
                .thenReturn(Optional.of(entity));
        Mockito.when(messageRepository.save(Mockito.any()))
                .then(invocation -> invocation.getArgument(0));

        var message1 = new ScheduledMessage(null, "testId", Collections.emptyList(), "5bs");
        var message2 = new ScheduledMessage(null, "testId", Collections.emptyList(), "2a");
        var message3 = new ScheduledMessage(null, "testId", Collections.emptyList(), "abc");
        var message4 = new ScheduledMessage(null, "testId", Collections.emptyList(), "* * * * *");
        var message5 = new ScheduledMessage(null, "testId", Collections.emptyList(), "* * d * * *");
        var message6 = new ScheduledMessage(null, "testId", Collections.emptyList(), "* * * * ! *");

        assertThrows(IllegalFormatException.class, () -> scheduledMessageSender.register(message1));
        assertThrows(IllegalFormatException.class, () -> scheduledMessageSender.register(message2));
        assertThrows(IllegalFormatException.class, () -> scheduledMessageSender.register(message3));
        assertThrows(IllegalFormatException.class, () -> scheduledMessageSender.register(message4));
        assertThrows(IllegalFormatException.class, () -> scheduledMessageSender.register(message5));
        assertThrows(IllegalFormatException.class, () -> scheduledMessageSender.register(message6));
    }

    @Test
    void getScheduledMessages_WhenNoMessages_ReturnEmptyList() {
        Mockito.when(messageRepository.findAll())
                .thenReturn(Collections.emptyList());

        assertTrue(scheduledMessageSender.getScheduledMessages().isEmpty());
    }

    @Test
    void getScheduledMessages_WhenMessagesExist_ReturnValidList() {
        var template1 = new MessageTemplateEntity("id1", "OK1", Collections.emptyList(), Collections.emptyList(), Collections.emptyList());
        var template2 = new MessageTemplateEntity("id2", "OK2", Collections.emptyList(), Collections.emptyList(), Collections.emptyList());
        var entity1 = new ScheduledMessageEntity(null, template1, Collections.emptyList(), "5s");
        var entity2 = new ScheduledMessageEntity(null, template2, Collections.emptyList(), "10m");

        var message1 = new ScheduledMessage(null, "id1", Collections.emptyList(), "5s");
        var message2 = new ScheduledMessage(null, "id2", Collections.emptyList(), "10m");

        Mockito.when(messageRepository.findAll())
                .thenReturn(List.of(entity1, entity2));

        assertEquals(List.of(message1, message2), scheduledMessageSender.getScheduledMessages());
    }

    @Test
    void cancel_WhenScheduled_CancelTask() {
        var entity = new MessageTemplateEntity("testId", "Template",
                Collections.emptyList(), Collections.emptyList(), Collections.emptyList());
        Mockito.when(messageService.processMessage(Mockito.any(), Mockito.any()))
                .thenReturn(new Message("OK"));
        Mockito.when(templateRepository.findById(Mockito.any()))
                .thenReturn(Optional.of(entity));
        Mockito.when(messageRepository.save(Mockito.any())).then(invocation -> {
            ScheduledMessageEntity entityToSave = invocation.getArgument(0);
            entityToSave.setId(UUID.randomUUID());
            return entityToSave;
        });


        Mockito.when(messageService.send(Mockito.any(), Mockito.any())).then(invocation -> new Message("OK"));

        AtomicBoolean called = new AtomicBoolean(false);
        Mockito.when(messageService.send(Mockito.any(), Mockito.any()))
                .then(invocation -> {
                    called.set(true);
                    return new Message("OK");
                });

        var message = scheduledMessageSender.register(
                new ScheduledMessage(UUID.randomUUID(), "testId", Collections.emptyList(), "2s"));
        scheduledMessageSender.cancel(message.getId());
        called.set(false);
        try {
            Thread.sleep(3_100);
        } catch (Exception ignored) {
        }
        assertFalse(called.get());
    }
}