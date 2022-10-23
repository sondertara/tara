package com.sondertara.excel.meta.annotation.validation;

import com.sondertara.excel.enums.FieldRangeType;
import com.sondertara.excel.support.validator.ValueRangeValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author huangxiaohu
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@ConstraintValidator(validator = ValueRangeValidator.class)
public @interface ExcelRangeRule {
    /**
     * range
     * <p>
     * number eg: (2,3),[2,3)
     * date eg: [2019-08-01 12:00:00,],(2019-08-01 12:00:00,2019-10-01 12:00:00]
     * </p>
     *
     * @return the value range
     */
    String min() default "";

    String max() default "";

    FieldRangeType rangeType() default FieldRangeType.RANGE_CLOSE;
}
