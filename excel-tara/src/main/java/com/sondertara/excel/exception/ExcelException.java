package com.sondertara.excel.exception;

import com.sondertara.common.exception.TaraException;

/**
 * @author huangxiaohu
 */
public class ExcelException extends TaraException {

    public ExcelException(String format, Object... arguments) {
        super(format, arguments);
    }

    public ExcelException(Throwable cause) {
        super(cause);
    }
}
