package com.sondertara.common.invocation.proxy.sparse;

import com.sondertara.common.invocation.proxy.core.Invokable;
import com.sondertara.common.invocation.proxy.core.MethodInterceptor;
import com.sondertara.common.invocation.proxy.core.MethodSignature;
import com.sondertara.common.invocation.proxy.core.TargetMethod;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Objects;

public class SparseMethodInvocationHandler implements InvocationHandler {

    @NonNull
    private Object target;

    @Nullable
    private MethodInterceptor interceptor;

    public SparseMethodInvocationHandler(Object target) {
        Objects.requireNonNull(target, "the target is null");
        this.target = target;
    }

    public void setInterceptor(MethodInterceptor interceptor) {
        this.interceptor = interceptor;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (this.interceptor == null) {
            return method.invoke(target, args);
        }
        return this.interceptor.intercept(new TargetMethod(MethodSignature.of(method), Invokable.of(method,target), args));
    }
}
