package com.sondertara.common.invocation.proxy.aop.exception;

/**
 * AOP异常基类
 *
 * @author byx
 */
public class AopRunException extends RuntimeException {
    public AopRunException(String msg) {
        super(msg);
    }

    public AopRunException(String msg, Exception e) {
        super(msg, e);
    }
}
