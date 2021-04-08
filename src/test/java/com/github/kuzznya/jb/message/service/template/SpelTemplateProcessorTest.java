package com.github.kuzznya.jb.message.service.template;

import com.github.kuzznya.jb.message.exception.TemplateProcessingException;
import com.github.kuzznya.jb.message.model.MessageTemplate;
import com.github.kuzznya.jb.message.model.MessageVariable;
import com.github.kuzznya.jb.message.model.TemplateVariable;
import com.github.kuzznya.jb.message.model.VariableType;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SpelTemplateProcessorTest {

    private final TemplateProcessor resolver = new SpelTemplateProcessor();

    @Test
    void resolve_WhenValidTemplateWithInt_ReturnValidMessage() {
        resolveVar("123", VariableType.INT);
    }

    @Test
    void resolve_WhenValidTemplateWithFloat_ReturnValidMessage() {
        resolveVar("123.456", VariableType.FLOAT);
    }

    @Test
    void resolve_WhenValidTemplateWithString_ReturnValidMessage() {
        resolveVar("some very long value with backspaces", VariableType.STRING);
    }

    @Test
    void resolve_WhenUndefinedVariable_ThrowException() {
        var template = new MessageTemplate(
                "template1",
                "Test $undefinedVar$ test",
                List.of(new TemplateVariable("var1", VariableType.STRING)),
                Collections.emptyList());
        assertThrows(TemplateProcessingException.class, () -> resolver.process(template, Collections.emptyList()));
    }

    @Test
    void resolve_WhenMultipleVariables_ReturnValidMessage() {
        var template = new MessageTemplate(
                "template1",
                "Int value: $intVar$, float value: $floatVar$, string value: $strVar$",
                List.of(new TemplateVariable("intVar", VariableType.INT),
                        new TemplateVariable("floatVar", VariableType.FLOAT),
                        new TemplateVariable("strVar", VariableType.STRING)),
                Collections.emptyList());
        var vars = List.of(
                new MessageVariable("intVar", "1"),
                new MessageVariable("floatVar", "2.5"),
                new MessageVariable("strVar", "Test var"));
        assertEquals("Int value: 1, float value: 2.5, string value: Test var", resolver.process(template, vars));
    }

    @Test
    void resolve_WhenVarWithoutDefinition_ReturnValidMessage() {
        var prefix = "Not templated string with ";
        var postfix = " value";
        var varName = "var1";
        var value = "test value without definition";
        var template = new MessageTemplate(
                "template1",
                prefix + "$" + varName + "$" + postfix,
                Collections.emptyList(),
                List.of());
        String result = resolver.process(template, List.of(new MessageVariable(varName, value)));
        assertEquals(prefix + value + postfix, result);
    }

    private void resolveVar(String value, VariableType type) {
        var prefix = "Not templated string with ";
        var postfix = " value";
        var varName = "var1";
        var template = new MessageTemplate(
                "template1",
                prefix + "$" + varName + "$" + postfix,
                List.of(new TemplateVariable("var1", type)),
                List.of());
        String result = resolver.process(template, List.of(new MessageVariable(varName, value)));
        assertEquals(prefix + value + postfix, result);
    }
}