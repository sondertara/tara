package com.sondertara.excel.meta.annotation.validation;


import com.sondertara.excel.support.validator.AbstractExcelColumnValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ConstraintValidator {

    Class<? extends AbstractExcelColumnValidator> validator();
}
