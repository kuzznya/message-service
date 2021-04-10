package com.github.kuzznya.jb.message.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.kuzznya.jb.message.exception.NotFoundException;
import com.github.kuzznya.jb.message.model.ScheduledMessage;
import com.github.kuzznya.jb.message.service.ScheduledMessageSender;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
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
import java.util.UUID;

@SpringBootTest(properties = "spring.profiles.active=test")
@AutoConfigureMockMvc
class ScheduledMessageControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ScheduledMessageSender messageSender;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void scheduleMessage_WhenRegistered_ReturnOK() throws Exception {
        var message = new ScheduledMessage(UUID.randomUUID(), "id1", Collections.emptyList(), "1s");

        Mockito.when(messageSender.register(Mockito.any()))
                .thenReturn(message);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/messages/scheduled")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(message)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(message)));
    }

    @Test
    void scheduleMessage_WhenTemplateNotFound_Return404() throws Exception {
        var message = new ScheduledMessage(UUID.randomUUID(), "id1", Collections.emptyList(), "1s");

        Mockito.when(messageSender.register(Mockito.any()))
                .thenThrow(new NotFoundException("Template not found"));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/messages/scheduled")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(message)))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void getScheduledMessages_WhenNoData_ReturnEmptyList() throws Exception {
        Mockito.when(messageSender.getScheduledMessages())
                .thenReturn(Collections.emptyList());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/messages/scheduled"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json("[]"));
    }

    @Test
    void getScheduledMessages_WhenHasData_returnValidList() throws Exception {
        var message1 = new ScheduledMessage(UUID.randomUUID(), "id1", Collections.emptyList(), "1s");
        var message2 = new ScheduledMessage(UUID.randomUUID(), "id2", Collections.emptyList(), "2s");

        Mockito.when(messageSender.getScheduledMessages())
                .thenReturn(List.of(message1, message2));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/messages/scheduled"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content()
                        .json(objectMapper.writeValueAsString(List.of(message1, message2))));
    }

    @Test
    void removeScheduledMessage() throws Exception {
        Mockito.doNothing().when(messageSender).delete(Mockito.any());

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/messages/scheduled/" + UUID.randomUUID()))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}