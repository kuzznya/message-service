package com.github.kuzznya.jb.message.exception;

import com.github.kuzznya.jb.message.model.VariableType;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class IncompatibleVarTypeException extends TemplateProcessingException {
    public IncompatibleVarTypeException(String value, VariableType expected) {
        super("Incompatible variable value type: expected " + expected.name() + ", received value " + value);
    }

    public IncompatibleVarTypeException(String value, VariableType expected, Throwable cause) {
        super("Incompatible variable value type: expected " + expected.name() + ", received value " + value, cause);
    }
}
