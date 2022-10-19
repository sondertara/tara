package com.sondertara.excel.exception;

/**
 * 值校验异常
 *
 * @author huangxiaohu
 */
public class ExcelValidationException extends ExcelException {
    private static final long serialVersionUID = 1888228315406130225L;


    public ExcelValidationException(String format, Object... arguments) {
        super(format, arguments);
    }

}
