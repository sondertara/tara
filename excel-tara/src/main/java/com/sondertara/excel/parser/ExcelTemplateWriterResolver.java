package com.sondertara.excel.parser;


import com.sondertara.excel.common.Constant;
import com.sondertara.excel.entity.ExcelEntity;
import com.sondertara.excel.entity.ExcelHelper;
import com.sondertara.excel.entity.ExcelPropertyEntity;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.DefaultIndexedColorMap;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author huangxiaohu
 */
public class ExcelTemplateWriterResolver extends ExcelDefaultWriterResolver {

    final Map<Integer, Integer> columnWidthMap = new HashMap<>();

    final ExcelHelper excelHelper;

    final ExcelEntity excelEntity;

    private XSSFCellStyle headCellStyle;


    public ExcelTemplateWriterResolver(ExcelEntity excelEntity) {
        this.excelEntity = excelEntity;
        this.excelHelper = ExcelHelper.builder().build();
    }

    public ExcelTemplateWriterResolver(ExcelEntity excelEntity, ExcelHelper excelHelper) {
        this.excelEntity = excelEntity;
        this.excelHelper = excelHelper;
    }


    /**
     * 构建模板Excel
     *
     * @return workbook
     */
    public SXSSFWorkbook generateTemplate() {
        SXSSFWorkbook workbook = new SXSSFWorkbook();
        List<ExcelPropertyEntity> propertyList = excelEntity.getPropertyList();
        SXSSFSheet sheet = generateHeader(workbook, propertyList, excelEntity.getSheetName());
        SXSSFRow row = sheet.createRow(1);
        for (int j = 0; j < propertyList.size(); j++) {
            SXSSFCell cell = row.createCell(j);
            cell.setCellValue(propertyList.get(j).getTemplateCellValue());
            calculateColumnWidth(cell, j);
        }
        sizeColumnWidth(sheet, propertyList.size());
        return workbook;
    }

    /**
     * 初始化第一行的属性
     *
     * @param workbook     workbook
     * @param propertyList the Excel properties
     * @param sheetName    sheet name
     * @return SXSSFSheet
     */
    protected SXSSFSheet generateHeader(SXSSFWorkbook workbook, List<ExcelPropertyEntity> propertyList, String sheetName) {
        SXSSFSheet sheet = workbook.createSheet(sheetName);
        SXSSFRow headerRow = sheet.createRow(0);
        if (Constant.OPEN_CELL_STYLE) {
            headerRow.setHeight((short) 600);
            CellStyle headCellStyle = getHeaderCellStyle(workbook);
        }
        for (int i = 0; i < propertyList.size(); i++) {
            SXSSFCell cell = headerRow.createCell(i);
            if (Constant.OPEN_CELL_STYLE) {
                cell.setCellStyle(headCellStyle);
            }
            cell.setCellValue(propertyList.get(i).getColumnName());
            calculateColumnWidth(cell, i);
        }
        return sheet;
    }


}
