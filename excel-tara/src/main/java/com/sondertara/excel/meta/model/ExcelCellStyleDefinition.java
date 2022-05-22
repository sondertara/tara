package com.sondertara.excel.meta.model;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Workbook;

/**
 * @author chenzw
 */
public class ExcelCellStyleDefinition {

    private CellStyle cellStyle;

    private Font font;

    public ExcelCellStyleDefinition(Workbook workbook) {
        this.cellStyle = workbook.createCellStyle();
        this.font = workbook.createFont();

        this.cellStyle.setFont(font);
    }

    public ExcelCellStyleDefinition(CellStyle cellStyle, Font font) {
        this.cellStyle = cellStyle;
        this.font = font;

        this.cellStyle.setFont(font);
    }

    public CellStyle getCellStyle() {
        return cellStyle;
    }

    public void setCellStyle(CellStyle cellStyle) {
        this.cellStyle = cellStyle;
    }

    public Font getFont() {
        return font;
    }

    public void setFont(Font font) {
        this.font = font;
    }
}
