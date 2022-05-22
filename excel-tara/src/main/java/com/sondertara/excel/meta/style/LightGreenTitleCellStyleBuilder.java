package com.sondertara.excel.meta.style;

import com.sondertara.excel.meta.model.ExcelCellStyleDefinition;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;

/**
 * 浅绿色
 */
public class LightGreenTitleCellStyleBuilder implements CellStyleBuilder {


    @Override
    public CellStyle build(Workbook workbook, ExcelCellStyleDefinition cellStyleDefinition, Cell cell) {
        XSSFCellStyle cellStyle = (XSSFCellStyle) cellStyleDefinition.getCellStyle();
        Font font = cellStyleDefinition.getFont();

        // 前景色
        cellStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(112, 173, 71), null));
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        // 对齐方式
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        // 黑体
        font.setFontHeightInPoints((short) 12);
        font.setBold(true);

        return cellStyle;
    }
}
