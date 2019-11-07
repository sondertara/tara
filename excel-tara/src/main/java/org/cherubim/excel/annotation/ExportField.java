
package org.cherubim.excel.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.math.BigDecimal;

/**
 * @author huangxiaohu
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ExportField {

    /**
     * excel列名称
     *
     * @return
     */
    String columnName();

    /**
     * 默认单元格值
     *
     * @return
     */
    String defaultCellValue() default "";

    /**
     * 日期格式 默认 yyyy-MM-dd HH:mm:ss
     *
     * @return
     */
    String dateFormat() default "yyyy-MM-dd HH:mm:ss";

    /**
     * BigDecimal精度 默认:-1(默认不开启BigDecimal格式化)
     *
     * @return
     */
    int scale() default -1;

    /**
     * BigDecimal 舍入规则 默认:BigDecimal.ROUND_HALF_EVEN
     *
     * @return
     */
    int roundingMode() default BigDecimal.ROUND_HALF_EVEN;
}
