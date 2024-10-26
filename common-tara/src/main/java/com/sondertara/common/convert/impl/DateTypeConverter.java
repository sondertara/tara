package com.sondertara.common.convert.impl;

import com.sondertara.common.datetime.LocalDateTimeUtils;
import com.sondertara.common.function.TypeConverter;

import java.time.temporal.TemporalAccessor;
import java.util.Calendar;
import java.util.Date;

/**
 * @author huangxiaohu
 */

public class DateTypeConverter implements TypeConverter<Date> {

    @Override
    public Date apply(Object value) {
        if (value instanceof Calendar) {
            return LocalDateTimeUtils.date((Calendar) value);
        } else if (value instanceof Long) {
            return LocalDateTimeUtils.date((Long) value);
        } else if (value instanceof TemporalAccessor) {
            return LocalDateTimeUtils.date((TemporalAccessor) value);
        } else {
            return LocalDateTimeUtils.date(value);
        }
    }
}
