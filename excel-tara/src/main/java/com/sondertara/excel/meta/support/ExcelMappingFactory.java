package com.sondertara.excel.meta.support;

import com.sondertara.common.base.Assert;
import com.sondertara.common.collection.Maps;
import com.sondertara.common.reflect.ClassUtils;
import com.sondertara.common.reflect.ReflectUtils;
import com.sondertara.excel.entity.ExcelCellEntity;
import com.sondertara.excel.entity.ExcelReadSheetEntity;
import com.sondertara.excel.entity.ExcelWriteSheetEntity;
import com.sondertara.excel.enums.ExcelColBindType;
import com.sondertara.excel.exception.ExcelAnnotationWriterException;
import com.sondertara.excel.exception.ExcelTaraException;
import com.sondertara.excel.meta.annotation.CellRange;
import com.sondertara.excel.meta.annotation.ExcelComplexHeader;
import com.sondertara.excel.meta.annotation.ExcelExport;
import com.sondertara.excel.meta.annotation.ExcelExportField;
import com.sondertara.excel.meta.annotation.ExcelImportField;
import com.sondertara.excel.meta.annotation.validation.ExcelRangeRule;
import com.sondertara.excel.meta.annotation.validation.ExcelRegexValue;
import com.sondertara.excel.meta.style.CellStyleBuilder;
import com.sondertara.excel.meta.style.DefaultDataCellStyleBuilder;
import com.sondertara.excel.meta.style.DefaultTitleCellStyleBuilder;
import com.sondertara.excel.support.cache.CellStyleCache;
import com.sondertara.excel.support.validator.ValueRangeValidator;
import com.sondertara.excel.utils.ColorUtils;
import com.sondertara.excel.utils.ExcelAnnotationUtils;
import org.apache.poi.ss.usermodel.CellType;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author huangxiaohu
 */
public class ExcelMappingFactory {

    private static final Map<String, ExcelWriteSheetEntity> EXCEL_WRITE_SHEET_ENTITY_MAP = Maps.newConcurrentMap();

    private static final Map<String, ExcelColumnProvider> EXCEL_WRITE_ENTITY_PROVIDER_MAP = Maps.newConcurrentMap();
    private static final Map<String, ExcelColumnFilter> EXCEL_WRITE_ENTITY_FILTER_MAP = Maps.newConcurrentMap();

    /**
     * 根据指定Excel实体获取导入Excel文件相关信息
     *
     * @param clazz excel class
     * @return excel 属性
     */
    public static ExcelReadSheetEntity loadImportExcelClass(Class<?> clazz) {
        List<ExcelCellEntity> propertyList = new ArrayList<>();
        ExcelReadSheetEntity excelMapping = new ExcelReadSheetEntity();

       List<Field> fields = ClassUtils.getFields(clazz);
        for (Field field : fields) {
            ExcelImportField excelImportField = field.getAnnotation(ExcelImportField.class);
            if (null != excelImportField) {
                field.setAccessible(true);
                ExcelCellEntity excelPropertyEntity = ExcelCellEntity.builder().fieldEntity(field)
                        .index(excelImportField.colIndex() - 1).required(!excelImportField.allowBlank()).build();

                ExcelRegexValue regexValue = field.getAnnotation(ExcelRegexValue.class);
                if (null != regexValue) {
                    excelPropertyEntity.setRegex(regexValue.regex());
                    excelPropertyEntity.setRegexMessage(regexValue.message());
                }

                ExcelRangeRule rangeRule = field.getAnnotation(ExcelRangeRule.class);
                if (null != rangeRule) {
                    ValueRangeValidator validator = new ValueRangeValidator();
                    validator.initialize(rangeRule);
                    excelPropertyEntity.setRangeValidator(validator);
                }
                propertyList.add(excelPropertyEntity);
            }
        }
        if (propertyList.isEmpty()) {
            throw new ExcelTaraException("[{}] 类未找到标注@ImportField注解的属性!", clazz.getName());
        }

        excelMapping.setPropertyList(propertyList);
        return excelMapping;

    }

    /**
     * 根据指定Excel实体获取导出Excel文件相关信息
     *
     * @param clazz class
     * @return excel属性
     */
    public static ExcelWriteSheetEntity loadExportExcelClass(Class<?> clazz) {
        final String canonicalName = clazz.getCanonicalName();
        ExcelWriteSheetEntity sheetEntity = null;
        if (EXCEL_WRITE_ENTITY_PROVIDER_MAP.containsKey(canonicalName)) {
            sheetEntity = EXCEL_WRITE_ENTITY_PROVIDER_MAP.get(canonicalName).resolve(clazz);
            if (null == sheetEntity) {
                sheetEntity = EXCEL_WRITE_SHEET_ENTITY_MAP.get("Default/" + canonicalName);
            }
        } else {
            sheetEntity = EXCEL_WRITE_SHEET_ENTITY_MAP.computeIfAbsent(canonicalName, key -> {
                        ExcelExport annotation = clazz.getAnnotation(ExcelExport.class);
                        if (annotation == null) {
                            throw new IllegalArgumentException("No ExcelExport annotation found for class:" + canonicalName);
                        }
                        ExcelWriteSheetEntity entity = null;
                        if (!DefaultExcelColumnProvider.class.isAssignableFrom(annotation.provider())) {
                            ExcelColumnProvider columnProvider = ReflectUtils.newInstance(annotation.provider());
                            entity = columnProvider.resolve(clazz);
                            EXCEL_WRITE_ENTITY_PROVIDER_MAP.put(canonicalName, columnProvider);
                        }
                        if (null == entity) {
                            entity = resolveSheetEntity(clazz);
                            EXCEL_WRITE_SHEET_ENTITY_MAP.put("Default/" + canonicalName, entity);
                        }
                        return entity;
                    }
            );
        }
        if (null == sheetEntity) {
            sheetEntity = resolveSheetEntity(clazz);
        }
        Assert.notNull(sheetEntity, "No excel export config found for class:" + clazz.getCanonicalName());
        if (EXCEL_WRITE_ENTITY_FILTER_MAP.containsKey(canonicalName)) {
            sheetEntity.setPropertyList(filterCell(sheetEntity.getPropertyList(), EXCEL_WRITE_ENTITY_FILTER_MAP.get(canonicalName), sheetEntity.getBindType()));
        }
        return sheetEntity;
    }

    static ExcelWriteSheetEntity resolveSheetEntity(Class<?> clazz) {
        String canonicalName = clazz.getCanonicalName();
        ExcelExport annotation = clazz.getAnnotation(ExcelExport.class);
        if (annotation == null) {
            throw new IllegalArgumentException("No ExcelExport annotation found for class:" + canonicalName);
        }
        ExcelWriteSheetEntity.ExcelWriteSheetEntityBuilder builder = ExcelWriteSheetEntity.builder();
        ExcelColBindType bindType = annotation.bindType();
        builder.sheetName(annotation.sheetName())
                .order(annotation.order())
                .bindType(bindType)
                .hasTitle(annotation.hasTitle())
                .autoColWidth(annotation.autoWidth())
                .rowStriped(annotation.rowStriped())
                .rowStripeColor(ColorUtils.hexToRgb(annotation.rowStripeColor()))
                .maxRowsPerSheet(annotation.maxRowsPerSheet())
                .dataRowHeight(annotation.dataRowHeight())
                .maxColWidth(annotation.maxColWidth())
                .titleRowHeight(annotation.titleRowHeight());


        List<ExcelCellEntity> propertyList = new ArrayList<>();

        int defaultIndex = 0;
        int colIndex = 0;
        Set<Integer> colIndexSet = new HashSet<>();

        List<Field>fields = ClassUtils.getFields(clazz);
        //没有标注注解的属性
        long count = fields.stream().filter(field -> field.isAnnotationPresent(ExcelExportField.class)).count();
        if (count == 0) {

            for (Field field : fields) {
                colIndex = defaultIndex++;
                field.setAccessible(true);
                CellStyleBuilder dataStyle = CellStyleCache.getInstance().getCellStyleInstance(DefaultDataCellStyleBuilder.class);
                CellStyleBuilder headerStyle = CellStyleCache.getInstance().getCellStyleInstance(DefaultTitleCellStyleBuilder.class);
                ExcelCellEntity excelPropertyEntity = ExcelCellEntity.builder().fieldEntity(field)
                        .index(colIndex).cellType(CellType.STRING)
                        .dataStyle(dataStyle).headStyle(headerStyle)
                        .titles(new String[]{field.getName()})
                        .build();
                propertyList.add(excelPropertyEntity);
            }
        } else {
            for (Field field : fields) {
                ExcelExportField excelExportField = field.getAnnotation(ExcelExportField.class);
                if (null != excelExportField) {
                    if (bindType.equals(ExcelColBindType.COL_INDEX)) {
                        if (excelExportField.colIndex() < 1) {
                            throw new ExcelAnnotationWriterException("The @ExcelExportColumn on Field [" + field.getName() + "] of Class[" + canonicalName + "] miss \"colIndex\" attribute or less than 1 !");
                        }
                        // exists colIndex
                        if (colIndexSet.contains(excelExportField.colIndex())) {
                            throw new ExcelAnnotationWriterException("The @ExcelExportColumn on Field [" + field.getName() + "] of Class[" + canonicalName + "] has conflicting \"colIndex\" value => [" + excelExportField.colIndex() + "] !");
                        }
                        colIndex = excelExportField.colIndex() - 1;
                        colIndexSet.add(excelExportField.colIndex());

                    } else {
                        colIndex = defaultIndex++;
                    }

                    field.setAccessible(true);
                    Class<?> styleBuilder = excelExportField.dataCellStyleBuilder();
                    Class<?> headerStyleClass = excelExportField.titleCellStyleBuilder();

                    CellStyleBuilder dataStyle = CellStyleCache.getInstance().getCellStyleInstance(styleBuilder);
                    CellStyleBuilder headerStyle = CellStyleCache.getInstance().getCellStyleInstance(headerStyleClass);
                    ExcelCellEntity excelPropertyEntity = ExcelCellEntity.builder().fieldEntity(field)
                            .index(colIndex).cellType(excelExportField.cellType())
                            .dateFormat(excelExportField.dataFormat()).dataStyle(dataStyle).headStyle(headerStyle)
                            .authWith(excelExportField.autoWidth()).colWidth(excelExportField.colWidth())
                            .autoMerge(excelExportField.autoMerge())
                            .titles(ExcelAnnotationUtils.getColName(excelExportField))
                            .defaultValue(excelExportField.defaultCellValue().trim()).build();
                    propertyList.add(excelPropertyEntity);
                }
            }
        }

        if (propertyList.isEmpty()) {
            throw new ExcelTaraException("[{}]类未找到属性!", clazz.getName());
        }
        Class<? extends ExcelColumnFilter> colFilter = annotation.colFilter();
        List<ExcelCellEntity> cellEntityList;

        if (DefaultExcelColumnFilter.class.isAssignableFrom(colFilter)) {
            cellEntityList = propertyList;
        } else {
            ExcelColumnFilter columnFilter = ReflectUtils.newInstance(colFilter);
            EXCEL_WRITE_ENTITY_FILTER_MAP.put(canonicalName, columnFilter);
            cellEntityList = filterCell(propertyList, columnFilter, bindType);
        }
        return builder.lastColIndex(colIndex).propertyList(cellEntityList).build();
    }


    private static int getFirstDataRow(Class<?> clazz, ExcelExport annotation) {
        int firstDataRow = 1;
        if (annotation.hasTitle()) {

            ExcelComplexHeader complexHeader = clazz.getAnnotation(ExcelComplexHeader.class);
            if (complexHeader == null) {
                firstDataRow = 2;
            } else {
                CellRange[] cellRanges = complexHeader.value();
                int startRow = 1;
                for (CellRange cellRange : cellRanges) {
                    if (cellRange.lastRow() > startRow) {
                        startRow = cellRange.lastRow();
                    }
                }
                firstDataRow = startRow + 1;
            }
        }
        return firstDataRow;
    }

    private static List<ExcelCellEntity> filterCell(final List<ExcelCellEntity> source, ExcelColumnFilter columnFilter, ExcelColBindType bindType) {

        final List<String> apply = columnFilter.apply(source.stream().map(ExcelCellEntity::getFieldName).collect(Collectors.toList()));

        List<ExcelCellEntity> target = new ArrayList<>();
        int index = 1;
        for (ExcelCellEntity cellEntity : source) {
            if (apply.contains(cellEntity.getFieldName())) {
                if (ExcelColBindType.DEF_ORDER.equals(bindType)) {
                    cellEntity.setColIndex(index++);
                }
                target.add(cellEntity);
            }
        }
        return target;
    }
}
