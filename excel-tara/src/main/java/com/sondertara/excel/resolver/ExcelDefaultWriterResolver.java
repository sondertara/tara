package com.sondertara.excel.resolver;

import com.sondertara.excel.base.TaraExcelConfig;
import com.sondertara.excel.common.constants.Constants;
import com.sondertara.excel.fast.writer.Color;
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

import java.util.HashMap;
import java.util.Map;

/**
 * @author huangxiaohu
 */
public class ExcelDefaultWriterResolver {

    final Map<Integer, Integer> columnWidthMap = new HashMap<>();

    private XSSFCellStyle headCellStyle;

    /**
     * 自动适配中文单元格
     *
     * @param cell        cell
     * @param columnIndex index
     */
    public void calculateColumnWidth(Cell cell, Integer columnIndex) {
        if (TaraExcelConfig.CONFIG.isOpenAutoColWidth()) {

            String cellValue = new DataFormatter().formatCellValue(cell);
            int length = cellValue.getBytes().length;
            length = cellValue.length() + (int) Math.ceil((length - cellValue.length()) * 0.9d / 2);
            length = Math.max(length, Constants.CHINESE_AUTO_SIZE_COLUMN_WIDTH_MIN);
            length = Math.min(length, Constants.CHINESE_AUTO_SIZE_COLUMN_WIDTH_MAX);
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
    public void sizeColumnWidth(SXSSFSheet sheet, Integer columnSize) {
        if (TaraExcelConfig.CONFIG.isOpenAutoColWidth()) {
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
            headCellStyle.setBorderTop(BorderStyle.THIN);
            headCellStyle.setBorderRight(BorderStyle.THIN);
            headCellStyle.setBorderBottom(BorderStyle.THIN);
            headCellStyle.setBorderLeft(BorderStyle.THIN);
            headCellStyle.setAlignment(HorizontalAlignment.CENTER);
            headCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            XSSFColor color = new XSSFColor(ColorUtils.hexToRgb(Color.EXCEL_GREEN_TITLE), new DefaultIndexedColorMap());
            headCellStyle.setFillForegroundColor(color);
            headCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            Font font = workbook.createFont();
            font.setFontHeightInPoints((short) 11);
            font.setFontName("微软雅黑");
            font.setColor(IndexedColors.WHITE.index);
            font.setBold(true);
            headCellStyle.setFont(font);
            headCellStyle.setDataFormat(workbook.createDataFormat().getFormat("@"));
        }
        return headCellStyle;
    }

    public void addColumnWidth(int columnIndex, int columnWidth) {
        this.columnWidthMap.put( columnIndex,columnWidth);
    }

    public void clearColumnWidthMap(){
        this.columnWidthMap.clear();
    }
}
