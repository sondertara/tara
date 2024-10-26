package com.sondertara.excel.resolver;

import com.sondertara.excel.common.constants.Constants;
import com.sondertara.excel.utils.ColorUtils;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.DefaultIndexedColorMap;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.dhatim.fastexcel.Color;

import java.util.HashMap;
import java.util.Map;

/**
 * @author huangxiaohu
 */
public class ExcelDefaultWriterResolver {

    final Map<Integer, Integer> columnWidthMap = new HashMap<>();

    private XSSFCellStyle headCellStyle;

    private final int maxWidth;

    public ExcelDefaultWriterResolver(int maxWidth) {
        this.maxWidth = maxWidth;
    }

    /**
     * 自动适配中文单元格
     *
     * @param cell        cell
     * @param columnIndex index
     */
    public void calculateColumnWidth(Cell cell, Integer columnIndex) {
        String cellValue = new DataFormatter().formatCellValue(cell);
        int length = cellValue.getBytes().length;
        length = cellValue.length() + (int) Math.ceil((length - cellValue.length()) * 0.9d / 2);
        length = Math.max(length, Constants.CHINESE_AUTO_SIZE_COLUMN_WIDTH_MIN);
        length = Math.min(length, Constants.CHINESE_AUTO_SIZE_COLUMN_WIDTH_MAX);
        if (columnWidthMap.get(columnIndex) == null || columnWidthMap.get(columnIndex) < length) {
            columnWidthMap.put(columnIndex, length);
        }
    }

    /**
     * auto size of chinese,
     * 自动适配中文单元格
     *
     * @param sheet    sheet
     * @param endIndex endIndex not included
     */
    public void sizeColumnsWidth(SXSSFSheet sheet, Integer endIndex) {
        for (int j = 0; j < endIndex; j++) {
            if (columnWidthMap.get(j) != null) {
                sheet.setColumnWidth(j, Math.min(maxWidth, columnWidthMap.get(j) * 256));
            }
        }
    }

    public void adjustColWidth(SXSSFSheet sheet, int colIndex) {
        sheet.setColumnWidth(colIndex, Math.min(maxWidth, columnWidthMap.get(colIndex) * 256));
    }

    public CellStyle getHeaderCellStyle(SXSSFWorkbook workbook) {
        if (headCellStyle == null) {
            headCellStyle = workbook.getXSSFWorkbook().createCellStyle();
            headCellStyle.setBorderTop(BorderStyle.THIN);
            headCellStyle.setBorderRight(BorderStyle.THIN);
            headCellStyle.setBorderBottom(BorderStyle.THIN);
            headCellStyle.setBorderLeft(BorderStyle.THIN);
            headCellStyle.setAlignment(HorizontalAlignment.CENTER);
            headCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            XSSFColor color = new XSSFColor(ColorUtils.hexToRgb(Color.GRAY1), new DefaultIndexedColorMap());
            headCellStyle.setFillForegroundColor(color);
            headCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            Font font = workbook.createFont();
            font.setFontHeightInPoints((short) 11);
            font.setFontName("微软雅黑");
            font.setColor(IndexedColors.WHITE.getIndex());
            font.setBold(true);
            headCellStyle.setFont(font);
            headCellStyle.setDataFormat(workbook.createDataFormat().getFormat("@"));
        }
        return headCellStyle;
    }

    public void addColumnWidth(int columnIndex, int columnWidth) {
        this.columnWidthMap.put(columnIndex, columnWidth);
    }

    public void clearColumnWidthMap() {
        this.columnWidthMap.clear();
    }
}
