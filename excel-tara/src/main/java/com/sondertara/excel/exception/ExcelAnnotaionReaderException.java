package com.sondertara.excel.exception;

import com.sondertara.common.util.StringFormatter;
import org.apache.commons.lang3.exception.ExceptionUtils;

public class ExcelAnnotaionReaderException extends ExcelReaderException {

    private int sheetIndex;
    private int rowIndex;
    private int colIndex;
    private String abcPosition;

    private String colTitle;
    private String cellValue;
    private String message;
    private Throwable cause;

    public ExcelAnnotaionReaderException(int sheetIndex, int rowIndex, int colIndex, String colTitle, String abcPosition, String cellValue, String message, Throwable cause) {
        super(message, cause);
        this.sheetIndex = sheetIndex;
        this.rowIndex = rowIndex;
        this.colIndex = colIndex;
        this.abcPosition = abcPosition;
        this.colTitle = colTitle;
        this.cellValue = cellValue;
        this.message = message;
        this.cause = cause;

    }

    public ExcelAnnotaionReaderException(int sheetIndex, int rowIndex, int colIndex, String abcPosition, String colTitle, String cellValue, String message) {
        super(message);
        this.sheetIndex = sheetIndex;
        this.rowIndex = rowIndex;
        this.colIndex = colIndex;
        this.abcPosition = abcPosition;
        this.cellValue = cellValue;
        this.message = message;
        this.colTitle = colTitle;

    }

    public ExcelAnnotaionReaderException(int sheetIndex, int rowIndex, int colIndex, String abcPosition, String message) {
        super(message);
        this.sheetIndex = sheetIndex;
        this.rowIndex = rowIndex;
        this.colIndex = colIndex;
        this.abcPosition = abcPosition;
        this.message = message;

    }

    public ExcelAnnotaionReaderException(int sheetIndex, int rowIndex, String message) {
        super(message);
        this.sheetIndex = sheetIndex;
        this.rowIndex = rowIndex;
        this.message = message;

    }

    public ExcelAnnotaionReaderException(String message) {
        super(message);
    }

    public ExcelAnnotaionReaderException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExcelAnnotaionReaderException(Throwable cause) {
        super(cause);
    }


    public String getShortMessage() {
        return String.format("sheet:[ %d ], row:[ %d ], col:[ %d ], cellValue: [ %s ], message:[ %s ]", this.sheetIndex,
                this.rowIndex, this.colIndex, this.cellValue, this.message);
    }

    public String getHumaneMessage() {
        return StringFormatter.format("第{}个Sheet页的第{}行第{}列[{}]的数据[{}]读取异常! 可能原因:{}\n异常堆栈:{}", this.sheetIndex,
                this.rowIndex, this.colIndex, this.abcPosition, this.cellValue, this.message,
                (this.cause == null ? "" : ExceptionUtils.getStackTrace(this.cause)));
    }

    @Override
    public String toString() {
        return getHumaneMessage();
    }
}
