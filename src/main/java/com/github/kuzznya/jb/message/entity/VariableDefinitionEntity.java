package com.github.kuzznya.jb.message.entity;

import com.github.kuzznya.jb.message.model.VariableType;
import lombok.Data;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Data
public class VariableDefinitionEntity {
    @Id
    @GeneratedValue
    private UUID id;
    private String key;
    @Enumerated(EnumType.STRING)
    private VariableType type;
    @ManyToOne
    private MessageTemplateEntity template;
}
