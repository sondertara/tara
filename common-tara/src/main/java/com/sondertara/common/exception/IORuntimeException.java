package com.sondertara.common.exception;

import org.slf4j.helpers.MessageFormatter;

/**
 * @author huangxiaohu
 */
public class IORuntimeException extends RuntimeException {

    public IORuntimeException(String format, Object... arguments) {

        super(MessageFormatter.arrayFormat(format, arguments).getMessage(),
                MessageFormatter.getThrowableCandidate(arguments));
    }

    public IORuntimeException(Throwable throwable) {
        super(throwable);
    }
}
