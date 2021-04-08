package com.github.kuzznya.jb.message.service.sender;

import com.github.kuzznya.jb.message.exception.MessageSendException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.List;

@Service
@Primary
@RequiredArgsConstructor
public class DelegatingSenderService implements SenderService {

    private final List<SenderService> senderServices;

    @Override
    public boolean canSend(URI recipient) {
        return senderServices.stream().anyMatch(service -> service.canSend(recipient));
    }

    @Override
    public void send(String message, URI recipient) {
        senderServices.stream()
                .filter(service -> service.canSend(recipient))
                .findFirst()
                .orElseThrow(() -> new MessageSendException("Cannot send message to recipient " + recipient + ": no available senders"))
                .send(message, recipient);
    }
}
