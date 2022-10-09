package com.sondertara.excel.meta.annotation.datavalidation;

import com.sondertara.excel.support.dataconstraint.ExcelDataValidationConstraint;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author huangxiaohu
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelDataValidation {

    Class<? extends ExcelDataValidationConstraint> dataConstraint();

}
