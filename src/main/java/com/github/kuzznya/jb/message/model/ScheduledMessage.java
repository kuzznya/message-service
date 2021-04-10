package com.github.kuzznya.jb.message.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Value
public class ScheduledMessage {
    UUID id;
    String templateId;
    List<MessageVariable> variables;
    String interval;

    @JsonCreator
    public ScheduledMessage(@JsonProperty("id") UUID id,
                            @JsonProperty(value = "templateId", required = true) String templateId,
                            @JsonProperty(value = "variables") List<MessageVariable> variables,
                            @JsonProperty(value =  "interval", required = true)
                                @JsonAlias("sendInterval") String interval) {
        this.id = id;
        this.templateId = templateId;
        this.variables = variables != null ? variables : Collections.emptyList();
        this.interval = interval;
    }
}
