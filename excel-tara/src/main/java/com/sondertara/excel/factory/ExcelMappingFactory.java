
package com.sondertara.excel.factory;

import com.sondertara.excel.annotation.ExportField;
import com.sondertara.excel.annotation.ImportField;
import com.sondertara.excel.entity.ExcelEntity;
import com.sondertara.excel.entity.ExcelPropertyEntity;
import com.sondertara.excel.exception.ExcelTaraException;

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
    public static ExcelEntity loadImportExcelClass(Class clazz) {
        List<ExcelPropertyEntity> propertyList = new ArrayList<ExcelPropertyEntity>();

        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            ImportField importField = field.getAnnotation(ImportField.class);
            if (null != importField) {
                field.setAccessible(true);
                ExcelPropertyEntity excelPropertyEntity = ExcelPropertyEntity.builder()
                        .fieldEntity(field)
                        .index(importField.index() - 1)
                        .required(importField.required())
                        .dateFormat(importField.dateFormat().trim())
                        .regex(importField.regex().trim())
                        .regexMessage(importField.regexMessage().trim())
                        .scale(importField.scale())
                        .roundingMode(importField.roundingMode())
                        .range(importField.range())
                        .rangeType(importField.rangeType())
                        .build();
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
     * @param clazz
     * @return excel属性
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public static ExcelEntity loadExportExcelClass(Class<?> clazz, String fileName) {
        List<ExcelPropertyEntity> propertyList = new ArrayList<ExcelPropertyEntity>();

        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            ExportField exportField = field.getAnnotation(ExportField.class);
            if (null != exportField) {
                field.setAccessible(true);
                ExcelPropertyEntity excelPropertyEntity = ExcelPropertyEntity.builder()
                        .fieldEntity(field)
                        .columnName(exportField.columnName().trim())
                        .scale(exportField.scale())
                        .roundingMode(exportField.roundingMode())
                        .dateFormat(exportField.dateFormat().trim())
                        .templateCellValue(exportField.defaultCellValue().trim())
                        .build();
                propertyList.add(excelPropertyEntity);
            }
        }
        if (propertyList.isEmpty()) {
            throw new ExcelTaraException("[{}]类未找到标注@ExportField注解的属性!", clazz.getName());
        }
        ExcelEntity excelMapping = new ExcelEntity();
        excelMapping.setPropertyList(propertyList);
        excelMapping.setFileName(fileName);
        return excelMapping;
    }

}
