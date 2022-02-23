package com.sondertara.excel.factory;

import com.sondertara.excel.annotation.ExcelExport;
import com.sondertara.excel.annotation.ExcelImportFiled;
import com.sondertara.excel.annotation.ExcelExportField;
import com.sondertara.excel.entity.ExcelEntity;
import com.sondertara.excel.entity.ExcelPropertyEntity;
import com.sondertara.excel.exception.ExcelTaraException;

import java.lang.annotation.Annotation;
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
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public static ExcelEntity loadImportExcelClass(Class<?> clazz) {
        List<ExcelPropertyEntity> propertyList = new ArrayList<ExcelPropertyEntity>();

        ExcelExport annotation = clazz.getAnnotation(ExcelExport.class);
        if (annotation != null) {
            String s = annotation.sheetName();
        }

        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            ExcelExportField excelExportField = field.getAnnotation(ExcelExportField.class);
            if (null != excelExportField) {
                field.setAccessible(true);
                ExcelPropertyEntity excelPropertyEntity = ExcelPropertyEntity.builder().fieldEntity(field).index(excelExportField.index() - 1).required(excelExportField.required()).dateFormat(excelExportField.dateFormat().trim()).regex(excelExportField.regex().trim()).regexMessage(excelExportField.regexMessage().trim()).scale(excelExportField.scale()).roundingMode(excelExportField.roundingMode()).range(excelExportField.range()).rangeType(excelExportField.rangeType()).build();
                propertyList.add(excelPropertyEntity);
            }
        }
        if (propertyList.isEmpty()) {
            throw new ExcelTaraException("[{}] 类未找到标注@ImportField注解的属性!", clazz.getName());
        }
        ExcelEntity excelMapping = new ExcelEntity();
        excelMapping.setPropertyList(propertyList);
        return excelMapping;

    }

    /**
     * 根据指定Excel实体获取导出Excel文件相关信息
     *
     * @param clazz class
     * @return excel属性
     */
    public static ExcelEntity loadExportExcelClass(Class<?> clazz) {
        List<ExcelPropertyEntity> propertyList = new ArrayList<ExcelPropertyEntity>();

        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            ExcelImportFiled excelImportFiled = field.getAnnotation(ExcelImportFiled.class);
            if (null != excelImportFiled) {
                field.setAccessible(true);
                ExcelPropertyEntity excelPropertyEntity = ExcelPropertyEntity.builder().fieldEntity(field).columnName(excelImportFiled.columnName().trim()).scale(excelImportFiled.scale()).roundingMode(excelImportFiled.roundingMode()).dateFormat(excelImportFiled.dateFormat().trim()).templateCellValue(excelImportFiled.defaultCellValue().trim()).build();
                propertyList.add(excelPropertyEntity);
            }
        }
        if (propertyList.isEmpty()) {
            throw new ExcelTaraException("[{}]类未找到标注@ExportField注解的属性!", clazz.getName());
        }
        ExcelEntity excelMapping = new ExcelEntity();
        excelMapping.setPropertyList(propertyList);
        return excelMapping;
    }

}
