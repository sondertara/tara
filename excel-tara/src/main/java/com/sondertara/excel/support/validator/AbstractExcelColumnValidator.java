package com.sondertara.excel.support.validator;

import java.lang.annotation.Annotation;

/**
 * @author huangxiaohu
 */
public interface AbstractExcelColumnValidator<T extends Annotation> {
    /**
     * initialize the validator
     *
     * @param annotation the validator
     */
    void initialize(T annotation);

    /**
     * validate the value
     *
     * @param value the value
     * @return is pass
     */
    boolean validate(String value);
}
