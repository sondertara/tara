package com.sondertara.common.invocation.proxy.aop.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 方法过滤器
 *
 * @author byx
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Filter {
    /**
     * 方法名
     */
    String name() default "";

    /**
     * 方法名模式
     */
    String pattern() default "";

    /**
     * 返回值类型
     */
    Class<?> returnType() default Dummy.class;

    /**
     * 参数类型
     */
    Class<?>[] parameterTypes() default {};

    final class Dummy {}
}
