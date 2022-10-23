package com.sondertara.excel.meta.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * complex header of Excel
 * 复杂的列标题
 *
 * @author huangxiaohu
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelComplexHeader {
    /**
     * 表头
     *
     * @return
     */
    CellRange[] value();
}
