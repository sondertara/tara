package com.sondertara.common.exception;

import org.slf4j.helpers.MessageFormatter;

/**
 * @author huangxiaohu
 */
public class TaraException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public TaraException(String msg) {
        super(msg);
    }

    public TaraException(String format, Object... arguments) {

        super(MessageFormatter.arrayFormat(format, arguments).getMessage(), MessageFormatter.getThrowableCandidate(arguments));
    }

    public TaraException(String msg, Throwable e) {
        super(msg, e);
    }

    public TaraException(Throwable cause) {
        super(cause);
    }

}
