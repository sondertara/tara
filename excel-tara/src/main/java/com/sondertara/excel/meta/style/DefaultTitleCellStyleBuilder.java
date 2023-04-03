package com.sondertara.excel.meta.style;

import com.sondertara.excel.fast.writer.Color;
import com.sondertara.excel.meta.model.ExcelCellStyleDefinition;
import com.sondertara.excel.utils.ColorUtils;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.DefaultIndexedColorMap;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;

/**
 * @author chenzw
 */
public class DefaultTitleCellStyleBuilder implements CellStyleBuilder {


    @Override
    public CellStyle build(final Workbook workbook, final ExcelCellStyleDefinition cellStyleDefinition, final Cell cell) {
        final XSSFCellStyle cellStyle = (XSSFCellStyle) cellStyleDefinition.getCellStyle();
        final Font font = cellStyleDefinition.getFont();

        // 背景颜色
        XSSFColor color = new XSSFColor(ColorUtils.hexToRgb(Color.EXCEL_GREEN_TITLE), new DefaultIndexedColorMap());
        cellStyle.setFillForegroundColor(color);
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setBorderTop(BorderStyle.THIN);

        // 设置字体
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.index);
        cellStyle.setFont(font);
        // 设置对齐方式
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        return cellStyle;
    }
}
