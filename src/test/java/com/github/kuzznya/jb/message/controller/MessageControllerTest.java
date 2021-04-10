package com.github.kuzznya.jb.message.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.kuzznya.jb.message.exception.ConflictException;
import com.github.kuzznya.jb.message.model.Message;
import com.github.kuzznya.jb.message.model.MessageTemplate;
import com.github.kuzznya.jb.message.service.MessageService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@SpringBootTest(properties = "spring.profiles.active=test")
@AutoConfigureMockMvc
class MessageControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private MessageService messageService;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void saveTemplate_WhenServiceSaves_ReturnOK() throws Exception {
        Mockito.when(messageService.save(Mockito.any()))
                .thenAnswer((Answer<MessageTemplate>) invocation -> invocation.getArgument(0));

        var template = new MessageTemplate(
                "id1",
                "Test $var1$ $var2$",
                Collections.emptyList(),
                Collections.emptyList());

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/v1/templates")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(template)))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void saveTemplate_WhenConflictException_Return409() throws Exception {
        Mockito.when(messageService.save(Mockito.any()))
                .thenThrow(new ConflictException());

        var template = new MessageTemplate(
                "id1",
                "Test $var1$ $var2$",
                Collections.emptyList(),
                Collections.emptyList());

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/v1/templates")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(template)))
                .andExpect(MockMvcResultMatchers.status().isConflict());
    }

    @Test
    void getAllTemplates_WhenNoTemplates_ReturnEmptyList() throws Exception {
        Mockito.when(messageService.getAllTemplates())
                .thenReturn(Collections.emptyList());

        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/v1/templates"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json("[]"));
    }

    @Test
    void getAllTemplates_WhenHasTemplates_ReturnTemplates() throws Exception {
        var template1 = new MessageTemplate(
                "id1",
                "Test $var1$ $var2$",
                Collections.emptyList(),
                Collections.emptyList());

        var template2 = new MessageTemplate(
                "id2",
                "Test $var1$ $var2$",
                Collections.emptyList(),
                Collections.emptyList());

        Mockito.when(messageService.getAllTemplates())
                .thenReturn(List.of(template1, template2));

        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/v1/templates"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content()
                        .json(objectMapper.writeValueAsString(List.of(template1, template2))));
    }

    @Test
    void getTemplate_WhenTemplateExists_ReturnTemplate() throws Exception {
        var template = new MessageTemplate(
                "id1",
                "Test $var1$ $var2$",
                Collections.emptyList(),
                Collections.emptyList());

        Mockito.when(messageService.getTemplate(Mockito.any()))
                .thenReturn(Optional.of(template));

        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/v1/templates/id1"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void deleteTemplate() throws Exception {
        Mockito.doNothing().when(messageService).deleteTemplate(Mockito.any());

        mockMvc.perform(MockMvcRequestBuilders
                .delete("/api/v1/templates/id1"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void processMessage() throws Exception {
        Mockito.when(messageService.processMessage(Mockito.any(), Mockito.any()))
                .thenReturn(new Message("Test message"));

        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/v1/templates/id1/message"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json("{\"message\": \"Test message\"}"));
    }

    @Test
    void sendMessage() throws Exception {
        Mockito.when(messageService.send(Mockito.any(), Mockito.any()))
                .thenReturn(new Message("Test message"));

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/v1/templates/id1/message"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json("{\"message\": \"Test message\"}"));
    }
}