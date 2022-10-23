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
     * the col index,begin is 1
     * it takes effect only when {@link ExcelImport#bindType()} is {@link com.sondertara.excel.enums.ExcelColBindType#COL_INDEX}
     * 列索引(从1开始)
     *
     * @return the bind col index
     */
    int colIndex() default -1;

    /**
     * all empty cell
     * 是否允许空值
     *
     * @return allow empty
     */
    boolean allowBlank() default true;

    /**
     * data format
     * 日期格式
     *
     * @return the data format
     */
    String dateFormat() default DatePattern.NORM_DATETIME_PATTERN;

    /**
     * the column title
     * 列标题
     * if {@link ExcelImport#bindType()} is {@link com.sondertara.excel.enums.ExcelColBindType#TITLE} this value must be set to the Excel title row cell
     *
     * @return the title
     */
    String title() default "";
}
