package com.sondertara.common.excel.annotation;



import com.sondertara.common.excel.enums.Null;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author: chenxinshi
 * @date: 2019/2/13
 * @desc: 导入注解
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Import {

    /**
     * 字段在Excel首行的名称
     */
    String title();

    /**
     * 导出字段所在列的下标，大于0时有效，默认为类中该注解出现的次序
     */
    int index() default -1;

    /**
     * 是否必填
     */
    boolean required() default false;

    /**
     * 字符串类型有效，枚举全限定名
     */
    Class<? extends Enum> enumClass() default Null.class;

    /**
     * 字符串类型有效，枚举属性名
     */
    String enumField() default "name";

    /**
     * 字符串类型有效，最大长度
     */
    int maxLength() default 200;

    /**
     * 字符串类型有效，正则表达式匹配
     */
    String regExp() default "";

    /**
     * 数字类型有效，大于等于
     */
    String gte() default "";

    /**
     * 数字类型有效，大于
     */
    String gt() default "";

    /**
     * 数字类型有效，小于等于
     */
    String lte() default "";

    /**
     * 数字类型有效，小于
     */
    String lt() default "";

    /**
     * 日期类型有效，日期大于
     */
    String dateFrom() default "2000-01-01";
}
