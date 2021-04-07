package com.github.kuzznya.jb.message.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.UUID;

@Entity
@Data
public class VariableValueEntity {
    @Id
    @GeneratedValue
    private UUID id;
    @ManyToOne(optional = false)
    private VariableDefinitionEntity definition;
    private String value;
}
