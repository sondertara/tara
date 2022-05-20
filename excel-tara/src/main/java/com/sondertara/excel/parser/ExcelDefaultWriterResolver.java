package com.sondertara.excel.parser;

import com.sondertara.excel.common.Constant;
import com.sondertara.excel.entity.ExcelEntity;
import com.sondertara.excel.entity.ExcelHelper;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.DefaultIndexedColorMap;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExcelDefaultWriterResolver {

    final Map<Integer, Integer> columnWidthMap = new HashMap<>();

    private XSSFCellStyle headCellStyle;

    /**
     * 自动适配中文单元格
     *
     * @param cell        cell
     * @param columnIndex index
     */
    protected void calculateColumnWidth(SXSSFCell cell, Integer columnIndex) {
        if (Constant.OPEN_AUTO_COLUMN_WIDTH) {
            String cellValue = cell.getStringCellValue();
            int length = cellValue.getBytes().length;
            length += (int) Math.ceil((double) ((cellValue.length() * 3 - length) / 2) * 0.1D);
            length = Math.max(length, Constant.CHINESES_ATUO_SIZE_COLUMN_WIDTH_MIN);
            length = Math.min(length, Constant.CHINESES_ATUO_SIZE_COLUMN_WIDTH_MAX);
            if (columnWidthMap.get(columnIndex) == null || columnWidthMap.get(columnIndex) < length) {
                columnWidthMap.put(columnIndex, length);
            }
        }
    }

    /**
     * auto size of chinese
     * 自动适配中文单元格
     *
     * @param sheet      sheet
     * @param columnSize size
     */
    protected void sizeColumnWidth(SXSSFSheet sheet, Integer columnSize) {
        if (Constant.OPEN_AUTO_COLUMN_WIDTH) {
            for (int j = 0; j < columnSize; j++) {
                if (columnWidthMap.get(j) != null) {
                    sheet.setColumnWidth(j, columnWidthMap.get(j) * 256);
                }
            }
        }
    }

    public CellStyle getHeaderCellStyle(SXSSFWorkbook workbook) {
        if (headCellStyle == null) {
            headCellStyle = workbook.getXSSFWorkbook().createCellStyle();
            headCellStyle.setBorderTop(BorderStyle.NONE);
            headCellStyle.setBorderRight(BorderStyle.NONE);
            headCellStyle.setBorderBottom(BorderStyle.NONE);
            headCellStyle.setBorderLeft(BorderStyle.NONE);
            headCellStyle.setAlignment(HorizontalAlignment.CENTER);
            headCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            XSSFColor color = new XSSFColor(new java.awt.Color(217, 217, 217), new DefaultIndexedColorMap());
            headCellStyle.setFillForegroundColor(color);
            headCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            Font font = workbook.createFont();
            font.setFontName("微软雅黑");
            font.setColor(IndexedColors.ROYAL_BLUE.index);
            font.setBold(true);
            headCellStyle.setFont(font);
            headCellStyle.setDataFormat(workbook.createDataFormat().getFormat("@"));
        }
        return headCellStyle;
    }
}
