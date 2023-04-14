package com.sondertara.common.exception;

import com.sondertara.common.util.StringUtils;

/**
 * 验证异常
 *
 * @author xiaoleilu
 */
public class ValidateException extends TaraException {
    private static final long serialVersionUID = 6057602589533840889L;


    public ValidateException(String msg) {
        super(msg);
    }

    public ValidateException(String messageTemplate, Object... params) {
        super(StringUtils.format(messageTemplate, params));
    }

    public ValidateException(Throwable throwable) {
        super(throwable);
    }

    public ValidateException(String msg, Throwable throwable) {
        super(msg, throwable);
    }

    public ValidateException(int status, String msg) {
        super(status, msg);
    }


    public ValidateException(String message, Throwable throwable, boolean enableSuppression, boolean writableStackTrace) {
        super(message, throwable, enableSuppression, writableStackTrace);
    }

    public ValidateException(int status, String msg, Throwable throwable) {
        super(status, msg, throwable);
    }
}
