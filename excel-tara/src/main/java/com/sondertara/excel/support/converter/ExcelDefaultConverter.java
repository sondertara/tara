package com.sondertara.excel.support.converter;

import java.lang.annotation.Annotation;

/**
 * @author huangxiaohu
 */
public class ExcelDefaultConverter implements AbstractExcelColumnConverter<Annotation, Object> {

    @Override
    public void initialize(Annotation annotation) {

    }

    @Override
    public Object convert(Object value) {
        return value;
    }
}
