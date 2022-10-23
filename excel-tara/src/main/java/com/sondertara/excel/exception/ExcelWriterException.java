package com.sondertara.excel.exception;

/**
 * @author huangxiaohu
 */
public class ExcelWriterException extends ExcelException{
    public ExcelWriterException(String format, Object... arguments) {
        super(format, arguments);
    }

    public ExcelWriterException(Throwable cause) {
        super(cause);
    }
}
