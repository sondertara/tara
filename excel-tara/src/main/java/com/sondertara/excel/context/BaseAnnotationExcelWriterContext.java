package com.sondertara.excel.context;

import com.sondertara.excel.function.ExportFunction;
import com.sondertara.excel.meta.annotation.ExcelExport;
import com.sondertara.excel.meta.model.AnnotationExcelWriterSheetDefinition;
import com.sondertara.excel.meta.model.AnnotationSheet;
import com.sondertara.excel.utils.ListUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

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
        Map<String, AnnotationSheet> map = this.sheetDefinitions.stream().collect(Collectors.toMap(s -> s.getMappingClass().getSimpleName(), Function.identity()));
        return map;
    }


    @Override
    public void addMapper(Class<?> excelClass, ExportFunction<?> function) {
        sheetDefinitions.add(new AnnotationExcelWriterSheetDefinition<>(excelClass, function));
    }

    @Override
    public void addData(List<?> dataList) {
        sheetDefinitions.add(new AnnotationExcelWriterSheetDefinition<>(ListUtils.getGenericClass(dataList), dataList));
        Collections.sort(sheetDefinitions);
    }

    @Override
    public void addMapper(Class<?> ...clazz) {
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


}
