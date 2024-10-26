package com.sondertara.common.convert.impl;

import com.sondertara.common.datetime.LocalDateTimeUtils;
import com.sondertara.common.function.TypeConverter;

import java.time.temporal.TemporalAccessor;
import java.util.Calendar;
import java.util.Date;

/**
 * 字符串格式转换器
 *
 * @author huangxiaohu
 */
public class StringTypeConverter implements TypeConverter<String> {

    @Override
    public String apply(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Date) {
            return LocalDateTimeUtils.format((Date) value);
        } else if (value instanceof Calendar) {
            return LocalDateTimeUtils.format(LocalDateTimeUtils.date((Calendar) value));
        } else if (value instanceof TemporalAccessor) {
            return LocalDateTimeUtils.format((TemporalAccessor) value);
        }
        return TypeConverter.convertToStr(value);

    }
}
