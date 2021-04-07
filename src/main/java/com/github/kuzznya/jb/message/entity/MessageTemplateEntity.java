package com.github.kuzznya.jb.message.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
public class MessageTemplateEntity {
    @Id
    @GeneratedValue
    private UUID id;
    private String template;
    @ElementCollection
    private List<String> recipients;
    @OneToMany(mappedBy = "template", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<VariableDefinitionEntity> variables;
    @OneToMany(mappedBy = "template", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<MessageEntity> messages;

    public void addVariableDefinition(VariableDefinitionEntity definition) {
        variables.add(definition);
        definition.setTemplate(this);
    }
}
