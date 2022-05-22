package com.sondertara.excel.parser;


import com.sondertara.common.exception.TaraException;
import com.sondertara.common.util.LocalDateTimeUtils;
import com.sondertara.common.util.StringUtils;
import com.sondertara.excel.common.Constant;
import com.sondertara.excel.entity.ExcelCellEntity;
import com.sondertara.excel.entity.ExcelHelper;
import com.sondertara.excel.entity.ExcelWriteSheetEntity;
import com.sondertara.excel.entity.PageQueryParam;
import com.sondertara.excel.function.ExportFunction;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.io.FileUtils;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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

    public ExcelWriterResolver(ExcelWriteSheetEntity excelEntity, ExcelHelper excelHelper) {
        super(excelEntity,excelHelper);

    }


    public void generateCsv(String fileName) {
        try {
            final String workPath = Constant.FILE_PATH + File.separator + fileName + File.separator;
            File path = new File(workPath);

            List<File> fileList = new ArrayList<File>();
            if (path.exists()) {
                File[] files = path.listFiles();
                assert files != null;
                if (files.length <= 0) {
                    return;
                }
                Collections.addAll(fileList, files);
                final List<File> collect = fileList.stream().sorted(Comparator.comparing(File::getName)).collect(Collectors.toList());
                File csvFile = new File(workPath + fileName + ".csv");

                if (csvFile.exists()) {
                    boolean delete = csvFile.delete();
                    if (!delete) {
                        throw new IOException("Delete file:" + csvFile.getAbsolutePath() + " failed");
                    }
                } else {
                    boolean newFile = csvFile.createNewFile();
                    if (!newFile) {
                        throw new IOException("Create file:" + csvFile.getAbsolutePath() + " failed");
                    }
                }
                Appendable printWriter = new PrintWriter(csvFile, Constant.CHARSET);
                CSVPrinter csvPrinter = CSVFormat.EXCEL.builder().setHeader(excelEntity.getPropertyList().stream().map(ExcelCellEntity::getColumnName).toArray(String[]::new)).build().print(printWriter);

                csvPrinter.flush();
                csvPrinter.close();
                for (File file : collect) {
                    if (file.getName().endsWith("csv")) {
                        byte[] bytes = FileUtils.readFileToByteArray(file);
                        FileUtils.writeByteArrayToFile(csvFile, bytes, true);
                    }
                    if (!file.getName().contains(fileName)) {
                        file.delete();
                    }
                }
            }

        } catch (Exception e) {
            logger.error("write into file error:{}", e.toString());
        }

    }

    /**
     * @param param          pagination param
     * @param exportFunction export function
     * @param <R>            export pojo
     * @return workbook
     * @throws InvocationTargetException e
     * @throws NoSuchMethodException     e
     * @throws ParseException            e
     * @throws IllegalAccessException    e
     */
    public <R> SXSSFWorkbook generateWorkbook(PageQueryParam param, ExportFunction<R> exportFunction) {
        SXSSFWorkbook workbook = new SXSSFWorkbook(excelHelper.getRowAccessWindowSize());
        int sheetNo = 1;
        int rowNum = 1;
        List<ExcelCellEntity> propertyList = excelEntity.getPropertyList();
        //generate first row head.
        SXSSFSheet sheet = generateHeader(workbook, propertyList, excelEntity.getSheetName());

        // generate data rows
        int firstPageNo = 1;
        while (true) {
            List<R> data = exportFunction.queryPage(firstPageNo, param.getPageSize());
            if (data == null || data.isEmpty()) {
                if (rowNum != 1) {
                    if (Constant.OPEN_CELL_STYLE) {
                        sizeColumnWidth(sheet, propertyList.size());
                    }
                }
                logger.warn("query data is empty,query exit!");
                break;
            }
            int dataSize = data.size();

            for (int i = 1; i <= dataSize; i++, rowNum++) {
                R queryResult = data.get(i - 1);
                if (rowNum > Constant.MAX_RECORD_COUNT_PEER_SHEET) {
                    if (Constant.OPEN_CELL_STYLE) {
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
            if (data.size() < param.getPageSize()) {
                sizeColumnWidth(sheet, propertyList.size());
                logger.warn("current query data size is [{}],less than pageSize[{}],is the last page,query exit!", data.size(), param.getPageSize());
                break;
            }
            firstPageNo++;
        }
        return workbook;
    }



    /**
     * 构建多Sheet Excel
     *
     * @param param          pagination param
     * @param exportFunction export
     * @param <R>            the type of param
     * @return workbook
     * @throws InvocationTargetException e
     * @throws NoSuchMethodException     e
     * @throws ParseException            e
     * @throws IllegalAccessException    e
     */
    public <R> SXSSFWorkbook generateMultiSheetWorkbook(PageQueryParam param, ExportFunction<R> exportFunction) {
        int pageNo = 1;
        int sheetNo = 1;
        int rowNum = 1;
        SXSSFWorkbook workbook = new SXSSFWorkbook(excelHelper.getRowAccessWindowSize());
        List<ExcelCellEntity> propertyList = excelEntity.getPropertyList();
        SXSSFSheet sheet = generateHeader(workbook, propertyList, excelEntity.getSheetName());

        while (true) {
            List<R> data = exportFunction.queryPage(pageNo, param.getPageSize());
            if (data == null || data.isEmpty()) {
                if (rowNum != 1) {
                    sizeColumnWidth(sheet, propertyList.size());
                }
                logger.warn("query result is empty,query exit!");
                break;
            }
            for (int i = 1; i <= data.size(); i++, rowNum++) {
                R queryResult = data.get(i - 1);
                if (rowNum > excelHelper.getRecordCountPerSheet()) {
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
            if (data.size() < param.getPageSize()) {
                sizeColumnWidth(sheet, propertyList.size());
                logger.warn("current query data size is [{}],less than pageSize[{}],is the last page,query exit!", data.size(), param.getPageSize());
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
