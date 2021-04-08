package com.github.kuzznya.jb.message.controller;

import com.github.kuzznya.jb.message.exception.NotFoundException;
import com.github.kuzznya.jb.message.model.Message;
import com.github.kuzznya.jb.message.model.MessageTemplate;
import com.github.kuzznya.jb.message.model.MessageVariable;
import com.github.kuzznya.jb.message.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class MessageController {

    private final MessageService messageService;

    @PostMapping("/templates")
    public MessageTemplate saveTemplate(@RequestBody MessageTemplate template) {
        return messageService.save(template);
    }

    @GetMapping("/templates/{id}")
    public MessageTemplate getTemplate(@PathVariable String id) {
        return messageService.getTemplate(id)
                .orElseThrow(() -> new NotFoundException("Template with id " + id + " not found"));
    }

    @DeleteMapping("/templates/{id}")
    public void deleteTemplate(@PathVariable String id) {
        messageService.deleteTemplate(id);
    }

    @GetMapping("/templates/{id}/message")
    public Message processMessage(@PathVariable String id, @RequestParam Map<String, String> variables) {
        var templateVars = variables.entrySet()
                .stream()
                .map(entry -> new MessageVariable(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
        return messageService.processMessage(id, templateVars);
    }

    @PostMapping("/templates/{id}/message")
    public Message sendMessage(@PathVariable String id, @RequestParam Map<String, String> variables) {
        var templateVars = variables.entrySet()
                .stream()
                .map(entry -> new MessageVariable(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
        return messageService.send(id, templateVars);
    }
}
