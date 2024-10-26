package com.sondertara.excel.executor;

import com.sondertara.common.base.Valid;
import com.sondertara.common.collection.CollectionUtils;
import com.sondertara.common.collection.Lists;
import com.sondertara.common.collection.Maps;
import com.sondertara.common.reflect.ReflectUtils;
import com.sondertara.common.struct.Pair;
import com.sondertara.common.text.StringUtils;
import com.sondertara.excel.antlr.tablemodel.MergedRegion;
import com.sondertara.excel.common.constants.ExcelExportConstants;
import com.sondertara.excel.context.ExcelRawWriterContext;
import com.sondertara.excel.entity.ExcelCellEntity;
import com.sondertara.excel.entity.ExcelWriteSheetEntity;
import com.sondertara.excel.exception.ExcelAnnotationWriterException;
import com.sondertara.excel.exception.ExcelConvertException;
import com.sondertara.excel.exception.ExcelWriterException;
import com.sondertara.excel.function.ExportFunction;
import com.sondertara.excel.lifecycle.ExcelWriterLifecycle;
import com.sondertara.excel.meta.annotation.ExcelComplexHeader;
import com.sondertara.excel.meta.annotation.ExcelDataFormat;
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
import com.sondertara.excel.task.AbstractExcelGenerateTask;
import com.sondertara.excel.task.PageResultWrapper;
import com.sondertara.excel.utils.ExcelFieldUtils;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.DataValidationConstraint;
import org.apache.poi.ss.usermodel.DataValidationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author huangxiaohu
 */
public abstract class AbstractExcelWriterExecutor implements TaraExcelExecutor, ExcelWriterLifecycle<SXSSFSheet> {

    private static final Logger logger = LoggerFactory.getLogger(AbstractExcelWriterExecutor.class);
    private final ExcelRawWriterContext<SXSSFWorkbook> writerContext;
    private final CellStyleCache cellStyleCache;
    private final SXSSFWorkbook sxssfWorkbook;

    private final AtomicInteger existSheetIndex = new AtomicInteger(0);

    private final ConcurrentHashMap<String, AtomicInteger> sheetNameMap = new ConcurrentHashMap<>();
    /**
     * the current row number (1 based)
     */
    protected int curRowIndex;
    protected int curSheetIndex;
    /**
     * the current cell number (1 based)
     */
    protected int curColIndex;


    public AbstractExcelWriterExecutor(final SXSSFWorkbook sxssfWorkbook, final ExcelRawWriterContext<SXSSFWorkbook> writerContext) {
        this.sxssfWorkbook = sxssfWorkbook;
        this.sxssfWorkbook.setCompressTempFiles(true);
        this.writerContext = writerContext;
        this.cellStyleCache = new CellStyleCache();
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

    /**
     * before
     */
    @Override
    public abstract void beforeCallback();

    @Override
    public void handleComplexHeader(SXSSFSheet sheet, String sheetIdentity) {
        AnnotationExcelWriterSheetDefinition<?> sheetDefinition = (AnnotationExcelWriterSheetDefinition<?>) this.writerContext.getSheetDefinitions().get(sheetIdentity);
        final ExcelComplexHeader excelComplexHeader = sheetDefinition.getAnnotation(ExcelComplexHeader.class);

        int maxLastRow = 0;
        if (excelComplexHeader != null) {
            com.sondertara.excel.meta.annotation.CellRange[] cellRanges = excelComplexHeader.value();
            for (final com.sondertara.excel.meta.annotation.CellRange cellRange : cellRanges) {
                this.curRowIndex = cellRange.firstRow();
                this.curColIndex = cellRange.firstCol();
                final int firstRow = cellRange.firstRow() - 1;
                final int firstCol = cellRange.firstCol() - 1;
                final int lastRow = cellRange.lastRow() - 1;
                final int lastCol = cellRange.lastCol() - 1;
                Valid.isTrue(firstRow >= 0 && firstCol >= 0, "firstRow and firstCol must be greater than or equal to 0");
                Valid.isTrue(lastRow >= 0 && lastCol >= 0, "lastRow and lastCol must be greater than or equal to 0");
                if (lastRow > maxLastRow) {
                    maxLastRow = lastRow;
                }

                Row row = sheet.getRow(firstRow);
                if (row == null) {
                    row = sheet.createRow(firstRow);
                }
                row.setHeightInPoints(cellRange.height());

                final Cell cell = row.createCell(firstCol);
                cell.setCellValue(cellRange.title());

                // 设置样式
                final CellStyleBuilder cellStyleBuilder = this.cellStyleCache.getCellStyleInstance(cellRange.cellStyleBuilder());
                cell.setCellStyle(cellStyleBuilder.build(this.sxssfWorkbook, new ExcelCellStyleDefinition(this.sxssfWorkbook), cell));

                // 合并单元格
                final CellRangeAddress cellRangeAddress = new CellRangeAddress(firstRow, lastRow, firstCol, lastCol);
                sheet.addMergedRegion(cellRangeAddress);
                RegionUtil.setBorderBottom(BorderStyle.THIN, cellRangeAddress, sheet);
                RegionUtil.setBorderRight(BorderStyle.THIN, cellRangeAddress, sheet);
                RegionUtil.setBorderLeft(BorderStyle.THIN, cellRangeAddress, sheet);
            }
        }

        int firstDataRow = sheetDefinition.getFirstDataRow();
        sheetDefinition.setFirstDataRow(maxLastRow + firstDataRow);

    }


    @Override
    public void addDataValidation(SXSSFSheet sheet, String sheetIdentity) {
        AnnotationExcelWriterSheetDefinition<?> sheetDefinition = (AnnotationExcelWriterSheetDefinition<?>) this.writerContext.getSheetDefinitions().get(sheetIdentity);

        final Map<Integer, Field> columnFields = sheetDefinition.getColFields();
        for (final Map.Entry<Integer, Field> columnFieldEntry : columnFields.entrySet()) {
            final Field field = columnFieldEntry.getValue();
            final int colIndex = this.curColIndex = columnFieldEntry.getKey();

            if (colIndex < 0) {
                throw new IllegalArgumentException(field.getName() + "' colIndex less than 0");
            }

            final String[] dataValidationConstraintList = getDataValidationConstraint(field);
            if (dataValidationConstraintList != null) {

                final DataValidationHelper helper = sheet.getDataValidationHelper();
                // 加载下拉列表内容
                final DataValidationConstraint dataConstraint = helper.createExplicitListConstraint(dataValidationConstraintList);
                dataConstraint.setExplicitListValues(dataValidationConstraintList);
                final CellRangeAddressList regions = new CellRangeAddressList(sheetDefinition.getFirstDataRow(), ExcelExportConstants.MAX_PER_SHEET_COUNT, colIndex, colIndex);

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
        if (!sheetDefinition.isHasTitle()) {
            return;
        }

        ExcelWriteSheetEntity sheetMeta = sheetDefinition.getSheetMeta();
        List<ExcelCellEntity> propertyList = sheetMeta.getPropertyList();
        List<MergedRegion> cellRangeList = new ArrayList<MergedRegion>();
        Set<String> alreadyRangeSet = new HashSet<>();
        int titleRowCount = propertyList.stream().max(Comparator.comparingInt(o -> o.getTitles().size())).orElseThrow(() -> new RuntimeException("ExcelCellEntity is empty")).getTitles().size();

        List<ExcelCellEntity> entities = propertyList.stream().peek(p -> {
            int size = p.getTitles().size();
            if (size < titleRowCount) {
                String lastTitle = p.getTitle(size - 1);
                for (int i = 0; i < titleRowCount - size; i++) {
                    p.getTitles().add(lastTitle);
                }
            }
        }).collect(Collectors.toList());

        int lastRowNum = sheet.getLastRowNum();
        int startRowIndex = 0;
        if (lastRowNum >= 0) {
            startRowIndex = Math.max(sheetDefinition.getFirstDataRow(), sheet.getLastRowNum());
        }
        for (int i = 0; i < titleRowCount; i++) {
            sheet.createRow(startRowIndex + i);
        }

        for (int i = 0; i < entities.size(); i++) {
            ExcelCellEntity head = entities.get(i);
            List<String> headNameList = head.getTitles();

            int headRowCount = headNameList.size();
            for (int j = 0; j < headRowCount; j++) {
                SXSSFCell cell = sheet.getRow(j + startRowIndex).createCell(head.getColIndex());
                cell.setCellValue(headNameList.get(j));
                // 设置标题样式
                final CellStyleBuilder cellStyleBuilder = head.getHeadStyle();
                final CellStyle cellStyle = cellStyleBuilder.build(this.sxssfWorkbook, new ExcelCellStyleDefinition(this.sxssfWorkbook), cell);
                cell.setCellStyle(cellStyle);
                if (alreadyRangeSet.contains(i + "-" + j)) {
                    continue;
                }
                alreadyRangeSet.add(i + "-" + j);
                String headName = headNameList.get(j);
                int lastCol = i;
                int lastRow = j;
                for (int k = i + 1; k < entities.size(); k++) {
                    String key = k + "-" + j;
                    if (Objects.equals(entities.get(k).getTitle(j), headName) && !alreadyRangeSet.contains(key)) {
                        alreadyRangeSet.add(key);
                        lastCol = k;
                    } else {
                        break;
                    }
                }
                Set<String> tempAlreadyRangeSet = new HashSet<>();
                outer:
                for (int k = j + 1; k < headRowCount; k++) {
                    for (int l = i; l <= lastCol; l++) {
                        String key = l + "-" + k;
                        if (Objects.equals(entities.get(l).getTitle(k), headName) && !alreadyRangeSet.contains(key)) {
                            tempAlreadyRangeSet.add(l + "-" + k);
                        } else {
                            break outer;
                        }
                    }
                    lastRow = k;
                    alreadyRangeSet.addAll(tempAlreadyRangeSet);
                }
                if (j == lastRow && i == lastCol) {
                    continue;
                }
                cellRangeList
                        .add(new MergedRegion(j + startRowIndex, startRowIndex + lastRow, head.getColIndex(), entities.get(lastCol).getColIndex()));
            }
        }

        for (MergedRegion range : cellRangeList) {
            CellRangeAddress cellAddresses = new CellRangeAddress(range.getFirstRow(), range.getLastRow(), range.getFirstCol(), range.getLastCol());
            sheet.addMergedRegion(cellAddresses);

        }

        sheetDefinition.setFirstDataRow(startRowIndex + titleRowCount + 1);
        System.out.println(cellRangeList);
    }

    @Override
    public void initData() {
        for (Map.Entry<String, ? extends TaraSheet> entry : this.writerContext.getSheetDefinitions().entrySet()) {
            String sheetIdentity = entry.getKey();
            AnnotationExcelWriterSheetDefinition<?> sheetDefinition = (AnnotationExcelWriterSheetDefinition<?>) entry.getValue();
            Map<Integer, Object> lastRowData = Maps.newConcurrentMap();
            int maxRowsPerSheet = sheetDefinition.maxRowsPerSheet();
            switch (sheetDefinition.getExcelDataType()) {
                case DIRECT:
                    if (sheetDefinition.getRows().isEmpty()) {
                        createSheet(sheetDefinition.getName(), sheetIdentity);
                        break;
                    }
                    List<Object> list = sheetDefinition.getRows().stream().map(TaraRow::getRowData).collect(Collectors.toList());
                    List<List<Object>> lists = Lists.partition(list, maxRowsPerSheet);
                    for (List<Object> objects : lists) {
                        SXSSFSheet sxssfSheet = createSheet(sheetDefinition.getName(), sheetIdentity);
                        int max = Math.max(sheetDefinition.getFirstDataRow() - 1, sxssfSheet.getLastRowNum() + 1);
                        lastRowData = new LinkedHashMap<>();
                        createBody(sxssfSheet, sheetDefinition, objects, lastRowData, max);
                    }
                    break;
                case QUERY:

                    Map<Integer, Object> lastRowData1 = Maps.newConcurrentMap();
                    ExportFunction<?> queryFunction = sheetDefinition.getQueryFunction();

                    new BeanExcelGeneTask<>(queryFunction, data -> {
                        List<Object> existData = new ArrayList<>();
                        SXSSFSheet existSheet = getSheet(sheetDefinition.getName(), sheetIdentity);
                        int rowDataCount = Math.max(0, existSheet.getLastRowNum() - sheetDefinition.getFirstDataRow());
                        int remainSize = Math.min(data.size(), maxRowsPerSheet - rowDataCount);
                        for (int i = 0; i < remainSize; i++) {
                            existData.add(data.get(i));
                        }
                        //从0开始
                        int startIndex = Math.max(sheetDefinition.getFirstDataRow() - 1, existSheet.getLastRowNum() + 1);

                        createBody(existSheet, sheetDefinition, existData, lastRowData1, startIndex);
                        List<?> objects = data.subList(remainSize, data.size());
                        if (!objects.isEmpty()) {
                            List<? extends List<?>> partition = Lists.partition(objects, maxRowsPerSheet);
                            for (List<?> sheetData : partition) {
                                SXSSFSheet newSheet = createSheet(sheetDefinition.getName(), sheetIdentity);
                                //从0开始
                                lastRowData1.clear();
                                startIndex = Math.max(sheetDefinition.getFirstDataRow() - 1, newSheet.getLastRowNum() + 1);
                                createBody(newSheet, sheetDefinition, sheetData, lastRowData1, startIndex);
                            }
                        }
                    }).start();
                    break;
                default:
            }
        }

    }

    private void createRow(Map<Integer, Object> previous, Row row, int dataRowIndex, Object rowData, AnnotationExcelWriterSheetDefinition<?> sheetDefinition, int[][] mergeIndex) {
        //the row number (0 based)
        int rowNum = row.getRowNum();
        this.curRowIndex = rowNum + 1;
        Map<Integer, Object> data = new LinkedHashMap<>();
        ExcelWriteSheetEntity sheetMeta = sheetDefinition.getSheetMeta();
        Map<Integer, ExcelCellStyleDefinition> columnCellStyles = sheetDefinition.getColumnCellStyles(sxssfWorkbook);
        Class<?> mappingClass = sheetDefinition.getMappingClass();
        for (ExcelCellEntity cellEntity : sheetMeta.getPropertyList()) {
            //colIndex start 0
            Integer colIndex = cellEntity.getColIndex();
            this.curColIndex = colIndex;
            final Field field = cellEntity.getFieldEntity();
            final Cell cell = row.createCell(colIndex);

            ExcelCellStyleDefinition cellStyleDefinition;
            ExcelDefaultWriterResolver resolver = new ExcelDefaultWriterResolver(sheetMeta.getMaxColWidth());

            if (sheetMeta.isRowStriped()) {
                if (dataRowIndex % 2 == 0) {
                    cellStyleDefinition = columnCellStyles.get(colIndex * 2 - 1);
                } else {
                    cellStyleDefinition = columnCellStyles.get(colIndex * 2);
                }
            } else {
                cellStyleDefinition = columnCellStyles.get(colIndex);
            }
            CellStyle cellStyle;

            // 设置数据样式
            final CellStyleBuilder cellStyleBuilder = cellEntity.getDataStyle();
            cellStyle = cellStyleBuilder.build(this.sxssfWorkbook, cellStyleDefinition, cell);

            // 设置数据格式
            final ExcelDataFormat excelDataFormat = cellEntity.getDateFormat();
            if (null != excelDataFormat && StringUtils.isNotBlank(excelDataFormat.value())) {
                final DataFormat dataFormat = this.sxssfWorkbook.createDataFormat();
                cellStyle.setDataFormat(dataFormat.getFormat(excelDataFormat.value()));
            }
            cell.setCellStyle(cellStyle);

            // 值转换
            List<AbstractExcelColumnConverter<Annotation, ?>> columnConverters = ExcelExportConstants.getColConverterCache().get(mappingClass.getName() + "#" + field.getName());
            if (columnConverters == null) {
                columnConverters = findColumnConverter(field);
                ExcelExportConstants.getColConverterCache().put(mappingClass.getName() + "#" + field.getName(), columnConverters);
            }
            Object value;
            try {
                value = field.get(rowData);
                if (null == value && StringUtils.isNotBlank(cellEntity.getDefaultValue())) {
                    value = cellEntity.getDefaultValue();
                }
            } catch (IllegalAccessException e) {
                throw new ExcelWriterException(e);
            }
            try {
                for (final AbstractExcelColumnConverter<Annotation, ?> columnConverter : columnConverters) {
                    value = columnConverter.convert(value);
                }
            } catch (ExcelConvertException e) {
                throw new ExcelWriterException("Excel value convert failed for property[{}],{}", field.getName(), e.getMessage(), e.getCause());
            }

            try {
                ExcelFieldUtils.setCellValue(cell, value, field, cellEntity, resolver);
                boolean autoMerge = cellEntity.isAutoMerge();
                if (autoMerge && isSame(previous.get(curColIndex), value)) {
                    //set current and previous to merge flag
                    mergeIndex[colIndex][rowNum - 1] = 1;
                    mergeIndex[colIndex][rowNum] = 1;
                }
                //如果为空，则给默认空字符串 ，否则 ConcurrentHashMap 会报NPE
                data.put(curColIndex, Optional.ofNullable(value).orElse(""));
            } catch (final IllegalAccessException e) {
                throw new ExcelWriterException(e);
            }
        }
        previous.clear();
        previous.putAll(data);
    }

    private void createBody(SXSSFSheet sheet, AnnotationExcelWriterSheetDefinition<?> sheetDefinition, List<?> rows, Map<Integer, Object> previousRowData, int startRowIndex) {
        if (CollectionUtils.isEmpty(rows)) {
            return;
        }

        ExcelWriteSheetEntity sheetMeta = sheetDefinition.getSheetMeta();
        ExcelDefaultWriterResolver resolver = new ExcelDefaultWriterResolver(sheetMeta.getMaxColWidth());
        int maxColIndex = sheetMeta.getLastColIndex();
        int dataRowHeight = sheetMeta.getDataRowHeight();
        //  colIndex ,dataRowIndex is sheet row count+1
        int[][] mergeIndex = new int[maxColIndex][rows.size() + startRowIndex + 1];
        for (int rowIndex = 0; rowIndex < rows.size(); rowIndex++) {
            final Row row = sheet.createRow(rowIndex + startRowIndex);
            row.setHeightInPoints(dataRowHeight);
            Object rowData = rows.get(rowIndex);
            createRow(previousRowData, row, rowIndex + startRowIndex, rowData, sheetDefinition, mergeIndex);
        }
        // 设置列自动大小和自动合并
        sheet.trackAllColumnsForAutoSizing();
        for (ExcelCellEntity cellEntity : sheetMeta.getPropertyList()) {
            int cellIndex = cellEntity.getColIndex();
            if (sheetMeta.isAutoColWidth() || cellEntity.isAuthWith()) {
                resolver.adjustColWidth(sheet, cellIndex);
            }
            //如果自动合并
            if (cellEntity.isAutoMerge()) {
                //起始坐标
                int start = 0;
                //终止坐标
                int end;
                //连续相等是否中断，默认中断
                boolean flag = true;
                //取出colIndex一列所有标记的数据
                int[] rowIndex = mergeIndex[cellIndex];
                //最后一行
                int lastRowIndex = -1;


                for (int i = 1; i < rowIndex.length; i++) {
                    boolean prevMerge = rowIndex[i - 1] == 1;
                    boolean curMerge = rowIndex[i] == 1;
                    //如果和前一行都为1，连续相等中断，设置起始节点
                    if (curMerge && prevMerge && flag) {
                        start = i - 1;
                        //设置连续相等未中断
                        flag = false;
                    } else if (prevMerge && !curMerge && !flag) {
                        //设置终止坐标
                        end = i - 1;
                        //设置连续相等中断标志位
                        flag = true;
                        //合并单元格
                        if (start < end) {
                            sheet.addMergedRegion(new CellRangeAddress(start, end, cellIndex, cellIndex));
                        }

                    }
                    //最后一行坐标
                    lastRowIndex = i;
                }
                //处理遍历到最后一行时，连续相等未中断
                if (!flag) {
                    end = lastRowIndex;
                    sheet.addMergedRegion(new CellRangeAddress(start, end, cellIndex, cellIndex));
                }
            }
        }
    }

    /**
     * after
     */
    @Override
    public abstract void afterCallback();

    @Override
    public void execute() {
        beforeCallback();
        this.initData();
        afterCallback();
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
                //index is from 0
                return sxssfWorkbook.getSheetAt(Math.max(0, sheetNameMap.get(sheetName).get() - 1));
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

        AtomicInteger index = sheetNameMap.computeIfAbsent(sheetName, key -> new AtomicInteger(0));
        int sheetIndex = index.incrementAndGet();
        SXSSFSheet sheet = sxssfWorkbook.createSheet(sheetIndex > 1 ? sheetIndex + "_" + sheetName : sheetName);
        this.handleComplexHeader(sheet, sheetIdentity);
        this.initHeadTitle(sheet, sheetIdentity);
        this.addDataValidation(sheet, sheetIdentity);
        this.curSheetIndex = existSheetIndex.incrementAndGet();
        return sheet;
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

        if (columnConverters.isEmpty()) {
            columnConverters = Collections.singletonList(new ExcelDefaultConverter());
        }

        return columnConverters;
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

    static class BeanExcelGeneTask<R> extends AbstractExcelGenerateTask<R> {
        private final Consumer<List<R>> consumer;

        public BeanExcelGeneTask(ExportFunction<R> exportFunction, Consumer<List<R>> consumer) {
            super(exportFunction);
            this.consumer = consumer;
        }


        @Override
        protected void consumeData(@NonNull PageResultWrapper<R> data) {
            consumer.accept(data.getRaw().getData());
        }
    }

}
