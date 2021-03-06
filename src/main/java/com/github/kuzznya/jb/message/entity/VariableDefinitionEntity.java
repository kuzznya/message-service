package com.github.kuzznya.jb.message.entity;

import com.github.kuzznya.jb.message.model.VariableType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VariableDefinitionEntity {
    @Id
    @GeneratedValue
    private UUID id;
    private String key;
    @Enumerated(EnumType.STRING)
    private VariableType type;
}
