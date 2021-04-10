package com.github.kuzznya.jb.message.controller;

import com.github.kuzznya.jb.message.model.ScheduledMessage;
import com.github.kuzznya.jb.message.service.ScheduledMessageSender;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class ScheduledMessageController {

    private final ScheduledMessageSender messageSender;

    @PostMapping("/messages/scheduled")
    public ScheduledMessage scheduleMessage(@RequestBody ScheduledMessage message) {
        return messageSender.register(message);
    }

    @DeleteMapping("/messages/scheduled/{id}")
    public void removeScheduledMessage(@PathVariable UUID id) {
        messageSender.delete(id);
    }
}
