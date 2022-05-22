package com.sondertara.excel.meta.model;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Map;

/**
 * @author huangxiaohu
 */
public interface ExcelSheetDefinition extends Comparable<ExcelSheetDefinition> {

    /**
     * 获取Sheet注解
     *
     * @param clazz
     * @return
     */
    <A extends Annotation> A getAnnotation(Class<A> clazz);

    /**
     * 获取绑定的对象
     *
     * @return
     */
    Class<?> getBindingModel();

    /**
     * 获取列字段
     *
     * @return
     */
    Map<Integer, Field> getColumnFields();

    /**
     * 获取列标题
     *
     * @return
     */
    Map<Integer, String> getColumnTitles();

    /**
     * 获取数据行号
     *
     * @return
     */
    int getFirstDataRow();


}
