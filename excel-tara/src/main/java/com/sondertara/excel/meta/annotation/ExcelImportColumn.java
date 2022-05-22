package com.sondertara.excel.meta.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelImportColumn {

    /**
     * 列索引(从1开始)
     *
     * @return
     */
    int colIndex();

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
    String dateFormat() default "";

    /**
     * 列标题（用于异常提示）
     *
     * @return
     */
    String title() default "";
}
