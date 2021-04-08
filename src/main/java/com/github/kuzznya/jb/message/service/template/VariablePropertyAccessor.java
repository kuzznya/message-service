package com.github.kuzznya.jb.message.service.template;

import org.springframework.expression.AccessException;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.PropertyAccessor;
import org.springframework.expression.TypedValue;

public class VariablePropertyAccessor implements PropertyAccessor {

    static final Object ROOT_OBJECT = new Object();

    @Override
    public Class<?>[] getSpecificTargetClasses() {
        return new Class[] {Object.class};
    }

    @Override
    public boolean canRead(EvaluationContext context, Object target, String name) {
        return target == ROOT_OBJECT && context.lookupVariable(name) != null;
    }

    @Override
    public TypedValue read(EvaluationContext context, Object target, String name) throws AccessException {
        if (target != ROOT_OBJECT)
            throw new AccessException("Target objects are not allowed");
        var value = context.lookupVariable(name);
        if (value == null)
            throw new AccessException("Variable " + name + " is not defined");
        return new TypedValue(value);
    }

    @Override
    public boolean canWrite(EvaluationContext context, Object target, String name) {
        return false;
    }

    @Override
    public void write(EvaluationContext context, Object target, String name, Object newValue) throws AccessException {
        throw new AccessException("Context is read-only");
    }
}
