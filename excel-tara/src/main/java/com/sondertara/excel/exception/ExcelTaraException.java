package com.sondertara.excel.exception;

import org.slf4j.helpers.MessageFormatter;

/**
 * @author huangxiaohu
 */
public class ExcelTaraException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public ExcelTaraException(String msg) {
        super(msg);
    }

    public ExcelTaraException(String format, Object... arguments) {
        super(MessageFormatter.arrayFormat(format, arguments).getMessage());
    }

    public ExcelTaraException(Throwable cause, String format, Object... arguments) {
        super(MessageFormatter.arrayFormat(format, arguments).getMessage(), cause);
    }

    public ExcelTaraException(Throwable cause) {
        super(cause);
    }
}
