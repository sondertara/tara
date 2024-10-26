package com.sondertara.excel.support.converter;

import com.sondertara.excel.exception.ExcelConvertException;

import java.lang.annotation.Annotation;

/**
 * 字段值转换器
 *
 * @param <A>
 * @param <T>
 * @author huangxiaohu
 */
public interface AbstractExcelColumnConverter<A extends Annotation, T> {

    void initialize(A annotation);

    T convert(Object value) throws ExcelConvertException;
}
