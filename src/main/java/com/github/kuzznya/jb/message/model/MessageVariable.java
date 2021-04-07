package com.github.kuzznya.jb.message.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

@Value
public class MessageVariable {
    String key;
    String value;

    @JsonCreator
    public MessageVariable(@JsonProperty(value = "key", required = true) String key,
                           @JsonProperty(value = "value", required = true) String value) {
        this.key = key;
        this.value = value;
    }
}
