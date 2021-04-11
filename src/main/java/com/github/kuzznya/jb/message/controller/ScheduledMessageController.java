package com.github.kuzznya.jb.message.controller;

import com.github.kuzznya.jb.message.model.ScheduledMessage;
import com.github.kuzznya.jb.message.service.ScheduledMessageSender;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/messages/scheduled")
public class ScheduledMessageController {

    private final ScheduledMessageSender messageSender;

    @PostMapping
    @Operation(summary = "Schedule message", responses = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "404", description = "Template not found"),
            @ApiResponse(responseCode = "400", description = "Template processing error or invalid interval format")
    })
    public ScheduledMessage scheduleMessage(@RequestBody ScheduledMessage message) {
        return messageSender.register(message);
    }

    @GetMapping
    @Operation(summary = "Get all scheduled messages")
    public List<ScheduledMessage> getScheduledMessages() {
        return messageSender.getScheduledMessages();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Cancel scheduled message")
    public void removeScheduledMessage(@PathVariable UUID id) {
        messageSender.cancel(id);
    }
}
