package com.sondertara.common.invocation.proxy.core;

/**
 * 异常拦截器
 *
 * @author byx
 */
public interface ExceptionInterceptor {
    Object intercept(Throwable e);
}
