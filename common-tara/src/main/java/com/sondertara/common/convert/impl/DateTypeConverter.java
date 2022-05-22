package com.sondertara.common.convert.impl;

import com.sondertara.common.convert.AbstractTypeConverter;
import com.sondertara.common.util.LocalDateTimeUtils;

import java.time.temporal.TemporalAccessor;
import java.util.Calendar;
import java.util.Date;

/**
 * @author huangxiaohu
 */
public class DateTypeConverter extends AbstractTypeConverter<Date> {

    @Override
    protected Date convertInternal(Object value) {
        Long mills;
        if (value instanceof Calendar) {
            mills = ((Calendar) value).getTimeInMillis();
        } else if (value instanceof Long) {
            mills = (Long) value;
        } else if (value instanceof TemporalAccessor) {
            mills = LocalDateTimeUtils.toInstant((TemporalAccessor) value).toEpochMilli();
        } else {
            String sValue = convertToStr(value);
            mills = LocalDateTimeUtils.parseDate(sValue).getTime();

        }

        return new Date(mills);
    }
}
