package com.sondertara.common.invocation.proxy.exception;

import java.lang.reflect.Method;

/**
 * 目标方法产生的异常
 *
 * @author byx
 */
public class TargetMethodException extends ProxyRunException {
    private final Throwable targetException;

    public TargetMethodException(Throwable e, Method method) {
        super("Exception from target method: " + method, e);
        this.targetException = e;
    }

    public Throwable getTargetException() {
        return targetException;
    }
}
