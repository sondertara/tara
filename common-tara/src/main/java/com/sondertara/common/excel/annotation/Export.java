package com.sondertara.common.excel.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author: chenxinshi
 * @date: 2019/2/13
 * @desc: 导出注解
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Export {

    /**
     * 字段在Excel首行的名称
     */
    String title();

    /**
     * 导出字段所在列的下标，大于等于0时有效，默认为类中该注解出现的次序
     */
    int index() default -1;

    /**
     * 字段为null时的默认值
     */
    String ifNull() default "";

    /**
     * 字段为""时的默认值
     */
    String ifEmptyString() default "";

    /**
     * 字段类型为Date时有效，写法同SimpleDateFormat构造参数
     */
    String dateFormat() default "yyyy-MM-dd HH:mm:ss";

    /**
     * 字段类型为Number时有效，写法同DecimalFormat构造参数
     */
    String numberFormat() default "";

    /**
     * 字段类型为BigDecimal时有效，写法同setScale()
     */
    int[] scale() default {};

    /**
     * 连接符，Iterator时有效
     */
    String join() default "";
}
