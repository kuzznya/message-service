package com.github.kuzznya.jb.message.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

@Value
public class TemplateVariable {
    String key;
    VariableType type;

    @JsonCreator
    public TemplateVariable(@JsonProperty(value = "key", required = true) String key,
                            @JsonProperty(value = "type", defaultValue = "STRING") VariableType type) {
        this.key = key;
        this.type = type;
    }
}
