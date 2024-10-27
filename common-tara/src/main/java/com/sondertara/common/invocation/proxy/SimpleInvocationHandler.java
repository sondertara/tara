package com.sondertara.common.invocation.proxy;

import com.sondertara.common.invocation.proxy.core.Invokable;
import com.sondertara.common.invocation.proxy.core.MethodInterceptor;
import com.sondertara.common.invocation.proxy.core.MethodSignature;
import com.sondertara.common.invocation.proxy.core.TargetMethod;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * ProxyInstance -> invocation handler -> Target
 *
 * @author jinuo.fang
 */
public class SimpleInvocationHandler implements MethodInterceptor, InvocationHandler {
    protected Object target;

    public SimpleInvocationHandler() {
    }

    public SimpleInvocationHandler(Object target) {
        setTarget(target);
    }

    public void setTarget(Object target) {
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return intercept(new TargetMethod(MethodSignature.of(method), Invokable.of(method, target), args));
    }


    @Override
    public Object intercept(TargetMethod targetMethod) {
        return targetMethod.invokeWithOriginalParams();
    }
}
