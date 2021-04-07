package com.github.kuzznya.jb.message.exception;

public class MessageSendException extends RuntimeException {
    public MessageSendException() {
        super("Message sending error");
    }

    public MessageSendException(String message) {
        super(message);
    }

    public MessageSendException(String message, Throwable cause) {
        super(message, cause);
    }

    public MessageSendException(Throwable cause) {
        super("Message sending error", cause);
    }
}
