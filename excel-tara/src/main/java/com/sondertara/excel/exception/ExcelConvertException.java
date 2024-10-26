package com.sondertara.excel.exception;

import org.slf4j.helpers.MessageFormatter;

/**
 * 字段值转换异常
 *
 * @author huangxiaohu
 */
public class ExcelConvertException extends Exception {


    public ExcelConvertException(String message) {
        super(message);
    }

    public ExcelConvertException(String format, Object... arguments) {
        super(MessageFormatter.arrayFormat(format, arguments).getMessage(),
                MessageFormatter.getThrowableCandidate(arguments));
    }
}
