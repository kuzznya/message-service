package com.github.kuzznya.jb.message.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class IllegalFormatException extends RuntimeException {

    public IllegalFormatException(String message, Throwable cause) {
        super(message, cause);
    }

}
