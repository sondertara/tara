package com.sondertara.excel.meta.annotation;

import com.sondertara.excel.enums.ExcelColBindType;

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
     * bind the sheet index of Excel,begin is  1
     * 绑定的sheet页（可多个, 从1开始）
     *
     * @return sheets
     */
    int[] sheetIndex() default {1};

    /**
     * point the data row num start,begin is 1
     * 起始数据行(从1开始)
     *
     * @return the data row index
     */
    int firstDataRow() default 2;


    /**
     * 数据绑定类型
     * data bind type,default order is the field definition order is class
     * If {@link ExcelColBindType#COL_INDEX} the value {@link ExcelImportField#colIndex()} must be set.
     * If {@link ExcelColBindType#ORDER} the colIndex is the order field definition order.
     * If {@link ExcelColBindType#TITLE} the value {@link ExcelImportField#title()} must be set,and colIndex will calculate by the title in Excel
     *
     * @return the type of data bind
     * @see ExcelColBindType
     */
    ExcelColBindType bindType() default ExcelColBindType.ORDER;

}
