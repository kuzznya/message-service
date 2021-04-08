package com.github.kuzznya.jb.message.service.sender;

import java.net.URI;

public interface SenderService {
    void send(String message, URI recipient);
}
