package com.sondertara.common.reflect.parameter;

import com.sondertara.common.annotation.Name;

import java.lang.reflect.Method;

@Name("langx_java6")
public class Java6MethodParameterSupplier extends AbstractMethodParameterSupplier {
    @Override
    public MethodParameter apply(ParameterMeta meta) {
        init();
        return new Java6MethodParameter(meta.getName(), meta.getModifiers(), (Method) meta.getExecutable(), meta.getIndex());
    }

    @Override
    public boolean usingJdkApi() {
        return false;
    }
}
