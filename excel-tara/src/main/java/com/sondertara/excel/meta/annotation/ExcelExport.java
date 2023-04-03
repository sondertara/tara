package com.sondertara.excel.meta.annotation;

import com.sondertara.excel.enums.ExcelColBindType;
import com.sondertara.excel.fast.writer.Color;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * the Excel export annotation
 *
 * @author huangxiaohu
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelExport {

    /**
     * the multiple sheet order, smaller is parsed earlier
     * 顺序（值越小，越靠前）
     *
     * @return order
     */
    int order() default 0;

    /**
     * The sheet name
     * Sheet名称
     *
     * @return sheet name
     */
    String sheetName() default "数据";

    /**
     * the max row of one sheet,excluding the title row
     * 每个Sheet页允许的最大条数（用于分页）
     *
     * @return the max row of one sheet
     */
    int maxRowsPerSheet() default 60000;

    /**
     * is open the row strip
     * 是否开启条纹
     *
     * @return is open the row strip
     */
    boolean rowStriped() default true;

    /**
     * the row strip color
     * 条纹颜色
     *
     * @return the color
     */
    String rowStripeColor() default Color.EXCEL_GREEN_STRIPE;

    /**
     * the title row height
     * 标题行高度
     *
     * @return the title row height
     */
    int titleRowHeight() default 20;

    /**
     * the data row height
     * 数据行高度
     *
     * @return the data row height
     */
    int dataRowHeight() default 20;

    /**
     * the bind type
     * If {@link ExcelColBindType#COL_INDEX} the value {@link ExcelExportField#colIndex()} must be set.
     * If {@link ExcelColBindType#ORDER} the colIndex is the order field definition order.
     *
     * @return whether enable colIndex
     * @see ExcelColBindType
     */
    ExcelColBindType bindType() default ExcelColBindType.ORDER;

    /**
     * is open column auto width
     * this is higher priority than {@link ExcelExportField#autoWidth()}
     * 是否自动调整宽度
     *
     * @return is open all column auto width
     */
    boolean autoWidth() default false;

}
