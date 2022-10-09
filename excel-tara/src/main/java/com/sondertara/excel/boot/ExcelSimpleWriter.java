package com.sondertara.excel.boot;

import com.sondertara.excel.common.Constant;
import com.sondertara.excel.parser.ExcelDefaultWriterResolver;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Simple Excel writer
 *
 * @author huangxiaohu
 */
public class ExcelSimpleWriter extends AbstractExcelWriter<Workbook> {

    final Map<Integer, Integer> columnWidthMap = new HashMap<>();

    private XSSFCellStyle headCellStyle;

    private List<String> titles;
    private String sheetName;
    private SXSSFWorkbook workbook;

    public ExcelSimpleWriter(List<String> header) {
        this.sheetName = "Default";
        this.titles = header;
    }

    public static ExcelSimpleWriter header(List<String> titles) {
        return new ExcelSimpleWriter(titles);
    }

    public ExcelSimpleWriter sheetName(String sheetName) {
        this.sheetName = sheetName;
        return this;
    }

    public ExcelSimpleWriter workbook(SXSSFWorkbook workbook) {
        this.workbook = workbook;
        return this;
    }

    @Override
    public Workbook generate() {
        if (null == this.workbook) {
            this.workbook = new SXSSFWorkbook(Constant.DEFAULT_ROW_ACCESS_WINDOW_SIZE);
        }
        SXSSFSheet sheet = workbook.createSheet(sheetName);

        SXSSFRow headerRow = sheet.createRow(0);
        ExcelDefaultWriterResolver resolver = new ExcelDefaultWriterResolver();
        if (Constant.OPEN_CELL_STYLE) {
            headerRow.setHeight((short) 600);
            CellStyle headCellStyle = resolver.getHeaderCellStyle(workbook);
        }
        for (int i = 0; i < titles.size(); i++) {
            SXSSFCell cell = headerRow.createCell(i);
            if (Constant.OPEN_CELL_STYLE) {
                cell.setCellStyle(headCellStyle);
            }
            cell.setCellValue(titles.get(i));
            resolver.calculateColumnWidth(cell, i);
        }
        resolver.sizeColumnWidth(sheet, titles.size());
        return workbook;
    }

}
