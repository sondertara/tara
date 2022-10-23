package com.sondertara.excel.context;

import com.google.common.collect.Maps;
import com.sondertara.common.util.CollectionUtils;
import com.sondertara.excel.function.ExportFunction;
import com.sondertara.excel.meta.annotation.ExcelExport;
import com.sondertara.excel.meta.model.AnnotationExcelWriterSheetDefinition;
import com.sondertara.excel.meta.model.AnnotationSheet;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author huangxiaohu
 */
@Slf4j
public abstract class BaseAnnotationExcelWriterContext<T> implements ExcelRawWriterContext<T> {

    private final List<AnnotationSheet> sheetDefinitions;


    public BaseAnnotationExcelWriterContext() {


        this.sheetDefinitions = new ArrayList<>();

    }


    @Override
    public Map<String, AnnotationSheet> getSheetDefinitions() {

        Map<String, AnnotationSheet> map = Maps.newLinkedHashMap();
        this.sheetDefinitions.forEach(s -> {
            map.put(s.getMappingClass().getSimpleName(), s);

        });
        return map;
    }


    @Override
    public void addMapper(Class<?> excelClass, ExportFunction<?> function) {
        sheetDefinitions.add(new AnnotationExcelWriterSheetDefinition<>(excelClass, function));
        Collections.sort(sheetDefinitions);
    }

    @Override
    public void addData(List<?> dataList) {
        if (CollectionUtils.isEmpty(dataList)) {
            throw new IllegalArgumentException("collection size == 0");
        }
        sheetDefinitions.add(new AnnotationExcelWriterSheetDefinition<>(dataList.get(0).getClass(), dataList));
        Collections.sort(sheetDefinitions);
    }

    @Override
    public void addMapper(Class<?>... clazz) {
        for (Class<?> aClass : clazz) {
            sheetDefinitions.add(new AnnotationExcelWriterSheetDefinition<>(aClass, Collections.emptyList()));
        }
        Collections.sort(sheetDefinitions);
    }

    @Override
    public void removeSheet(int index) {
        sheetDefinitions.remove(index);
    }

    /**
     * 获取定义的SheetName
     *
     * @param sheetDefinition the sheet definition
     * @return the sheet name
     */
    private String getOriginalSheetName(AnnotationSheet sheetDefinition) {
        ExcelExport excelExport = sheetDefinition.getAnnotation(ExcelExport.class);
        return excelExport.sheetName();
    }

    /**
     * 获取泛型类型
     *
     * @param list
     * @return
     */
    public static Class<?> getGenericClass(List<?> list) {
        if (list.size() == 0) {
            throw new IllegalArgumentException("collection size == 0");
        }
        return list.get(0).getClass();
    }


}
