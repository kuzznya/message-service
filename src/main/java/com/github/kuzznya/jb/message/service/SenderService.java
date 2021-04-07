package com.github.kuzznya.jb.message.service;

import java.net.URI;

public interface SenderService {
    void send(String message, URI recipient);
}
