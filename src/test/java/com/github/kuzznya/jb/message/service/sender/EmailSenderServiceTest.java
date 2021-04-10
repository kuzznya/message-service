package com.github.kuzznya.jb.message.service.sender;

import com.github.kuzznya.jb.message.config.EmailProperties;
import com.github.kuzznya.jb.message.exception.MessageSendException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = {EmailSenderService.class, EmailProperties.class})
class EmailSenderServiceTest {

    @Autowired
    private EmailSenderService senderService;
    @MockBean
    private JavaMailSender mailSender;

    @Test
    void canSend_WhenValidEmail_ReturnTrue() {
        assertTrue(senderService.canSend(URI.create("mailto:valid@email.com")));
        assertTrue(senderService.canSend(URI.create("mailto:another-valid@complex.email.com")));
        assertTrue(senderService.canSend(URI.create("mailto:some.other.email.number3@gmail.com")));
    }

    @Test
    void canSend_WhenInvalidEmail_ReturnFalse() {
        assertFalse(senderService.canSend(URI.create("mailto:invalid")));
        assertFalse(senderService.canSend(URI.create("mailto:invalid@email@")));
        assertFalse(senderService.canSend(URI.create("mailto:123@123")));
    }

    @Test
    void send_WhenValidEmail_CallSender() {
        Mockito.doNothing().when(mailSender).send((SimpleMailMessage) Mockito.any());

        senderService.send("Test", URI.create("mailto:valid@email.com"));

        Mockito.verify(mailSender, Mockito.times(1))
                .send((SimpleMailMessage) Mockito.any());
    }

    @Test
    void send_WhenInvalidEmail_ThrowException() {
        assertThrows(MessageSendException.class,
                () -> senderService.send("Test", URI.create("mailto:invalid")));
    }
}