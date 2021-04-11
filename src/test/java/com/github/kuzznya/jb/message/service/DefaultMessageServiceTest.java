package com.github.kuzznya.jb.message.service;

import com.github.kuzznya.jb.message.entity.MessageTemplateEntity;
import com.github.kuzznya.jb.message.entity.VariableDefinitionEntity;
import com.github.kuzznya.jb.message.exception.ConflictException;
import com.github.kuzznya.jb.message.exception.NotFoundException;
import com.github.kuzznya.jb.message.model.*;
import com.github.kuzznya.jb.message.repository.TemplateRepository;
import com.github.kuzznya.jb.message.service.sender.SenderService;
import com.github.kuzznya.jb.message.service.template.SpelTemplateProcessor;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = {JacksonAutoConfiguration.class, SpelTemplateProcessor.class, DefaultMessageService.class})
class DefaultMessageServiceTest {

    @Autowired
    private MessageService messageService;
    @MockBean
    private SenderService senderService;
    @MockBean
    private TemplateRepository templateRepository;

    @Test
    void save_WhenValidTemplate_SaveValue() {
        Mockito.when(templateRepository.save(Mockito.any()))
                .then((Answer<MessageTemplateEntity>) invocation -> invocation.getArgument(0));
        var template = new MessageTemplate(
                "testTemplate",
                "Template $var1$ and $var2$",
                List.of(new TemplateVariable("var1", VariableType.STRING),
                        new TemplateVariable("var2", VariableType.INT)),
                List.of(URI.create("https://google.com")));
        assertEquals(template, messageService.save(template));
    }

    @Test
    void save_WhenTemplateWithIdExists_ThrowConflictException() {
        Mockito.when(templateRepository.existsById(Mockito.any()))
                .thenReturn(true);

        var template = new MessageTemplate(
                "testTemplate",
                "Template $var1$ and $var2$",
                List.of(new TemplateVariable("var1", VariableType.STRING),
                        new TemplateVariable("var2", VariableType.INT)),
                List.of(URI.create("https://google.com")));

        assertThrows(ConflictException.class, () -> messageService.save(template));
    }

    @Test
    void getAllTemplates_WhenNoTemplates_ReturnEmptyList() {
        Mockito.when(templateRepository.findAll())
                .thenReturn(Collections.emptyList());

        assertTrue(messageService.getAllTemplates().isEmpty());
    }

    @Test
    void getAllTemplates_WhenTemplatesExist_ReturnValidList() {
        var entity1 = new MessageTemplateEntity("testId1", "Template",
                Collections.emptyList(), Collections.emptyList(), Collections.emptyList());
        var entity2 = new MessageTemplateEntity("testId2", "Template",
                Collections.emptyList(), Collections.emptyList(), Collections.emptyList());

        Mockito.when(templateRepository.findAll())
                .thenReturn(List.of(entity1, entity2));

        var model1 = new MessageTemplate("testId1", "Template", Collections.emptyList(), Collections.emptyList());
        var model2 = new MessageTemplate("testId2", "Template", Collections.emptyList(), Collections.emptyList());

        assertEquals(List.of(model1, model2), messageService.getAllTemplates());
    }

    @Test
    void deleteTemplate_WhenAnyId_DeleteEntity() {
        Mockito.doNothing().when(templateRepository).deleteById(Mockito.any());
        messageService.deleteTemplate("testId");
        messageService.deleteTemplate("any id");
        messageService.deleteTemplate("do not throw");
    }

    @Test
    void getTemplate_WhenValidId_ReturnTemplate() {
        var validEntity = new MessageTemplateEntity("testId", "Template", null, null, null);

        var validModel = new MessageTemplate("testId", "Template",
                Collections.emptyList(), Collections.emptyList());

        Mockito.when(templateRepository.findById(Mockito.eq("testId"))).thenReturn(Optional.of(validEntity));

        var response = messageService.getTemplate("testId");
        assertTrue(response.isPresent(), "Optional is empty but should contain data");
        assertEquals(validModel, response.get());
    }

    @Test
    void getTemplate_WhenInvalidId_ReturnEmptyOptional() {
        Mockito.when(templateRepository.findById(Mockito.eq("invalidId"))).thenReturn(Optional.empty());

        var response = messageService.getTemplate("invalidId");
        assertTrue(response.isEmpty(), "Optional is not empty though id is invalid");
    }

    @Test
    void processMessage_WhenValidData_ReturnValidMessage() {
        var validEntity = new MessageTemplateEntity(
                "testId",
                "The Answer to the Ultimate Question of $var1$ is $var2$",
                null,
                List.of(
                        new VariableDefinitionEntity(null, "var1", VariableType.STRING),
                        new VariableDefinitionEntity(null, "var2", VariableType.INT)),
                Collections.emptyList());

        Mockito.when(templateRepository.findById(Mockito.eq("testId"))).thenReturn(Optional.of(validEntity));

        var message = messageService.processMessage("testId",
                 List.of(new MessageVariable("var1", "Life, The Universe, and Everything"), new MessageVariable("var2", "42")));
        assertEquals("The Answer to the Ultimate Question of Life, The Universe, and Everything is 42", message.getMessage());
    }

    @Test
    void processMessage_WhenVarWithoutDefinition_ReturnValidMessage() {
        var validEntity = new MessageTemplateEntity(
                "testId",
                "The Answer to the Ultimate Question of $var1$ is $var2$",
                Collections.emptyList(),
                List.of(new VariableDefinitionEntity(null, "var2", VariableType.INT)),
                Collections.emptyList());

        Mockito.when(templateRepository.findById(Mockito.eq("testId"))).thenReturn(Optional.of(validEntity));

        var message = messageService.processMessage("testId",
                List.of(new MessageVariable("var1", "Life, The Universe, and Everything"), new MessageVariable("var2", "42")));
        assertEquals("The Answer to the Ultimate Question of Life, The Universe, and Everything is 42", message.getMessage());
    }

    @Test
    void processMessage_WhenNoTemplateFound_ThrowNotFoundException() {
        Mockito.when(templateRepository.findById(Mockito.any()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> messageService.processMessage("test", Collections.emptyList()));
    }

    @Test
    void send_WhenValidTemplate_SendMessage() {
        var validEntity = new MessageTemplateEntity(
                "testId",
                "The Answer to the Ultimate Question of $var1$ is $var2$",
                List.of(URI.create("https://google.com"), URI.create("mailto:test@test.com")),
                List.of(
                        new VariableDefinitionEntity(null, "var1", VariableType.STRING),
                        new VariableDefinitionEntity(null, "var2", VariableType.INT)),
                Collections.emptyList());

        Mockito.when(templateRepository.findById(Mockito.any()))
                .thenReturn(Optional.of(validEntity));

        Mockito.doNothing().when(senderService).send(Mockito.any(), Mockito.any());

        assertEquals("The Answer to the Ultimate Question of Life, The Universe, and Everything is 42",
                messageService.send("testId", List.of(
                        new MessageVariable("var1", "Life, The Universe, and Everything"),
                        new MessageVariable("var2", "42")))
                        .getMessage());

        Mockito.verify(senderService, Mockito.times(2)).send(Mockito.any(), Mockito.any());
    }

    @Test
    void send_WhenInvalidTemplateId_ThrowNotFoundException() {
        Mockito.when(templateRepository.findById(Mockito.any()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> messageService.send("test", Collections.emptyList()));
    }
}