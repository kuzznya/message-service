package com.github.kuzznya.jb.message.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

import java.net.URI;
import java.util.Collections;
import java.util.List;

@Value
public class MessageTemplate {
    @JsonProperty("templateId")
    String id;
    String template;
    List<TemplateVariable> variables;
    List<URI> recipients;

    @JsonCreator
    public MessageTemplate(@JsonProperty(value = "templateId", required = true) String id,
                           @JsonProperty(value = "template", required = true) String template,
                           @JsonProperty(value = "variables") List<TemplateVariable> variables,
                           @JsonProperty(value = "recipients", required = true) List<URI> recipients) {
        this.id = id;
        this.template = template;
        this.variables = variables != null ? variables : Collections.emptyList();
        this.recipients = recipients;
    }
}
