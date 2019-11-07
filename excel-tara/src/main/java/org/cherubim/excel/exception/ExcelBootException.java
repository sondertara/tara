
package org.cherubim.excel.exception;

import org.slf4j.helpers.MessageFormatter;

/**
 * @author huangxiaohu
 */
public class ExcelBootException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public ExcelBootException(String msg) {
        super(msg);
    }

    public ExcelBootException(String format, Object... arguments) {
        super(MessageFormatter.arrayFormat(format, arguments).getMessage());
    }

    public ExcelBootException(Throwable cause, String format, Object... arguments) {
        super(MessageFormatter.arrayFormat(format, arguments).getMessage(), cause);
    }

    public ExcelBootException(Throwable cause) {
        super(cause);
    }
}
