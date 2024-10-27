package com.sondertara.common.invocation.proxy.exception;

import com.sondertara.common.exception.TaraException;

/**
 * ProxyUtils异常基类
 *
 * @author byx
 */
public class ProxyRunException extends TaraException {
    public ProxyRunException(String msg) {
        super(msg);
    }

    public ProxyRunException(Throwable e) {
        super(e);
    }


    public ProxyRunException(String format, Object... arguments) {
        super(format, arguments);
    }

    public ProxyRunException(String msg, Throwable e) {
        super(msg, e);
    }
}
