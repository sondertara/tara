
package com.sondertara.common.bean.exception;

/**
 * Bean copy when cause error
 *
 * @author huangxiaohu
 */
public class BeanCopyException extends RuntimeException {
    public BeanCopyException(Throwable cause) {
        super(cause);
    }

    public BeanCopyException(String message) {
        super(message);
    }

    public BeanCopyException(String message, Throwable cause) {
        super(message, cause);
    }
}
