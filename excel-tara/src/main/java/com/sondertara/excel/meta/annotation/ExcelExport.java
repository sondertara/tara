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
public @interface ExcelExport {

    /**
     * 顺序（值越小，越靠前）
     *
     * @return order
     */
    int order() default 0;

    /**
     * Sheet名称
     *
     * @return sheet name
     */
    String sheetName() default "数据";

    /**
     * 每个Sheet页允许的最大条数（用于分页）
     *
     * @return the max row of one sheet
     */
    int maxRowsPerSheet() default 60000;

    /**
     * 是否开启条纹
     *
     * @return
     */
    boolean rowStriped() default true;

    /**
     * 条纹颜色
     *
     * @return
     */
    String rowStripeColor() default "E2EFDA";

    /**
     * 标题行高度
     *
     * @return the title row height
     */
    int titleRowHeight() default 20;

    /**
     * 数据行高度
     *
     * @return the data row heiht
     */
    int dataRowHeight() default 20;

    /**
     * 是否使用colIndex 指定顺序，默认禁用，使用字段定义顺序
     *
     * @return whether enable colIndex
     */
    ExcelColBindType bindType() default ExcelColBindType.ORDER;

    /**
     * 是否自动调整宽度
     *
     * @return
     */
    boolean autoWidth() default false;


}
