package com.github.kuzznya.jb.message.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VariableValueEntity {
    @Id
    @GeneratedValue
    private UUID id;
    @ManyToOne(optional = false)
    private VariableDefinitionEntity definition;
    private String value;
}
