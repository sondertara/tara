package com.sondertara.excel.factory;

import com.sondertara.excel.entity.ExcelCellEntity;
import com.sondertara.excel.entity.ExcelReadSheetEntity;
import com.sondertara.excel.entity.ExcelWriteSheetEntity;
import com.sondertara.excel.exception.ExcelTaraException;
import com.sondertara.excel.executor.CellStyleCache;
import com.sondertara.excel.meta.annotation.ExcelExport;
import com.sondertara.excel.meta.annotation.ExcelExportField;
import com.sondertara.excel.meta.annotation.ExcelImportField;
import com.sondertara.excel.meta.annotation.validation.ExcelRangeRule;
import com.sondertara.excel.meta.annotation.validation.ExcelRegexValue;
import com.sondertara.excel.meta.style.CellStyleBuilder;
import com.sondertara.excel.support.validator.ValueRangeValidator;
import com.sondertara.excel.utils.ExcelAnnotationUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * @author huangxiaohu
 */
public class ExcelMappingFactory {

    /**
     * 根据指定Excel实体获取导入Excel文件相关信息
     *
     * @param clazz
     * @return excel 属性
     */
    public static ExcelReadSheetEntity loadImportExcelClass(Class<?> clazz) {
        List<ExcelCellEntity> propertyList = new ArrayList<>();
        ExcelReadSheetEntity excelMapping = new ExcelReadSheetEntity();

        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            ExcelImportField excelImportField = field.getAnnotation(ExcelImportField.class);
            if (null != excelImportField) {
                field.setAccessible(true);
                ExcelCellEntity excelPropertyEntity = ExcelCellEntity.builder().fieldEntity(field)
                        .index(excelImportField.colIndex() - 1).required(!excelImportField.allowBlank()).build();

                ExcelRegexValue regexValue = field.getAnnotation(ExcelRegexValue.class);
                if (null!=regexValue){
                    excelPropertyEntity.setRegex(regexValue.regex());
                    excelPropertyEntity.setRegexMessage(regexValue.message());
                }

                ExcelRangeRule rangeRule = field.getAnnotation(ExcelRangeRule.class);
                if (null!=rangeRule){
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

        ExcelWriteSheetEntity excelMapping = new ExcelWriteSheetEntity();
        ExcelExport annotation = clazz.getAnnotation(ExcelExport.class);
        if (annotation != null) {
            String s = annotation.sheetName();
            excelMapping.setSheetName(s);
        } else {
            excelMapping.setSheetName("Sheet");

        }
        List<ExcelCellEntity> propertyList = new ArrayList<>();

        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            ExcelExportField excelExportField = field.getAnnotation(ExcelExportField.class);
            if (null != excelExportField) {
                field.setAccessible(true);
                Class<?> styleBuilder = excelExportField.dataCellStyleBuilder();
                Class<?> headerStyleClass = excelExportField.titleCellStyleBuilder();

                CellStyleBuilder dataStyle = CellStyleCache.getInstance().getCellStyleInstance(styleBuilder);
                CellStyleBuilder headerStyle = CellStyleCache.getInstance().getCellStyleInstance(headerStyleClass);
                ExcelCellEntity excelPropertyEntity = ExcelCellEntity.builder().fieldEntity(field)
                        .index(excelExportField.colIndex()).cellType(excelExportField.cellType())
                        .dateFormat(excelExportField.dataFormat()).dataStyle(dataStyle).headStyle(headerStyle)
                        .authWith(excelExportField.autoWidth()).colWidth(excelExportField.colWidth())
                        .columnName(ExcelAnnotationUtils.getColName(excelExportField))
                        .defaultValue(excelExportField.defaultCellValue().trim()).build();
                propertyList.add(excelPropertyEntity);
            }
        }
        if (propertyList.isEmpty()) {
            throw new ExcelTaraException("[{}]类未找到标注@ExcelExportField注解的属性!", clazz.getName());
        }

        excelMapping.setPropertyList(propertyList);
        return excelMapping;
    }

}
