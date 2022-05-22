package com.sondertara.excel.support.validator;

import java.lang.annotation.Annotation;

/**
 * 默认校验器
 * @author chenzw
 */
public class ExcelDefaultValidator<T extends Annotation> implements AbstractExcelColumnValidator<T> {

    @Override
    public void initialize(T annotation) {

    }

    @Override
    public boolean validate(String value) {
        return true;
    }
}
