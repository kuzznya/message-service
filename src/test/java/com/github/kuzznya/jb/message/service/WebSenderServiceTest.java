package com.github.kuzznya.jb.message.service;

import com.github.kuzznya.jb.message.config.WebConfiguration;
import com.github.kuzznya.jb.message.service.sender.WebSenderService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.client.RestTemplateAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(
        classes = {WebConfiguration.class, RestTemplateAutoConfiguration.class, WebSenderService.class},
        properties = "logging.level.com.github.kuzznya=DEBUG")
@Disabled("Performs actual web request, requires internet connection")
class WebSenderServiceTest {

    @Autowired
    private WebSenderService webSenderService;

    @Test
    void send_WhenEchoServer_SendWithoutError() {
        assertDoesNotThrow(() -> webSenderService.send("test", URI.create("https://httpbin.org/anything")));
    }
}