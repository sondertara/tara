package com.sondertara.excel.meta.model;

import com.sondertara.excel.enums.ExcelDataType;
import lombok.Getter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * @author huangxiaohu
 */
@Getter
public class AnnotationSheet extends TaraSheet {
    protected Class<?> mappingClass;

    protected int firstDataRow;

    protected int order = 0;
    protected ExcelDataType excelDataType;

    protected final Map<Integer, Field> colFields = new HashMap<>();

    public AnnotationSheet(Class<?> mappingClass) {
        super(0);
        this.mappingClass = mappingClass;
    }

    public <A extends Annotation> A getAnnotation(Class<A> clazz) {
        return mappingClass.getAnnotation(clazz);
    }

}
