package com.sondertara.common.exception;

import org.slf4j.helpers.MessageFormatter;

/**
 * @author huangxiaohu
 */
public class TaraException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private final int code;

    public TaraException(String format, Object... arguments) {

        super(MessageFormatter.arrayFormat(format, arguments).getMessage(),
                MessageFormatter.getThrowableCandidate(arguments));
        this.code = 9999;
    }

    public TaraException(int code, String format, Object... arguments) {

        super(MessageFormatter.arrayFormat(format, arguments).getMessage(),
                MessageFormatter.getThrowableCandidate(arguments));
        this.code = code;
    }

    public TaraException(String msg, Throwable e) {
        super(msg, e);
        this.code = 9999;
    }

    public TaraException(Throwable cause) {
        super(cause);
        this.code = 9999;
    }

    public String getCode() {
        return String.format("TE%010d\n", code);
    }

}
