package com.sondertara.excel.executor;

import com.google.common.collect.Lists;
import com.sondertara.common.util.CollectionUtils;
import com.sondertara.excel.ExcelFieldUtils;
import com.sondertara.excel.context.ExcelWriterContext;
import com.sondertara.excel.entity.PageQueryParam;
import com.sondertara.excel.entity.PageResult;
import com.sondertara.excel.enums.ExcelDataType;
import com.sondertara.excel.exception.ExcelException;
import com.sondertara.excel.exception.ExcelWriterException;
import com.sondertara.excel.function.ExportFunction;
import com.sondertara.excel.lifecycle.ExcelWriterLifecycle;
import com.sondertara.excel.meta.AnnotationSheet;
import com.sondertara.excel.meta.annotation.CellRange;
import com.sondertara.excel.meta.annotation.ExcelComplexHeader;
import com.sondertara.excel.meta.annotation.ExcelDataFormat;
import com.sondertara.excel.meta.annotation.ExcelExportField;
import com.sondertara.excel.meta.annotation.datavalidation.ExcelDataValidation;
import com.sondertara.excel.meta.model.AnnotationExcelWriterSheetDefinition;
import com.sondertara.excel.meta.model.ExcelCellStyleDefinition;
import com.sondertara.excel.meta.style.CellStyleBuilder;
import com.sondertara.excel.support.dataconstraint.ExcelDataValidationConstraint;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.DataValidationConstraint;
import org.apache.poi.ss.usermodel.DataValidationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author chenzw
 */
public abstract class AbstractExcelWriterExecutor implements TaraExcelExecutor<Workbook>, ExcelWriterLifecycle {

    private static final Logger logger = LoggerFactory.getLogger(AbstractExcelWriterExecutor.class);

    protected int curSheetIndex;
    protected int curRowIndex;
    protected int curColIndex;
    private final ExcelWriterContext writerContext;
    private final CellStyleCache cellStyleCache;
    private final SXSSFWorkbook sxssfWorkbook;
    private Map<Integer, AnnotationSheet> sheetDefinitions;

    public AbstractExcelWriterExecutor(final ExcelWriterContext writerContext) {
        this.sxssfWorkbook = new SXSSFWorkbook(new XSSFWorkbook(), 1000);
        this.sxssfWorkbook.setCompressTempFiles(true);
        this.writerContext = writerContext;
        this.cellStyleCache = new CellStyleCache();
    }

    @Override
    public abstract void beforeCallback();

    @Override
    public void sheetPaging() {
        for (final Map.Entry<Integer, AnnotationSheet> sheetDefinitionEntry : sheetDefinitions.entrySet()) {
            AnnotationExcelWriterSheetDefinition<?> sheetDefinition = (AnnotationExcelWriterSheetDefinition<?>) sheetDefinitionEntry.getValue();
            this.curSheetIndex = sheetDefinitionEntry.getKey();
            ExcelDataType excelDataType = sheetDefinition.getExcelDataType();
            switch (excelDataType) {
                case QUERY:
                    PageQueryParam pagination = sheetDefinition.getPagination();
                    PageResult<?> page = sheetDefinition.getQueryFunction().queryPage(1, 1);
                    long totalRow = page.getTotal();

                    int pageCountPreSheet = sheetDefinition.getMaxRowsPerSheet() / pagination.getPageSize();
                    double segment = Math.ceil(totalRow * 1.0d / sheetDefinition.getMaxRowsPerSheet());
                    if (segment > 1) {
                        for (int i = 0; i < segment; i++) {
                            writerContext.addMapper(sheetDefinition.getClass(), sheetDefinition.getQueryFunction(), PageQueryParam.builder().pageStart(i * pageCountPreSheet + 1).pageEnd((i + 1) * pageCountPreSheet).pageSize(pagination.getPageSize()).build());
                        }
                        writerContext.removeSheet(sheetDefinitionEntry.getKey());
                    }
                    break;
                case DIRECT:
                    List<? extends List<?>> segments = Lists.partition(sheetDefinition.getRows(), sheetDefinition.getMaxRowsPerSheet());
                    if (segments.size() > 1) {
                        writerContext.addData(segments);
                        writerContext.removeSheet(sheetDefinitionEntry.getKey());
                    }
                    break;
                default:
            }
        }
    }


    @Override
    public void handleComplexHeader() {
        for (final Map.Entry<Integer, AnnotationSheet> sheetDefinitionEntry : sheetDefinitions.entrySet()) {
            this.curSheetIndex = sheetDefinitionEntry.getKey();
            AnnotationExcelWriterSheetDefinition<?> sheetDefinition = (AnnotationExcelWriterSheetDefinition<?>) sheetDefinitionEntry.getValue();
            final Sheet sheet = createSheet(sheetDefinitionEntry.getKey(), sheetDefinition.getName());
            final ExcelComplexHeader excelComplexHeader = sheetDefinition.getAnnotation(ExcelComplexHeader.class);
            if (excelComplexHeader != null) {
                CellRange[] cellRanges = excelComplexHeader.value();
                for (final CellRange cellRange : cellRanges) {
                    this.curRowIndex = cellRange.firstRow();
                    this.curColIndex = cellRange.firstCol();
                    final int firstRow = cellRange.firstRow() - 1;
                    final int firstCol = cellRange.firstCol() - 1;
                    final int lastRow = cellRange.lastRow() - 1;
                    final int lastCol = cellRange.lastCol() - 1;

                    Row row = sheet.getRow(firstRow);
                    if (row == null) {
                        row = sheet.createRow(firstRow);
                    }
                    row.setHeightInPoints(cellRange.height());

                    final Cell cell = row.createCell(firstCol);
                    cell.setCellValue(cellRange.title());

                    // 合并单元格
                    final CellRangeAddress cellRangeAddress = new CellRangeAddress(firstRow, lastRow, firstCol, lastCol);
                    sheet.addMergedRegion(cellRangeAddress);

                    // 设置样式
                    final CellStyleBuilder cellStyleBuilder = this.cellStyleCache.getCellStyleInstance(cellRange.cellStyleBuilder());
                    cell.setCellStyle(cellStyleBuilder.build(this.sxssfWorkbook, new ExcelCellStyleDefinition(this.sxssfWorkbook), cell));
                }
            }
        }
    }

    @Override
    public void addDataValidation() {
        for (final Map.Entry<Integer, AnnotationSheet> sheetDefinitionEntry : sheetDefinitions.entrySet()) {
            this.curSheetIndex = sheetDefinitionEntry.getKey();
            AnnotationExcelWriterSheetDefinition<?> sheetDefinition = (AnnotationExcelWriterSheetDefinition<?>) sheetDefinitionEntry.getValue();
            final Sheet sheet = createSheet(sheetDefinitionEntry.getKey(), sheetDefinition.getName());
            final Map<Integer, Field> columnFields = sheetDefinition.getColFields();
            for (final Map.Entry<Integer, Field> columnFieldEntry : columnFields.entrySet()) {
                final Field field = columnFieldEntry.getValue();
                final int colIndex = this.curColIndex = columnFieldEntry.getKey();

                if (colIndex < 1) {
                    throw new IllegalArgumentException(field.getName() + "' colIndex less than 1");
                }

                final String[] dataValidationConstraintList = getDataValidationConstraint(field);
                if (dataValidationConstraintList != null) {

                    final DataValidationHelper helper = sheet.getDataValidationHelper();
                    // 加载下拉列表内容
                    final DataValidationConstraint dataConstraint = helper.createExplicitListConstraint(dataValidationConstraintList);
                    dataConstraint.setExplicitListValues(dataValidationConstraintList);
                    final CellRangeAddressList regions = new CellRangeAddressList(sheetDefinition.getFirstDataRow(), 999, colIndex - 1, colIndex - 1);

                    final DataValidation dataValidation = helper.createValidation(dataConstraint, regions);

                    dataValidation.setSuppressDropDownArrow(true);
                    dataValidation.createPromptBox("提示", "可选值:" + Arrays.toString(dataValidationConstraintList));
                    dataValidation.createErrorBox("错误提示", "您的输入有误, 可选值:" + Arrays.toString(dataValidationConstraintList));
                    dataValidation.setShowPromptBox(true);
                    dataValidation.setShowErrorBox(true);

                    sheet.addValidationData(dataValidation);
                }
            }
        }
    }


    @Override
    public void initHeadTitle() {
        for (final Map.Entry<Integer, AnnotationSheet> sheetDefinitionEntry : sheetDefinitions.entrySet()) {
            this.curSheetIndex = sheetDefinitionEntry.getKey();
            AnnotationExcelWriterSheetDefinition<?> sheetDefinition = (AnnotationExcelWriterSheetDefinition<?>) sheetDefinitionEntry.getValue();
            final Sheet sheet = createSheet(sheetDefinitionEntry.getKey(), sheetDefinition.getName());

            final Row row = sheet.createRow(sheetDefinition.getFirstDataRow() - 1);
            row.setHeightInPoints(sheetDefinition.getTitleRowHeight());

            this.curRowIndex = row.getRowNum() + 1;
            final Map<Integer, Field> columnFields = sheetDefinition.getColFields();
            for (final Map.Entry<Integer, Field> columnFieldEntry : columnFields.entrySet()) {
                final Field field = columnFieldEntry.getValue();
                final int colIndex = this.curColIndex = columnFieldEntry.getKey();

                if (colIndex < 1) {
                    throw new IllegalArgumentException(field.getName() + "' colIndex less than 1");
                }

                final ExcelExportField exportColumn = field.getAnnotation(ExcelExportField.class);
                final Cell cell = row.createCell(colIndex - 1);
                cell.setCellValue(exportColumn.colName());

                // 设置标题样式
                final CellStyleBuilder cellStyleBuilder = this.cellStyleCache.getCellStyleInstance(exportColumn.titleCellStyleBuilder());

                final CellStyle cellStyle = cellStyleBuilder.build(this.sxssfWorkbook, new ExcelCellStyleDefinition(this.sxssfWorkbook), cell);
                cell.setCellStyle(cellStyle);
                if (!exportColumn.autoWidth()) {
                    sheet.setColumnWidth(colIndex - 1, exportColumn.colWidth() * 256);
                }
            }
        }
    }


    @Override
    public void initData() {
        for (final Map.Entry<Integer, AnnotationSheet> sheetDefinitionEntry : sheetDefinitions.entrySet()) {
            this.curSheetIndex = sheetDefinitionEntry.getKey();
            AnnotationExcelWriterSheetDefinition<?> sheetDefinition = (AnnotationExcelWriterSheetDefinition<?>) sheetDefinitionEntry.getValue();
            final Sheet sheet = createSheet(sheetDefinitionEntry.getKey(), sheetDefinition.getName());
            final Map<Integer, Field> columnFields = sheetDefinition.getColFields();

            final Map<Integer, ExcelCellStyleDefinition> columnCellStyles = sheetDefinition.getColumnCellStyles(sxssfWorkbook);

            ExportFunction<?> queryFunction = sheetDefinition.getQueryFunction();
            PageQueryParam queryParam = sheetDefinition.getPagination();
            int pageNo = queryParam.getPageStart();
            while (true) {
                if (pageNo > queryParam.getPageEnd()) {
                    break;
                }
                PageResult<?> pageResult = queryFunction.queryPage(pageNo, queryParam.getPageSize());
                if (CollectionUtils.isEmpty(pageResult.getData())) {
                    break;
                }
                final List<?> rows = pageResult.getData();
                for (int rowIndex = 0; rowIndex < rows.size(); rowIndex++) {
                    final Row row = sheet.createRow(rowIndex + sheetDefinition.getFirstDataRow());
                    row.setHeightInPoints(sheetDefinition.getDataRowHeight());
                    this.curRowIndex = row.getRowNum() + 1;
                    final Object rowData = rows.get(rowIndex);
                    for (final Map.Entry<Integer, Field> columnFieldEntry : columnFields.entrySet()) {
                        this.curColIndex = columnFieldEntry.getKey();
                        final Field field = columnFieldEntry.getValue();
                        final Cell cell = row.createCell(columnFieldEntry.getKey() - 1);


                        final ExcelExportField exportColumn = field.getAnnotation(ExcelExportField.class);

                        ExcelCellStyleDefinition cellStyleDefinition;
                        if (sheetDefinition.isRowStriped()) {
                            if (rowIndex % 2 == 0) {
                                cellStyleDefinition = columnCellStyles.get(columnFieldEntry.getKey() * 2 - 1);
                            } else {
                                cellStyleDefinition = columnCellStyles.get(columnFieldEntry.getKey() * 2);
                            }
                        } else {
                            cellStyleDefinition = columnCellStyles.get(columnFieldEntry.getKey());
                        }
                        CellStyle cellStyle;

                        // 设置数据样式
                        final CellStyleBuilder cellStyleBuilder = this.cellStyleCache.getCellStyleInstance(exportColumn.dataCellStyleBuilder());
                        cellStyle = cellStyleBuilder.build(this.sxssfWorkbook, cellStyleDefinition, cell);

                        // 设置数据格式
                        final ExcelDataFormat excelDataFormat = exportColumn.dataFormat();
                        if (!StringUtils.isBlank(excelDataFormat.value())) {
                            final DataFormat dataFormat = this.sxssfWorkbook.createDataFormat();
                            cellStyle.setDataFormat(dataFormat.getFormat(excelDataFormat.value()));
                        }

                        cell.setCellStyle(cellStyle);

                        try {
                            ExcelFieldUtils.setCellValue(cell, rowData, field, exportColumn);
                        } catch (final IllegalAccessException e) {
                            throw new ExcelWriterException("", e);
                        }
                    }
                    pageNo++;
                }

                // 设置列自动大小
                if (sheet instanceof SXSSFSheet) {
                    final SXSSFSheet sxssfSheet = (SXSSFSheet) sheet;
                    sxssfSheet.trackAllColumnsForAutoSizing();

                    for (final Map.Entry<Integer, Field> columnFieldEntry : columnFields.entrySet()) {
                        final Field field = columnFieldEntry.getValue();
                        final ExcelExportField exportColumn = field.getAnnotation(ExcelExportField.class);
                        if (exportColumn.autoWidth()) {
                            sxssfSheet.autoSizeColumn(columnFieldEntry.getKey());
                        }
                    }
                }
            }
        }
    }

    @Override
    public abstract void afterCallback();

    @Override
    public Workbook execute() {
        logger.debug("start write!");

        final long startTimeMillis = System.currentTimeMillis();
        this.sheetDefinitions = this.writerContext.getSheetDefinitions();
        this.sheetPaging();
        this.handleComplexHeader();
        this.addDataValidation();
        this.initHeadTitle();
        this.initData();

        logger.debug("finish write![total cost {}ms]", (System.currentTimeMillis() - startTimeMillis));
        return this.sxssfWorkbook;
    }

    /**
     * 获取列的下拉校验值列表
     *
     * @param field the Excel field
     * @return the values
     */

    @SuppressWarnings("unchecked")
    private String[] getDataValidationConstraint(final Field field) {
        final Annotation[] annotations = field.getAnnotations();
        for (final Annotation annotation : annotations) {
            final Class<? extends Annotation> aClass = annotation.annotationType();
            if (aClass.isAnnotationPresent(ExcelDataValidation.class)) {
                final ExcelDataValidation dataValidation = aClass.getAnnotation(ExcelDataValidation.class);
                try {
                    ExcelDataValidationConstraint<Annotation> dataValidationConstraint = dataValidation.dataConstraint().newInstance();
                    dataValidationConstraint.initialize(annotation);
                    return dataValidationConstraint.generate();
                } catch (final InstantiationException | IllegalAccessException e) {
                    throw new ExcelException("实例化下拉值约束[" + dataValidation.dataConstraint() + "]时失败!", e);
                }
            }
        }
        return null;
    }

    private Sheet createSheet(final int sheetIndex, final String sheetName) {
        SXSSFSheet sheet;
        if (sheetIndex >= this.sxssfWorkbook.getNumberOfSheets()) {
            sheet = this.sxssfWorkbook.createSheet(sheetName);
        } else {
            sheet = this.sxssfWorkbook.getSheetAt(sheetIndex);
        }
        return sheet;
    }

    /**
     * 单元格样式缓存
     */
    private static class CellStyleCache {
        private final Map<Class<?>, CellStyleBuilder> cellStyleCacheMap;

        public CellStyleCache() {
            this.cellStyleCacheMap = new HashMap<>();
        }

        public void addCache(final Class<?> clazz, final CellStyleBuilder cellStyleBuilder) {
            this.cellStyleCacheMap.put(clazz, cellStyleBuilder);
        }

        public CellStyleBuilder getCellStyleInstance(final Class<?> clazz) {
            CellStyleBuilder cellStyleBuilder = this.getCache(clazz);
            if (cellStyleBuilder == null) {
                try {
                    if (CellStyleBuilder.class.isAssignableFrom(clazz)) {
                        cellStyleBuilder = (CellStyleBuilder) clazz.newInstance();
                    } else {
                        throw new ExcelWriterException("CellStyle [" + clazz + "] not assignable from CellStyleBuilder.class");
                    }
                } catch (final InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                }
                this.addCache(clazz, cellStyleBuilder);
            }
            return cellStyleBuilder;
        }

        public CellStyleBuilder getCache(final Class<?> clazz) {
            return this.cellStyleCacheMap.get(clazz);
        }

        public void removeCache(final Class<?> clazz) {
            this.cellStyleCacheMap.remove(clazz);
        }

    }
}
