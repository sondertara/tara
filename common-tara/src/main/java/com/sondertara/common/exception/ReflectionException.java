package com.sondertara.common.exception;

import org.slf4j.helpers.MessageFormatter;

/**
 * @author huangxiaohu.1ih
 */
public class ReflectionException extends RuntimeException {
    public ReflectionException(String format, Object... arguments) {

        super(MessageFormatter.arrayFormat(format, arguments).getMessage(),
                MessageFormatter.getThrowableCandidate(arguments));
    }

    public ReflectionException(String message, Throwable cause) {
        super(message, cause);
    }

    public ReflectionException(Throwable cause) {
        super(cause);
    }
}
