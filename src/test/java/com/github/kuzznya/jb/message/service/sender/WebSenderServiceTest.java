package com.github.kuzznya.jb.message.service.sender;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(
        classes = WebSenderService.class)
class WebSenderServiceTest {

    @Autowired
    private WebSenderService webSenderService;
    @MockBean
    private RestTemplate restTemplate;

    @Test
    void canSend_WhenValidUri_ReturnTrue() {
        assertTrue(webSenderService.canSend(URI.create("https://test.com")));
        assertTrue(webSenderService.canSend(URI.create("https://test.test2.com/complex")));
        assertTrue(webSenderService.canSend(URI.create("http://not-secured.com")));
    }

    @Test
    void canSend_WhenInvalidUri_ReturnFalse() {
        assertFalse(webSenderService.canSend(URI.create("tcp://test")));
        assertFalse(webSenderService.canSend(URI.create("mailto:invalid")));
    }

    @Test
    void send_WhenValidUri_ExecutePostRequest() {
        Mockito.when(restTemplate.postForObject(Mockito.any(), Mockito.any(), Mockito.eq(String.class)))
                .thenReturn("Test message");
        Mockito.when(restTemplate.postForEntity(Mockito.any(), Mockito.any(), Mockito.eq(String.class)))
                .thenReturn(new ResponseEntity<>("Test value", HttpStatus.OK));

        webSenderService.send("Test message", URI.create("https://test.com"));
    }
}