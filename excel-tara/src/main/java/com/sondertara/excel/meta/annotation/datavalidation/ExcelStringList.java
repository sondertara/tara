package com.sondertara.excel.meta.annotation.datavalidation;

import com.sondertara.excel.support.dataconstraint.ExcelStringListDataValidationConstraint;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author huangxiaohu
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@ExcelDataValidation(dataConstraint = ExcelStringListDataValidationConstraint.class)
public @interface ExcelStringList {

    String[] value() default {};
}
