package com.sondertara.common.invocation.proxy.targetdelegate;

import com.sondertara.common.invocation.proxy.SimpleInvocationHandler;
import com.sondertara.common.invocation.proxy.core.TargetMethod;
import com.sondertara.common.invocation.proxy.exception.ProxyRunException;

import java.lang.reflect.InvocationTargetException;

/**
 * Use it, the delegate will be the target
 */
public class DelegateInvocationHandler extends SimpleInvocationHandler {
    private final TargetDelegateProvider delegateProvider;

    public DelegateInvocationHandler(Object target, TargetDelegateProvider delegateProvider) {
        super(target);
        this.delegateProvider = delegateProvider;
    }

    @Override
    public Object intercept(TargetMethod targetMethod) {
        Object delegate = this.delegateProvider.get(targetMethod);
        Object obj = delegate == null ? target : delegate;
        try {
            return targetMethod.getMethod().invoke(obj, targetMethod.getParams());
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new ProxyRunException(e);
        }
    }
}
