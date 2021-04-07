package com.github.kuzznya.jb.message.service;

import com.github.kuzznya.jb.message.exception.MessageSendException;
import com.github.kuzznya.jb.message.model.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebSenderService implements SenderService {

    private final RestTemplate restTemplate;

    @Override
    public void send(String message, URI recipient) {
        try {
            String response = restTemplate.postForObject(recipient, new Message(message), String.class);
            log.debug("Recipient {} response is {}", recipient, response);
        } catch (Exception e) {
            throw new MessageSendException(e);
        }
    }
}
