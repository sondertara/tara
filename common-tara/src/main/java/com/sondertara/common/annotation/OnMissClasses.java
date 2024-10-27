package com.sondertara.common.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;

/**
 *  */
@Retention(RetentionPolicy.RUNTIME)
@Target({TYPE})
public @interface OnMissClasses {
    String[] value();
}
