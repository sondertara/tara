package com.sondertara.common.invocation.proxy.aop.exception;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * 不合法的增强方法签名
 *
 * @author byx
 */
public class IllegalMethodSignatureException extends AopRunException {
    public <T extends Annotation> IllegalMethodSignatureException(Method method, Class<T> annotationClass) {
        super(String.format("Illegal method signature with @%s annotation: %s",
                annotationClass.getSimpleName(), method));
    }
}
