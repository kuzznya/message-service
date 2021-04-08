package com.github.kuzznya.jb.message.entity;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.net.URI;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageTemplateEntity {
    @Id
    @JsonAlias("templateId")
    private String id;
    private String template;
    @ElementCollection
    private List<URI> recipients;
    @OneToMany(mappedBy = "template", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<VariableDefinitionEntity> variables;
    @OneToMany(mappedBy = "template", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<MessageEntity> messages;

    public void addVariableDefinition(VariableDefinitionEntity definition) {
        variables.add(definition);
        definition.setTemplate(this);
    }
}
