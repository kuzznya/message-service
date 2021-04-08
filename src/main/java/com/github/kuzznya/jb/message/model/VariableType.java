package com.github.kuzznya.jb.message.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.kuzznya.jb.message.exception.IncompatibleVarTypeException;

public enum VariableType {
    @JsonProperty("string")
    STRING {
        @Override
        public Object parseValue(String value) {
            return value;
        }
    },

    @JsonProperty("int")
    INT {
        @Override
        public Object parseValue(String value) {
            try {
                return Integer.parseInt(value);
            } catch (Exception e) {
                throw new IncompatibleVarTypeException(value, this, e);
            }
        }
    },

    @JsonProperty("float")
    FLOAT {
        @Override
        public Object parseValue(String value) {
            try {
                return Double.parseDouble(value);
            } catch (Exception e) {
                throw new IncompatibleVarTypeException(value, this, e);
            }
        }
    };

    public abstract Object parseValue(String value);
}
