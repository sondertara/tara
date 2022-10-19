package com.sondertara.excel.meta.annotation.validation;

import com.sondertara.excel.support.validator.ExcelRegexValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author huangxiaohu
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@ConstraintValidator(validator = ExcelRegexValidator.class)
public @interface ExcelRegexValue {

    String regex();

    String message() default "";
}
