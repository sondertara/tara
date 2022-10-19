package com.sondertara.excel.exception;

/**
 * 字段值转换异常
 *
 * @author huangxiaohu
 */
public class ExcelConvertException extends ExcelException {


    public ExcelConvertException(String message) {
        super(message);
    }

    public ExcelConvertException(String format, Object... arguments) {
        super(format, arguments);
    }
}
