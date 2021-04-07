package com.github.kuzznya.jb.message.service.spel;

import com.github.kuzznya.jb.message.exception.TemplateProcessingException;
import com.github.kuzznya.jb.message.model.MessageTemplate;
import com.github.kuzznya.jb.message.model.MessageVariable;
import com.github.kuzznya.jb.message.service.TemplateResolver;
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
public class SpelTemplateResolver implements TemplateResolver {

    private final ExpressionParser expressionParser;
    private final ParserContext parserContext;

    public SpelTemplateResolver() {
        expressionParser = new SpelExpressionParser();
        parserContext = new TemplateParserContext("$", "$");
    }

    @Override
    public String resolve(MessageTemplate template, List<MessageVariable> variables) {
        Map<String, String> variablesMap = variables.stream()
                .collect(Collectors.toMap(MessageVariable::getKey, MessageVariable::getValue));
        EvaluationContext evaluationContext = SimpleEvaluationContext
                .forPropertyAccessors(new VariablePropertyAccessor())
                .withRootObject(VariablePropertyAccessor.rootObject)
                .build();
        template.getVariables().forEach(templateVar -> {
                    var value = variablesMap.get(templateVar.getKey());
                    if (value == null)
                        throw new TemplateProcessingException("Template requires variable " + templateVar.getKey() + " that is not defined");
                    evaluationContext.setVariable(templateVar.getKey(), templateVar.getType().parseValue(value));
                });
        try {
            return expressionParser.parseExpression(template.getTemplate(), parserContext)
                    .getValue(evaluationContext, String.class);
        } catch (Exception e) {
            throw new TemplateProcessingException(e);
        }
    }
}
