package com.sondertara.excel.meta.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author huangxiaohu
 */
public class ExcelRowDef implements Serializable {

    private static final long serialVersionUID = 4029152892615551071L;

    private Integer sheetIndex;
    private Integer rowIndex;
    private List<ExcelCellDef> excelCells = new ArrayList<>();

    public void addExcelCell(final ExcelCellDef excelCell) {
        this.excelCells.add(excelCell);
    }

    public Integer getSheetIndex() {
        return sheetIndex;
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

    public List<ExcelCellDef> getExcelCells() {
        return excelCells;
    }

    public void setExcelCells(final List<ExcelCellDef> excelCells) {
        this.excelCells = excelCells;
    }

    @Override
    public String toString() {
        return "ExcelRowDefinition{" + "sheetIndex=" + sheetIndex + ", rowIndex=" + rowIndex + ", excelCells="
                + excelCells + '}';
    }
}
