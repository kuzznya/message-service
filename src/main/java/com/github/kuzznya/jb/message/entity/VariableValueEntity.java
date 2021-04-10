package com.github.kuzznya.jb.message.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VariableValueEntity {
    @Id
    @GeneratedValue
    private UUID id;
    private String key;
    private String value;
}
