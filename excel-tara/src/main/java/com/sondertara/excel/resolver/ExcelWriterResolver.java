package com.sondertara.excel.resolver;

import com.sondertara.common.exception.TaraException;
import com.sondertara.common.model.PageResult;
import com.sondertara.common.util.LocalDateTimeUtils;
import com.sondertara.common.util.StringUtils;
import com.sondertara.excel.base.TaraExcelConfig;
import com.sondertara.excel.common.constants.Constants;
import com.sondertara.excel.entity.ExcelCellEntity;
import com.sondertara.excel.entity.ExcelWriteSheetEntity;
import com.sondertara.excel.function.ExportFunction;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

/**
 * excel write
 *
 * @author huangxiaohu
 */
public class ExcelWriterResolver extends ExcelTemplateWriterResolver {

    private static final Logger logger = LoggerFactory.getLogger(ExcelReader.class);

    private Integer nullCellCount = 0;

    public ExcelWriterResolver(ExcelWriteSheetEntity excelEntity) {
        super(excelEntity);

    }



    /**
     * @param exportFunction export function
     * @param <R>            export pojo
     * @return workbook
     * @throws InvocationTargetException e
     * @throws NoSuchMethodException     e
     * @throws ParseException            e
     * @throws IllegalAccessException    e
     */
    public <R> SXSSFWorkbook generateWorkbook( ExportFunction<R> exportFunction) {
        SXSSFWorkbook workbook = new SXSSFWorkbook(Constants.DEFAULT_ROW_ACCESS_WINDOW_SIZE);
        int sheetNo = 1;
        int rowNum = 1;
        List<ExcelCellEntity> propertyList = excelEntity.getPropertyList();
        // generate first row head.
        SXSSFSheet sheet = generateHeader(workbook, propertyList, excelEntity.getSheetName());

        // generate data rows
        int firstPageNo = 0;
        while (true) {
            PageResult<R> result = exportFunction.query(firstPageNo);
            List<R> data = result.getData();
            if (firstPageNo > result.endIndex() || result.isEmpty()) {
                if (rowNum != 1) {
                    if (Constants.OPEN_CELL_STYLE) {
                        sizeColumnWidth(sheet, propertyList.size());
                    }
                }
                logger.warn("query data is empty,query exit!");
                break;
            }
            int dataSize = data.size();
            for (int i = 1; i <= dataSize; i++, rowNum++) {
                R queryResult = data.get(i - 1);
                if (rowNum > Constants.MAX_RECORD_COUNT_PEER_SHEET) {
                    if (Constants.OPEN_CELL_STYLE) {
                        sizeColumnWidth(sheet, propertyList.size());
                    }
                    sheet = generateHeader(workbook, propertyList, excelEntity.getSheetName() + "_" + sheetNo);
                    sheetNo++;
                    rowNum = 1;
                    columnWidthMap.clear();
                }
                SXSSFRow row = sheet.createRow(rowNum);
                for (int j = 0; j < propertyList.size(); j++) {
                    SXSSFCell cell = row.createCell(j);
                    buildCellValue(cell, queryResult, propertyList.get(j));
                    calculateColumnWidth(cell, j);
                }
                if (nullCellCount == propertyList.size()) {
                    logger.warn("skip the empty row!");
                    sheet.removeRow(row);
                    rowNum--;
                }
                nullCellCount = 0;

            }
            if (data.size() < result.getPageSize()) {
                sizeColumnWidth(sheet, propertyList.size());
                logger.warn("current query data size is [{}],less than pageSize[{}],is the last page,query exit!", data.size(), result.getPageSize());
                break;
            }
            firstPageNo++;
        }
        return workbook;
    }

    /**
     * 构建多Sheet Excel
     *
     * @param exportFunction export
     * @param <R>            the type of param
     * @return workbook
     * @throws InvocationTargetException e
     * @throws NoSuchMethodException     e
     * @throws ParseException            e
     * @throws IllegalAccessException    e
     */
    public <R> SXSSFWorkbook generateMultiSheetWorkbook( ExportFunction<R> exportFunction) {
        int pageNo = 0;
        int sheetNo = 1;
        int rowNum = 1;
        SXSSFWorkbook workbook = new SXSSFWorkbook(Constants.DEFAULT_ROW_ACCESS_WINDOW_SIZE);
        List<ExcelCellEntity> propertyList = excelEntity.getPropertyList();
        SXSSFSheet sheet = generateHeader(workbook, propertyList, excelEntity.getSheetName());

        while (true) {
            PageResult<R> result = exportFunction.query(pageNo);
            if (pageNo > result.endIndex() || result.isEmpty()) {
                if (rowNum != 1) {
                    sizeColumnWidth(sheet, propertyList.size());
                }
                logger.warn("query result is empty,query exit!");
                break;
            }
            List<R> data = result.getData();

            for (int i = 1; i <= data.size(); i++, rowNum++) {
                R queryResult = data.get(i - 1);
                if (rowNum > TaraExcelConfig.CONFIG.getDefaultRowPeerSheet()) {
                    sizeColumnWidth(sheet, propertyList.size());
                    sheet = generateHeader(workbook, propertyList, excelEntity.getSheetName() + "_" + sheetNo);
                    sheetNo++;
                    rowNum = 1;
                    columnWidthMap.clear();
                }
                SXSSFRow bodyRow = sheet.createRow(rowNum);
                for (int j = 0; j < propertyList.size(); j++) {
                    SXSSFCell cell = bodyRow.createCell(j);
                    buildCellValue(cell, queryResult, propertyList.get(j));
                    calculateColumnWidth(cell, j);
                }
                if (nullCellCount == propertyList.size()) {
                    logger.warn("skip the empty row!");
                    sheet.removeRow(bodyRow);
                    rowNum--;
                }
                nullCellCount = 0;
            }
            if (data.size() < result.getPageSize()) {
                sizeColumnWidth(sheet, propertyList.size());
                logger.warn("current query data size is [{}],less than pageSize[{}],is the last page,query exit!", data.size(), result.getPageSize());
                break;
            }
            pageNo++;
        }
        return workbook;
    }

    /**
     * create the column of row start at the second row
     * 构造 除第一行以外的其他行的列值
     *
     * @param cell     cell
     * @param entity   data
     * @param property Excel properties
     */
    private void buildCellValue(SXSSFCell cell, Object entity, ExcelCellEntity property) {
        Field field = property.getFieldEntity();
        Object cellValue = null;
        try {
            cellValue = field.get(entity);
        } catch (IllegalAccessException e) {
            throw new TaraException("BuildCellValue error", e);
        }
        if (StringUtils.isBlank(cellValue) || "0".equals(cellValue.toString()) || "0.0".equals(cellValue.toString()) || "0.00".equals(cellValue.toString())) {
            nullCellCount++;
        }
        if (cellValue == null) {
            cell.setCellValue("");
        } else if (cellValue instanceof BigDecimal) {
            cell.setCellValue((((BigDecimal) cellValue).setScale(property.getScale(), property.getRoundingMode())).toString());

        } else if (cellValue instanceof Date) {
            cell.setCellValue(LocalDateTimeUtils.format((Date) cellValue, property.getDateFormat().value()));
        } else {
            cell.setCellValue(cellValue.toString());
        }
    }

}
