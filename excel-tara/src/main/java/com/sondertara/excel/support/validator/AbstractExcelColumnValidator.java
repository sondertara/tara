package com.sondertara.excel.support.validator;

import java.lang.annotation.Annotation;

/**
 * @author chenzw
 */
public interface AbstractExcelColumnValidator<T extends Annotation> {

    void initialize(T annotation);

    boolean validate(String value);
}
