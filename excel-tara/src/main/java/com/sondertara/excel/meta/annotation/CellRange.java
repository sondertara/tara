package com.sondertara.excel.meta.annotation;

import com.sondertara.excel.meta.style.DefaultTitleCellStyleBuilder;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author huangxiaohu
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface CellRange {
    /**
     * @return
     */
    int firstRow();

    /**
     * @return
     */
    int lastRow();

    /**
     * @return
     */
    int firstCol();

    /**
     * @return
     */
    int lastCol();

    /**
     * @return
     */
    String title();

    /**
     * @return
     */
    int height() default 20;

    /**
     * @return
     */
    Class<?> cellStyleBuilder() default DefaultTitleCellStyleBuilder.class;

}
