package com.sondertara.excel.meta;

import com.sondertara.excel.enums.ExcelDataType;
import com.sondertara.excel.meta.model.TaraSheet;
import lombok.Getter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;


@Getter
public class AnnotationSheet extends TaraSheet {
    protected Class<?> mappingClass;

    protected int firstDataRow;
    private int lastDataRow;

    protected int order = 0;
    protected ExcelDataType excelDataType;

    private final Map<Integer, Field> colFields = new HashMap<>();

    public AnnotationSheet(Class<?> mappingClass) {
        super(null, 0);
        this.mappingClass = mappingClass;
    }

    public <A extends Annotation> A getAnnotation(Class<A> clazz) {
        return mappingClass.getAnnotation(clazz);
    }

}
