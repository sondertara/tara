package com.sondertara.excel.parser;

import com.sondertara.excel.constants.Constants;
import com.sondertara.excel.entity.ExcelCellEntity;
import com.sondertara.excel.entity.ExcelHelper;
import com.sondertara.excel.entity.ExcelWriteSheetEntity;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author huangxiaohu
 */
public class ExcelTemplateWriterResolver extends ExcelDefaultWriterResolver {

    final Map<Integer, Integer> columnWidthMap = new HashMap<>();

    final ExcelHelper excelHelper;

    final ExcelWriteSheetEntity excelEntity;

    private XSSFCellStyle headCellStyle;

    public ExcelTemplateWriterResolver(ExcelWriteSheetEntity excelEntity) {
        this.excelEntity = excelEntity;
        this.excelHelper = ExcelHelper.builder().build();
    }

    public ExcelTemplateWriterResolver(ExcelWriteSheetEntity excelEntity, ExcelHelper excelHelper) {
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
        List<ExcelCellEntity> propertyList = excelEntity.getPropertyList();
        SXSSFSheet sheet = generateHeader(workbook, propertyList, excelEntity.getSheetName());
        SXSSFRow row = sheet.createRow(1);
        for (int j = 0; j < propertyList.size(); j++) {
            SXSSFCell cell = row.createCell(j);
            cell.setCellValue(propertyList.get(j).getDefaultValue());
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
    protected SXSSFSheet generateHeader(SXSSFWorkbook workbook, List<ExcelCellEntity> propertyList, String sheetName) {
        SXSSFSheet sheet = workbook.createSheet(sheetName);
        SXSSFRow headerRow = sheet.createRow(0);
        if (Constants.OPEN_CELL_STYLE) {
            headerRow.setHeight((short) 600);
        }
        for (int i = 0; i < propertyList.size(); i++) {
            SXSSFCell cell = headerRow.createCell(i);
            if (Constants.OPEN_CELL_STYLE) {
                cell.setCellStyle(headCellStyle);
            }
            cell.setCellValue(propertyList.get(i).getColumnName());
            calculateColumnWidth(cell, i);
        }
        return sheet;
    }

}
