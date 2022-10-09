package com.sondertara.excel.exception;

/**
 * 值校验异常
 *
 * @author huangxiaohu
 */
public class ExcelValidationException extends ExcelException {
    private static final long serialVersionUID = 1888228315406130225L;

    public ExcelValidationException() {
        super();
    }

    public ExcelValidationException(final String message) {
        super(message);
    }

    public ExcelValidationException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public ExcelValidationException(final Throwable cause) {
        super(cause);
    }

    protected ExcelValidationException(final String message, final Throwable cause, final boolean enableSuppression,
            final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
