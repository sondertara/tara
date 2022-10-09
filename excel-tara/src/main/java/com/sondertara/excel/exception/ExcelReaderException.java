package com.sondertara.excel.exception;

import org.apache.commons.lang3.exception.ExceptionUtils;

public class ExcelReaderException extends ExcelException {

    private int sheetIndex;
    private int rowIndex;
    private int colIndex;
    private String abcPosition;
    private String cellValue;
    private String message;
    private Throwable cause;

    public ExcelReaderException(int sheetIndex, int rowIndex, int colIndex, String cellValue, String message) {
        this.sheetIndex = sheetIndex;
        this.rowIndex = rowIndex;
        this.colIndex = colIndex;
        this.cellValue = cellValue;
        this.message = message;
    }

    public ExcelReaderException(int sheetIndex, int rowIndex, int colIndex, String cellValue, String message,
                                Throwable cause) {
        this.sheetIndex = sheetIndex;
        this.rowIndex = rowIndex;
        this.colIndex = colIndex;
        this.cellValue = cellValue;
        this.message = message;
        this.cause = cause;
    }

    public ExcelReaderException(String message) {
        super(message);
        this.message = message;
    }

    public ExcelReaderException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExcelReaderException(Throwable cause) {
        super(cause);
    }

    public int getSheetIndex() {
        return sheetIndex;
    }

    public int getRowIndex() {
        return rowIndex;
    }

    public int getColIndex() {
        return colIndex;
    }

    public String getAbcPosition() {
        return abcPosition;
    }

    public String getCellValue() {
        return cellValue;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public String getShortMessage() {
        return String.format("sheet:[ %d ], row:[ %d ], col:[ %d ], cellValue: [ %s ], message:[ %s ]", this.sheetIndex,
                this.rowIndex, this.colIndex, this.cellValue, this.message);
    }

    public String getHumaneMessage() {
        return String.format("第 %d 个Sheet页 的第 %d 行第 %d 列的数据[%s]读取异常! 可能原因:[%s]! \n 详细堆栈信息: [%s] ", this.sheetIndex,
                this.rowIndex, this.colIndex, this.cellValue, this.message,
                (this.cause == null ? "" : ExceptionUtils.getStackTrace(this.cause)));
    }

    @Override
    public String toString() {
        return getHumaneMessage();
    }
}
