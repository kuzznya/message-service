package com.github.kuzznya.jb.message.service.sender;

import com.github.kuzznya.jb.message.exception.MessageSendException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = {DelegatingSenderService.class})
class DelegatingSenderServiceTest {

    @Autowired
    private DelegatingSenderService delegatingSenderService;

    @MockBean
    private EmailSenderService emailSenderService;
    @MockBean
    private WebSenderService webSenderService;

    @Test
    void canSend_WhenOneSenderCanSend_ReturnTrue() {
        Mockito.when(emailSenderService.canSend(Mockito.any()))
                .thenReturn(true);
        Mockito.when(webSenderService.canSend(Mockito.any()))
                .thenReturn(false);

        assertTrue(delegatingSenderService.canSend(URI.create("mailto:test@test.com")));

        Mockito.when(emailSenderService.canSend(Mockito.any()))
                .thenReturn(false);
        Mockito.when(webSenderService.canSend(Mockito.any()))
                .thenReturn(true);

        assertTrue(delegatingSenderService.canSend(URI.create("https://test.com")));
    }

    @Test
    void canSend_WhenAnySenderCanSend_ReturnTrue() {
        Mockito.when(emailSenderService.canSend(Mockito.any()))
                .thenReturn(true);
        Mockito.when(webSenderService.canSend(Mockito.any()))
                .thenReturn(true);
        assertTrue(delegatingSenderService.canSend(URI.create("https://test.com")));
    }

    @Test
    void canSend_WhenNoSenderCanSend_ReturnFalse() {
        Mockito.when(emailSenderService.canSend(Mockito.any()))
                .thenReturn(false);
        Mockito.when(webSenderService.canSend(Mockito.any()))
                .thenReturn(false);
        assertFalse(delegatingSenderService.canSend(URI.create("https://test.com")));
    }

    @Test
    void send_WhenOneSenderCanSend_CallSender() {
        Mockito.when(emailSenderService.canSend(Mockito.any()))
                .thenReturn(true);
        Mockito.when(webSenderService.canSend(Mockito.any()))
                .thenReturn(false);

        delegatingSenderService.send("test", URI.create("mailto:test@test.com"));

        Mockito.verify(emailSenderService, Mockito.times(1))
                .send(Mockito.any(), Mockito.any());
    }

    @Test
    void send_WhenNoSenderCanSend_ThrowMessageSendException() {
        Mockito.when(emailSenderService.canSend(Mockito.any()))
                .thenReturn(false);
        Mockito.when(webSenderService.canSend(Mockito.any()))
                .thenReturn(false);

        assertThrows(MessageSendException.class,
                () -> delegatingSenderService.send("test", URI.create("https://test.com")));
    }
}