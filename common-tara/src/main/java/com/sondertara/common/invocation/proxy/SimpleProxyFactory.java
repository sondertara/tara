package com.sondertara.common.invocation.proxy;

import java.lang.reflect.InvocationHandler;

public class SimpleProxyFactory implements ProxyFactory<Object, Object> {
    @Override
    public Object get(Object target) {
        return newSimpleProxy(target);
    }

    public static Object newSimpleProxy(Object object) {
        InvocationHandler invocationHandler = new SimpleInvocationHandler(object);
        return ProxyUtils.newProxyInstance(object.getClass().getClassLoader(), invocationHandler, object.getClass().getInterfaces());
    }

    public static Object newSimpleProxy(Class clazz) {
        try {
            InvocationHandler invocationHandler = new SimpleInvocationHandler();
            return ProxyUtils.newProxyInstance(clazz.getClassLoader(), invocationHandler, clazz.getInterfaces());
        } catch (Throwable ex) {
            return new RuntimeException(ex);
        }
    }

}
