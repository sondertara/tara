package com.sondertara.excel.executor;

import com.google.common.collect.ArrayTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sondertara.common.lang.Pair;
import com.sondertara.common.lang.reflect.ReflectUtils;
import com.sondertara.common.model.PageResult;
import com.sondertara.common.util.CollectionUtils;
import com.sondertara.common.util.StringUtils;
import com.sondertara.excel.common.constants.Constants;
import com.sondertara.excel.context.ExcelRawWriterContext;
import com.sondertara.excel.exception.ExcelAnnotationWriterException;
import com.sondertara.excel.exception.ExcelWriterException;
import com.sondertara.excel.function.ExportFunction;
import com.sondertara.excel.lifecycle.ExcelWriterLifecycle;
import com.sondertara.excel.meta.annotation.CellRange;
import com.sondertara.excel.meta.annotation.ExcelComplexHeader;
import com.sondertara.excel.meta.annotation.ExcelDataFormat;
import com.sondertara.excel.meta.annotation.ExcelExportField;
import com.sondertara.excel.meta.annotation.converter.ExcelConverter;
import com.sondertara.excel.meta.annotation.datavalidation.ExcelDataValidation;
import com.sondertara.excel.meta.model.AnnotationExcelWriterSheetDefinition;
import com.sondertara.excel.meta.model.ExcelCellStyleDefinition;
import com.sondertara.excel.meta.model.TaraRow;
import com.sondertara.excel.meta.model.TaraSheet;
import com.sondertara.excel.meta.style.CellStyleBuilder;
import com.sondertara.excel.resolver.ExcelDefaultWriterResolver;
import com.sondertara.excel.support.converter.AbstractExcelColumnConverter;
import com.sondertara.excel.support.converter.ExcelDefaultConverter;
import com.sondertara.excel.support.dataconstraint.ExcelDataValidationConstraint;
import com.sondertara.excel.utils.CacheUtils;
import com.sondertara.excel.utils.ExcelAnnotationUtils;
import com.sondertara.excel.utils.ExcelFieldUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.DataValidationConstraint;
import org.apache.poi.ss.usermodel.DataValidationHelper;
import org.apache.poi.ss.usermodel.Row;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author huangxiaohu
 */
public abstract class AbstractExcelWriterExecutor implements TaraExcelExecutor<Workbook>, ExcelWriterLifecycle {

    private static final Logger logger = LoggerFactory.getLogger(AbstractExcelWriterExecutor.class);

    protected int curRowIndex;

    protected int curSheetIndex;
    protected int curColIndex;
    private final ExcelRawWriterContext<Workbook> writerContext;
    private final CellStyleCache cellStyleCache;
    private final SXSSFWorkbook sxssfWorkbook;

    private final ExcelDefaultWriterResolver resolver;

    private final AtomicInteger existSheetIndex = new AtomicInteger(0);

    private final ConcurrentHashMap<String, Integer> sheetNameMap = new ConcurrentHashMap<>();

    public AbstractExcelWriterExecutor(final ExcelRawWriterContext<Workbook> writerContext) {
        this.sxssfWorkbook = new SXSSFWorkbook(new XSSFWorkbook(), Constants.DEFAULT_ROW_ACCESS_WINDOW_SIZE);
        this.sxssfWorkbook.setCompressTempFiles(true);
        this.writerContext = writerContext;
        this.cellStyleCache = new CellStyleCache();
        this.resolver = new ExcelDefaultWriterResolver();
    }

    /**
     * before
     */
    @Override
    public abstract void beforeCallback();


    @Override
    public void handleComplexHeader(SXSSFSheet sheet, String sheetIdentity) {
        AnnotationExcelWriterSheetDefinition<?> sheetDefinition = (AnnotationExcelWriterSheetDefinition<?>) this.writerContext.getSheetDefinitions().get(sheetIdentity);
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

    @Override
    public void addDataValidation(SXSSFSheet sheet, String sheetIdentity) {
        AnnotationExcelWriterSheetDefinition<?> sheetDefinition = (AnnotationExcelWriterSheetDefinition<?>) this.writerContext.getSheetDefinitions().get(sheetIdentity);

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

    @Override
    public void initHeadTitle(SXSSFSheet sheet, String sheetIdentity) {
        AnnotationExcelWriterSheetDefinition<?> sheetDefinition = (AnnotationExcelWriterSheetDefinition<?>) this.writerContext.getSheetDefinitions().get(sheetIdentity);
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
            cell.setCellValue(ExcelAnnotationUtils.getColName(exportColumn));

            // 设置标题样式
            final CellStyleBuilder cellStyleBuilder = this.cellStyleCache.getCellStyleInstance(exportColumn.titleCellStyleBuilder());

            final CellStyle cellStyle = cellStyleBuilder.build(this.sxssfWorkbook, new ExcelCellStyleDefinition(this.sxssfWorkbook), cell);
            cell.setCellStyle(cellStyle);
            resolver.calculateColumnWidth(cell, colIndex - 1);
            if (sheetDefinition.isAutoColWidth() || !exportColumn.autoWidth()) {
                resolver.sizeColumnWidth(sheet, columnFields.size());
            }
        }
    }

    @Override
    public void initData() {
        for (Map.Entry<String, ? extends TaraSheet> entry : this.writerContext.getSheetDefinitions().entrySet()) {
            String sheetIdentity = entry.getKey();
            AnnotationExcelWriterSheetDefinition<?> sheetDefinition = (AnnotationExcelWriterSheetDefinition<?>) entry.getValue();
            Map<Integer, Object> lastRowData = new LinkedHashMap<>();
            switch (sheetDefinition.getExcelDataType()) {
                case DIRECT:
                    if (sheetDefinition.getRows().isEmpty()) {
                        createSheet(sheetDefinition.getName(), sheetIdentity);
                        break;
                    }
                    List<Object> list = sheetDefinition.getRows().stream().map(TaraRow::getRowData).collect(Collectors.toList());
                    List<List<Object>> lists = Lists.partition(list, sheetDefinition.getMaxRowsPerSheet());
                    for (List<Object> objects : lists) {
                        SXSSFSheet sxssfSheet = createSheet(sheetDefinition.getName(), sheetIdentity);
                        lastRowData = new LinkedHashMap<>();
                        createBody(sxssfSheet, sheetDefinition, objects, lastRowData);
                    }
                    break;
                case QUERY:
                    ExportFunction<?> queryFunction = sheetDefinition.getQueryFunction();
                    int pageNo = 0;
                    while (true) {
                        List<Object> existData = new LinkedList<>();
                        PageResult<?> result = queryFunction.query(pageNo);
                        if (result.isEmpty()) {
                            break;
                        }
                        List<?> data = result.getData();
                        SXSSFSheet existSheet = getSheet(sheetDefinition.getName(), sheetIdentity);
                        int rowDataCount = Math.max(0, existSheet.getLastRowNum() - sheetDefinition.getFirstDataRow());
                        int endIndex = Math.min(data.size(), sheetDefinition.getMaxRowsPerSheet() - rowDataCount);
                        for (int i = 0; i < endIndex; i++) {
                            existData.add(data.get(i));
                        }
                        createBody(existSheet, sheetDefinition, existData, lastRowData);
                        List<?> objects = data.subList(endIndex, data.size());
                        if (objects.isEmpty()) {
                            continue;
                        }
                        List<? extends List<?>> partition = Lists.partition(objects, sheetDefinition.getMaxRowsPerSheet());
                        for (List<?> sheetData : partition) {
                            SXSSFSheet newSheet = createSheet(sheetDefinition.getName(), sheetIdentity);
                            lastRowData = new LinkedHashMap<>();
                            createBody(newSheet, sheetDefinition, sheetData, lastRowData);
                        }
                        if (pageNo >= result.endIndex()) {
                            break;
                        }
                        pageNo++;
                    }
                    break;
                default:
            }
        }

    }

    @SuppressWarnings("UnstableApiUsage")
    private Map<Integer, Object> createRow(Map<Integer, Object> previous, Row row, int rowIndex, Object rowData, AnnotationExcelWriterSheetDefinition<?> sheetDefinition, ArrayTable<Integer, Integer, Integer> table) {
        this.curRowIndex = row.getRowNum() + 1;
        Map<Integer, Object> data = new LinkedHashMap<>();
        Map<Integer, ExcelCellStyleDefinition> columnCellStyles = sheetDefinition.getColumnCellStyles(sxssfWorkbook);
        Class<?> mappingClass = sheetDefinition.getMappingClass();
        for (final Map.Entry<Integer, Field> columnFieldEntry : sheetDefinition.getColFields().entrySet()) {
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

            // 值转换
            List<AbstractExcelColumnConverter<Annotation, ?>> columnConverters = CacheUtils.getColConverterCache().getIfPresent(mappingClass.getName() + "#" + field.getName());
            if (columnConverters == null) {
                columnConverters = findColumnConverter(field);
                CacheUtils.getColConverterCache().put(mappingClass.getName() + "#" + field.getName(), columnConverters);
            }
            Object value;
            try {
                value = field.get(rowData);
                if (null == value && StringUtils.isNotBlank(exportColumn.defaultCellValue())) {
                    value = exportColumn.defaultCellValue();
                }
            } catch (IllegalAccessException e) {
                throw new ExcelWriterException(e);
            }
            for (final AbstractExcelColumnConverter<Annotation, ?> columnConverter : columnConverters) {
                value = columnConverter.convert(value);
            }

            try {
                ExcelFieldUtils.setCellValue(cell, value, field, exportColumn, resolver);
                if (isSame(previous.get(curColIndex), value)) {
                    //set current and previous to merge flag
                    table.set(row.getRowNum() + 1, columnFieldEntry.getKey(), 1);
                    table.set(row.getRowNum(), columnFieldEntry.getKey(), 1);
                }
                data.put(curColIndex, value);
            } catch (final IllegalAccessException e) {
                throw new ExcelWriterException(e);
            }
        }
        return data;
    }

    @SuppressWarnings("UnstableApiUsage")
    private void createBody(SXSSFSheet sheet, AnnotationExcelWriterSheetDefinition<?> sheetDefinition, List<?> rows, Map<Integer, Object> previousRowData) {
        if (CollectionUtils.isEmpty(rows)) {
            return;
        }
        List<Integer> rowKeys = Stream.iterate(sheetDefinition.getFirstDataRow() + 1, item -> item + 1).limit(rows.size() + sheetDefinition.getFirstDataRow() + 1).toList();
        final Map<Integer, Field> columnFields = sheetDefinition.getColFields();
        Set<Integer> columnKeys = columnFields.keySet();
        ArrayTable<Integer, Integer, Integer> table = ArrayTable.create(rowKeys, columnKeys);
        Object firstData = rows.get(0);
        final Row firstRow = sheet.createRow(sheetDefinition.getFirstDataRow());
        firstRow.setHeightInPoints(sheetDefinition.getDataRowHeight());
        if (previousRowData.isEmpty()) {
            previousRowData = createRow(Maps.newHashMap(), firstRow, 0, firstData, sheetDefinition, table);
        }
        for (int rowIndex = 1; rowIndex < rows.size(); rowIndex++) {
            final Row row = sheet.createRow(rowIndex + sheetDefinition.getFirstDataRow());
            row.setHeightInPoints(sheetDefinition.getDataRowHeight());
            Object rowData = rows.get(rowIndex);
            previousRowData = createRow(previousRowData, row, rowIndex, rowData, sheetDefinition, table);
        }
        // 设置列自动大小
        sheet.trackAllColumnsForAutoSizing();
        for (final Map.Entry<Integer, Field> columnFieldEntry : columnFields.entrySet()) {
            final Field field = columnFieldEntry.getValue();
            ExcelExportField excelExportField = field.getAnnotation(ExcelExportField.class);
            Integer colIndex = columnFieldEntry.getKey();
            if (sheetDefinition.isAutoColWidth() || excelExportField.autoWidth()) {
                resolver.sizeColumnWidth(sheet, colIndex);
            } else if (Constants.DEFAULT_COL_WIDTH != excelExportField.colWidth()) {
                resolver.sizeColumnWidth(sheet, colIndex);
            }
            if (excelExportField.autoMerge()) {
                int start = -1;
                int end;
                boolean flag = true;
                Map<Integer, Integer> map = table.column(colIndex);

                int lastRowIndex = -1;
                while (map.entrySet().stream().iterator().hasNext()) {
                    Map.Entry<Integer, Integer> next = map.entrySet().stream().iterator().next();
                    if (next.getValue() == 1 && flag) {
                        start = next.getKey();
                        flag = false;
                    } else if (1 != next.getValue() && !flag) {
                        end = next.getKey() - 1;
                        flag = true;
                        sheet.addMergedRegion(new CellRangeAddress(start, end, colIndex, colIndex + 1));
                    }
                    lastRowIndex = next.getKey();
                }
                if (!flag) {
                    end = lastRowIndex;
                    sheet.addMergedRegion(new CellRangeAddress(start, end, colIndex, colIndex + 1));
                }
            }
        }
    }


    private static boolean isSame(Object previous, Object current) {
        if (null == previous && null == current) {
            return true;

        }
        if (null == previous) {
            return StringUtils.isBlank(current.toString());
        }

        if (null == current) {
            return StringUtils.isBlank(previous.toString());
        }
        return previous.toString().equals(current.toString());
    }

    public static void test(List<?> list) {
        // previous value
        Object previous = list.get(0);

        // if the repeat is interrupted
        boolean isInterrupted = true;
        List<Pair<Integer, Integer>> range = new ArrayList<>();
        //start index
        int start = -1;
        //end index
        int end;
        for (int i = 1; i < list.size(); i++) {
            if (isSame(previous, list.get(i)) && isInterrupted) {
                start = i - 1;
                isInterrupted = false;
            } else if (!isSame(previous, list.get(i)) && !isInterrupted) {
                end = i - 1;
                isInterrupted = true;
                range.add(Pair.of(start, end));
            }
            //the last node
            if (i == list.size() - 1 && isSame(previous, list.get(i)) && !isInterrupted) {
                end = i;
                range.add(Pair.of(start, end));
                break;
            }
            previous = list.get(i);
        }
        System.out.println(range);

    }

    public static void main(String[] args) {
        int[] a = new int[]{1, 1, 2, 3, 3, 3, 4, 5, 5, 7, 8, 8, 8};
        List<Integer> list = Arrays.stream(a).boxed().collect(Collectors.toList());
        test(list);
    }


    /**
     * after
     */
    @Override
    public abstract void afterCallback();

    @Override
    public Workbook execute() {
        logger.debug("start write!");
        beforeCallback();

        final long startTimeMillis = System.currentTimeMillis();
        this.initData();

        logger.debug("finish write![total cost {}ms]", (System.currentTimeMillis() - startTimeMillis));
        afterCallback();
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
                ExcelDataValidationConstraint<Annotation> dataValidationConstraint = ReflectUtils.newInstance(dataValidation.dataConstraint());
                dataValidationConstraint.initialize(annotation);
                return dataValidationConstraint.generate();
            }
        }
        return null;
    }

    /**
     * get sheet by sheetIndex
     * if cause error then create new sheet by the sheetIndex
     *
     * @return current sheet
     */
    private SXSSFSheet getSheet(String sheetName, String sheetIdentity) {
        if (sheetNameMap.containsKey(sheetName)) {
            try {
                return sxssfWorkbook.getSheetAt(sheetNameMap.get(sheetName) - 1);
            } catch (Exception e) {
                return createSheet(sheetName, sheetIdentity);
            }
        }
        return createSheet(sheetName, sheetIdentity);
    }

    /**
     * create new sheet
     *
     * @return sheet
     */
    private SXSSFSheet createSheet(String sheetName, String sheetIdentity) {
        sheetNameMap.put(sheetName, existSheetIndex.incrementAndGet());
        SXSSFSheet sheet = sxssfWorkbook.createSheet(existSheetIndex.get() + "_" + sheetName);
        this.handleComplexHeader(sheet, sheetIdentity);
        this.addDataValidation(sheet, sheetIdentity);
        this.initHeadTitle(sheet, sheetIdentity);
        this.curSheetIndex = existSheetIndex.get();
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
                if (CellStyleBuilder.class.isAssignableFrom(clazz)) {
                    cellStyleBuilder = (CellStyleBuilder) ReflectUtils.newInstance(clazz);
                } else {
                    throw new ExcelAnnotationWriterException("CellStyle [" + clazz + "] not assignable from CellStyleBuilder.class");
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

    @SuppressWarnings("unchecked")
    private List<AbstractExcelColumnConverter<Annotation, ?>> findColumnConverter(final Field field) {
        List<AbstractExcelColumnConverter<Annotation, ?>> columnConverters = new ArrayList<>();
        final Annotation[] annotations = field.getAnnotations();
        for (final Annotation annotation : annotations) {
            final Class<? extends Annotation> aClass = annotation.annotationType();
            if (aClass.isAnnotationPresent(ExcelConverter.class)) {
                final ExcelConverter excelConverter = aClass.getAnnotation(ExcelConverter.class);
                AbstractExcelColumnConverter<Annotation, ?> columnConverter = ReflectUtils.newInstance(excelConverter.convertBy());
                columnConverter.initialize(annotation);
                columnConverters.add(columnConverter);
            }
        }

        if (columnConverters.size() == 0) {
            columnConverters = Collections.singletonList(new ExcelDefaultConverter());
        }

        return columnConverters;
    }
}
