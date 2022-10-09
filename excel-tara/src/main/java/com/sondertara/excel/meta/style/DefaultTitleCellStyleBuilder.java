package com.sondertara.excel.meta.style;

import com.sondertara.excel.meta.model.ExcelCellStyleDefinition;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;

/**
 * @author huangxiaohu
 */
public class DefaultTitleCellStyleBuilder implements CellStyleBuilder {

    @Override
    public CellStyle build(final Workbook workbook, final ExcelCellStyleDefinition cellStyleDefinition,
            final Cell cell) {
        final XSSFCellStyle cellStyle = (XSSFCellStyle) cellStyleDefinition.getCellStyle();
        final Font font = cellStyleDefinition.getFont();

        // 背景颜色
        cellStyle.setFillForegroundColor(IndexedColors.GREEN.getIndex());
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        // 设置字体
        font.setBold(true);

        // 设置对齐方式
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        return cellStyle;
    }
}
