package com.sondertara.excel.meta.annotation;

import com.sondertara.excel.meta.style.DefaultDataCellStyleBuilder;
import com.sondertara.excel.meta.style.DefaultTitleCellStyleBuilder;
import org.apache.poi.ss.usermodel.CellType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Excel列
 *
 * @author huangxiaohu
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelExportField {

    @AliasFor("colName")
    String value() default "";

    /**
     * 标题
     *
     * @return
     */
    @AliasFor("value")
    String colName() default "";

    /**
     * 列索引（从1开始）
     *
     * @return
     */
    int colIndex() default -1;

    /**
     * 默认单元格值
     */
    String defaultCellValue() default "";

    /**
     * 列类型
     *
     * @return the CellType
     */
    CellType cellType() default CellType.STRING;

    /**
     * 数据格式
     *
     * @return
     */
    ExcelDataFormat dataFormat() default @ExcelDataFormat;

    /**
     * 数据样式
     *
     * @return
     */
    Class<?> dataCellStyleBuilder() default DefaultDataCellStyleBuilder.class;

    /**
     * 标题样式
     *
     * @return
     */
    Class<?> titleCellStyleBuilder() default DefaultTitleCellStyleBuilder.class;

    /**
     * 是否自动调整宽度
     *
     * @return
     */
    boolean autoWidth() default false;

    /**
     * 自定义cell宽度
     *
     * @return
     */
    int colWidth() default 16;

}
