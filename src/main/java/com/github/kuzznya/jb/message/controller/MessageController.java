package com.github.kuzznya.jb.message.controller;

import com.github.kuzznya.jb.message.exception.NotFoundException;
import com.github.kuzznya.jb.message.model.Message;
import com.github.kuzznya.jb.message.model.MessageTemplate;
import com.github.kuzznya.jb.message.model.MessageVariable;
import com.github.kuzznya.jb.message.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/templates")
public class MessageController {

    private final MessageService messageService;

    @PostMapping
    @Operation(summary = "Save new message template", responses = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "409", description = "Template with passed ID already exists")
    })
    public MessageTemplate saveTemplate(@RequestBody MessageTemplate template) {
        return messageService.save(template);
    }

    @GetMapping
    @Operation(summary = "Get all templates")
    public List<MessageTemplate> getAllTemplates() {
        return messageService.getAllTemplates();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get message template by ID", responses = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "404", description = "Template not found")
    })
    public MessageTemplate getTemplate(@PathVariable String id) {
        return messageService.getTemplate(id)
                .orElseThrow(() -> new NotFoundException("Template with id " + id + " not found"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete message template by ID")
    public void deleteTemplate(@PathVariable String id) {
        messageService.deleteTemplate(id);
    }

    @GetMapping("/{id}/message")
    @Operation(summary = "Process message template using passed variables and return resulting message", responses = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "404", description = "Template not found"),
            @ApiResponse(responseCode = "400", description = "Template processing error")
    })
    public Message processMessage(@PathVariable String id, @RequestParam Map<String, String> variables) {
        var templateVars = variables.entrySet()
                .stream()
                .map(entry -> new MessageVariable(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
        return messageService.processMessage(id, templateVars);
    }

    @PostMapping("/{id}/message")
    @Operation(summary = "Process message using passed variables and send to recipients", responses = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "404", description = "Template not found"),
            @ApiResponse(responseCode = "400", description = "Template processing error"),
            @ApiResponse(responseCode = "503", description = "Message sending error")
    })
    public Message sendMessage(@PathVariable String id, @RequestParam Map<String, String> variables) {
        var templateVars = variables.entrySet()
                .stream()
                .map(entry -> new MessageVariable(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
        return messageService.send(id, templateVars);
    }
}
