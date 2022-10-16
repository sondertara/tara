package com.sondertara.excel.meta.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author huangxiaohu
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelImport {

    /**
     * 绑定的sheet页（可多个, 从1开始）
     *
     * @return
     */
    int[] sheetIndex() default {1};

    /**
     * 起始数据行(从1开始)
     *
     * @return
     */
    int firstDataRow() default 2;

    /**
     * 是否指定列序号，否则使用属性定义的顺序
     * @return
     */
    boolean enableColIndex() default  false;

}
