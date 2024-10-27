package com.sondertara.excel.meta.support;

import com.sondertara.common.datetime.LocalDateTimeUtils;

public class DateFormatter implements ExcelDataFormatter {

    private final String datePattern;

    public DateFormatter(String datePattern) {
        this.datePattern = datePattern;
    }

    @Override
    public Object format(Object input) {
        return LocalDateTimeUtils.parse((String) input, datePattern);
    }
}
