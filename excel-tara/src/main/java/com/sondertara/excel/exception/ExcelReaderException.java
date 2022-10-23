package com.sondertara.excel.exception;

/**
 * Excel Reader Exception
 *
 * @author huangxiaohu
 */
public class ExcelReaderException extends ExcelException {


    public ExcelReaderException(String message) {
        super(message);
    }

    public ExcelReaderException(String message, Object... args) {
        super(message, args);
    }

    public ExcelReaderException(Throwable cause) {
        super(cause);
    }

}
