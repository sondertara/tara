
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
        super(MessageFormatter.arrayFormat(format, arguments).getMessage());
    }

    public TaraException(Throwable cause, String format, Object... arguments) {
        super(MessageFormatter.arrayFormat(format, arguments).getMessage(), cause);
    }

    public TaraException(Throwable cause) {
        super(cause);
    }
}
