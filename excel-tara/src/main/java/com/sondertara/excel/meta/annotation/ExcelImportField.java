package com.sondertara.excel.meta.annotation;

import com.sondertara.common.time.DatePattern;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author huangxiaohu
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelImportField {

    /**
     * 列索引(从1开始)
     *
     * @return
     */
    int colIndex() default -1;

    /**
     * 是否允许空值
     *
     * @return
     */
    boolean allowBlank() default true;

    /**
     * 日期格式
     *
     * @return
     */
    String dateFormat() default DatePattern.NORM_DATETIME_PATTERN;

    /**
     * 列标题
     *
     * @return
     */
    String title() default "";
}
