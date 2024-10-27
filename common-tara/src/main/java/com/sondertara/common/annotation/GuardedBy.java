package com.sondertara.common.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

/**
 * @author huangxiaohu.1ih
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target(value = {TYPE, FIELD, METHOD, PARAMETER, CONSTRUCTOR, LOCAL_VARIABLE})
public @interface GuardedBy {
    String value();
}
