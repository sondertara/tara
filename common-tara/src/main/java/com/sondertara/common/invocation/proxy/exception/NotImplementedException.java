package com.sondertara.common.invocation.proxy.exception;

import java.lang.reflect.Method;

/**
 * 方法未实现
 *
 * @author byx
 */
public class NotImplementedException extends ProxyRunException {
    public NotImplementedException(Method method) {
        super("Not implemented method: " + method);
    }
}
