package com.sondertara.excel.meta.model;

import com.sondertara.excel.meta.celltype.ExcelCellType;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

/**
 * Excel字段类型定义
 *
 * @author huangxiaohu
 */

@Slf4j
public class ExcelCellDef implements Serializable {

    private Integer sheetIndex;
    private Integer rowIndex;
    private Integer colIndex;
    private String colTitle;

    private String abcColIndex;
    private String cellValue;
    private ExcelCellType cellType;

    public Integer getSheetIndex() {
        return sheetIndex;
    }

    public String getAbcColIndex() {
        return abcColIndex;
    }

    public void setAbcColIndex(String abcColIndex) {
        this.abcColIndex = abcColIndex;
    }

    public void setSheetIndex(final Integer sheetIndex) {
        this.sheetIndex = sheetIndex;
    }

    public Integer getRowIndex() {
        return rowIndex;
    }

    public void setRowIndex(final Integer rowIndex) {
        this.rowIndex = rowIndex;
    }

    public Integer getColIndex() {
        return colIndex;
    }

    public void setColIndex(final Integer colIndex) {
        this.colIndex = colIndex;
    }

    public String getColTitle() {
        return colTitle;
    }

    public void setColTitle(final String colTitle) {
        this.colTitle = colTitle;
    }

    public String getCellValue() {
        return cellValue;
    }

    public void setCellValue(final String cellValue) {
        this.cellValue = cellValue;
    }

    public ExcelCellType getCellType() {
        return cellType;
    }

    public void setCellType(final ExcelCellType cellType) {
        this.cellType = cellType;
    }

    @Override
    public String toString() {
        return "ExcelCell{" + "sheetIndex=" + sheetIndex + ", rowIndex=" + rowIndex + ", colIndex=" + colIndex
                + ", colTitle='" + colTitle + '\'' + ", cellValue='" + cellValue + '\'' + ", cellType='" + cellType
                + '\'' + '}';
    }

}
