
package com.sondertara.common.bean.exception;

/**
 * Bean early check error
 *
 * @author huangxiaohu
 */
public class BeanAnalysisException extends RuntimeException {
    public BeanAnalysisException(String message) {
        super(message);
    }

    public BeanAnalysisException(Throwable cause) {
        super(cause);
    }
}
