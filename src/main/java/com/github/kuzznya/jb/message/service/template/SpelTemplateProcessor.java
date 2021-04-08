package com.github.kuzznya.jb.message.service.template;

import com.github.kuzznya.jb.message.exception.TemplateProcessingException;
import com.github.kuzznya.jb.message.model.MessageTemplate;
import com.github.kuzznya.jb.message.model.MessageVariable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.ParserContext;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.SimpleEvaluationContext;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SpelTemplateProcessor implements TemplateProcessor {

    private final ExpressionParser expressionParser;
    private final ParserContext parserContext;

    private static final String DOLLAR_REPLACEMENT = "\uFFFDS";

    public SpelTemplateProcessor() {
        expressionParser = new SpelExpressionParser();
        parserContext = new TemplateParserContext("$", "$");
    }

    @Override
    public String process(MessageTemplate template, List<MessageVariable> variables) {
        Map<String, String> variablesMap = variables.stream()
                .collect(Collectors.toMap(MessageVariable::getKey, MessageVariable::getValue));
        EvaluationContext evaluationContext = SimpleEvaluationContext
                .forPropertyAccessors(new VariablePropertyAccessor())
                .withRootObject(VariablePropertyAccessor.ROOT_OBJECT)
                .build();

        template.getVariables().forEach(templateVar -> {
                    var value = variablesMap.get(templateVar.getKey());
                    if (value == null)
                        throw new TemplateProcessingException("Template requires variable " + templateVar.getKey() + " that is not defined");
                    evaluationContext.setVariable(templateVar.getKey(), templateVar.getType().parseValue(value));
                });
        // Add variables without definitions as string variables
        variables.stream()
                .filter(var -> evaluationContext.lookupVariable(var.getKey()) == null)
                .forEach(var -> evaluationContext.setVariable(var.getKey(), var.getValue()));

        String templateString = template.getTemplate().replace("\\$", DOLLAR_REPLACEMENT);

        try {
            String result = expressionParser.parseExpression(templateString, parserContext)
                    .getValue(evaluationContext, String.class);
            if (result == null)
                throw new NullPointerException("Template processing returned null value");
            return result.replace(DOLLAR_REPLACEMENT, "$");
        } catch (Exception e) {
            log.error("Cannot process template", e);
            throw new TemplateProcessingException(e);
        }
    }
}
