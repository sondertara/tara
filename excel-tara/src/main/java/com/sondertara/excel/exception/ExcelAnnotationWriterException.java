package com.sondertara.excel.exception;

import org.apache.commons.lang3.exception.ExceptionUtils;

/**
 * @author huangxiaohu
 */
public class ExcelAnnotationWriterException extends ExcelWriterException {

    private String sheetIdentity;

    private Integer sheetIndex;
    private Integer rowIndex;
    private Integer colIndex;
    private String cellValue;
    private String message;
    private Throwable cause;


    public ExcelAnnotationWriterException(String sheetIdentity, Integer sheetIndex, Integer rowIndex, Integer colIndex, String cellValue, String message, Throwable cause) {
        super(message, cause);

        this.sheetIdentity = sheetIdentity;
        this.sheetIndex = sheetIndex;
        this.rowIndex = rowIndex;
        this.colIndex = colIndex;
        this.cellValue = cellValue;
        this.message = message;
        this.cause = cause;
    }

    public ExcelAnnotationWriterException(String format, Object... arguments) {
        super(format, arguments);
    }

    public ExcelAnnotationWriterException(Throwable cause) {
        super(cause);
    }

    public Integer getSheetIndex() {
        return sheetIndex;
    }

    public void setSheetIndex(Integer sheetIndex) {
        this.sheetIndex = sheetIndex;
    }

    public Integer getRowIndex() {
        return rowIndex;
    }

    public void setRowIndex(Integer rowIndex) {
        this.rowIndex = rowIndex;
    }

    public Integer getColIndex() {
        return colIndex;
    }

    public void setColIndex(Integer colIndex) {
        this.colIndex = colIndex;
    }

    public String getCellValue() {
        return cellValue;
    }

    public void setCellValue(String cellValue) {
        this.cellValue = cellValue;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public Throwable getCause() {
        return cause;
    }

    public void setCause(Throwable cause) {
        this.cause = cause;
    }

    @Override
    public String toString() {
        return String.format("[%s]第 %d 个Sheet页的第 %d 行第 %d 列的数据[ %s ]写入异常! 可能原因:[%s]! \n 详细堆栈信息: [%s] ", this.sheetIdentity, this.sheetIndex, this.rowIndex, this.colIndex, this.cellValue, this.message, ExceptionUtils.getStackTrace(this.cause));
    }
}
