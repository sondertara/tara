package com.sondertara.excel.meta.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelImport {

    /**
     * 绑定的sheet页（可多个, 从1开始）
     * @return
     */
    int[] sheetIndex() default {1};

    /**
     * 起始数据行(从1开始)
     * @return
     */
    int firstDataRow() default 2;

}
