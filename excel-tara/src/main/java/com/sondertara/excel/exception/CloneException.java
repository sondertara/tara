package com.sondertara.excel.exception;

/**
 * @author huangxiaohu
 */
public class CloneException extends RuntimeException {
    public CloneException() {
    }

    public CloneException(String message) {
        super(message);
    }

    public CloneException(String message, Throwable cause) {
        super(message, cause);
    }

    public CloneException(Throwable cause) {
        super(cause);
    }
}
