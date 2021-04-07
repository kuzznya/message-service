package com.github.kuzznya.jb.message.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class TemplateProcessingException extends RuntimeException {
    public TemplateProcessingException() {
        super("Template processing error");
    }

    public TemplateProcessingException(String message) {
        super(message);
    }

    public TemplateProcessingException(String message, Throwable cause) {
        super(message, cause);
    }

    public TemplateProcessingException(Throwable cause) {
        super("Template processing error", cause);
    }
}
