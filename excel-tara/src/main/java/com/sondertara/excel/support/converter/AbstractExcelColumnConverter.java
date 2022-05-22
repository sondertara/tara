package com.sondertara.excel.support.converter;

/**
 * 字段值转换器
 *
 * @param <A>
 * @param <T>
 * @author chenzw
 */
public interface AbstractExcelColumnConverter<A, T> {

    void initialize(A annotation);

    T convert(String value);
}
