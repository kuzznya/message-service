package com.github.kuzznya.jb.message.service;

import com.github.kuzznya.jb.message.entity.MessageTemplateEntity;
import com.github.kuzznya.jb.message.entity.VariableDefinitionEntity;
import com.github.kuzznya.jb.message.model.MessageTemplate;
import com.github.kuzznya.jb.message.model.MessageVariable;
import com.github.kuzznya.jb.message.model.TemplateVariable;
import com.github.kuzznya.jb.message.model.VariableType;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
                null, new ArrayList<>(), null);
        validEntity.addVariableDefinition(
                new VariableDefinitionEntity(null, "var1", VariableType.STRING, null));
        validEntity.addVariableDefinition(
                new VariableDefinitionEntity(null, "var2", VariableType.INT, null));

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
                null, new ArrayList<>(), null);
        validEntity.addVariableDefinition(
                new VariableDefinitionEntity(null, "var2", VariableType.INT, null));

        Mockito.when(templateRepository.findById(Mockito.eq("testId"))).thenReturn(Optional.of(validEntity));

        var message = messageService.processMessage("testId",
                List.of(new MessageVariable("var1", "Life, The Universe, and Everything"), new MessageVariable("var2", "42")));
        assertEquals("The Answer to the Ultimate Question of Life, The Universe, and Everything is 42", message.getMessage());
    }
}