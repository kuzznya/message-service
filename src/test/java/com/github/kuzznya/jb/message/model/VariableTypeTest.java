package com.github.kuzznya.jb.message.model;

import com.github.kuzznya.jb.message.exception.IncompatibleVarTypeException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VariableTypeTest {

    @Test
    void StringParseValue_WhenAnyString_ReturnEqualString() {
        assertEquals("Test", VariableType.STRING.parseValue("Test"));
        assertEquals("Value with spaces", VariableType.STRING.parseValue("Value with spaces"));
        assertEquals("123", VariableType.STRING.parseValue("123"));
    }

    @Test
    void IntParseValue_WhenInt_ReturnParsedInt() {
        assertEquals(123, VariableType.INT.parseValue("123"));
        assertEquals(-12, VariableType.INT.parseValue("-12"));
    }

    @Test
    void IntParseValue_WhenNotInt_ThrowIncompatibleVarTypeException() {
        assertThrows(IncompatibleVarTypeException.class, () -> VariableType.INT.parseValue("str"));
        assertThrows(IncompatibleVarTypeException.class, () -> VariableType.INT.parseValue(""));
        assertThrows(IncompatibleVarTypeException.class, () -> VariableType.INT.parseValue("1.5"));
        assertThrows(IncompatibleVarTypeException.class, () -> VariableType.INT.parseValue(null));
    }

    @Test
    void FloatParseValue_WhenFloat_ReturnParsedFloat() {
        assertEquals(123.333, VariableType.FLOAT.parseValue("123.333"));
        assertEquals(-12.13, VariableType.FLOAT.parseValue("-12.13"));
        assertEquals(5.0, VariableType.FLOAT.parseValue("5"));
    }

    @Test
    void FloatParseValue_WhenNotFloat_ThrowIncompatibleVarTypeException() {
        assertThrows(IncompatibleVarTypeException.class, () -> VariableType.FLOAT.parseValue("str"));
        assertThrows(IncompatibleVarTypeException.class, () -> VariableType.FLOAT.parseValue(""));
        assertThrows(IncompatibleVarTypeException.class, () -> VariableType.FLOAT.parseValue(null));
    }
}