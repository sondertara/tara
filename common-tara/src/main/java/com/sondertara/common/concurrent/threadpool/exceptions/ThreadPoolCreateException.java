package com.sondertara.common.concurrent.threadpool.exceptions;

import com.sondertara.common.exception.TaraException;

public class ThreadPoolCreateException extends TaraException {
    public ThreadPoolCreateException(String format, Object... arguments) {
        super(format, arguments);
    }
}
