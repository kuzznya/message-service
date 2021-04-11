package com.github.kuzznya.jb.message.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
public class MessageSendException extends RuntimeException {

    public MessageSendException(String message) {
        super(message);
    }

    public MessageSendException(Throwable cause) {
        super("Message sending error", cause);
    }
}
