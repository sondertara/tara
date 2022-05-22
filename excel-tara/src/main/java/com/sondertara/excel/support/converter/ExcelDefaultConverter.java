package com.sondertara.excel.support.converter;

/**
 * @author chenzw
 */
public class ExcelDefaultConverter implements AbstractExcelColumnConverter {

    @Override
    public void initialize(Object annotation) {

    }

    @Override
    public Object convert(String value) {
        return value;
    }
}
