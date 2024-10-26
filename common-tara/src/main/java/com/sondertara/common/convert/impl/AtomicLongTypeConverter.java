package com.sondertara.common.convert.impl;

import com.sondertara.common.text.StringUtils;
import com.sondertara.common.function.TypeConverter;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author huangxiaohu
 */
public class AtomicLongTypeConverter implements TypeConverter<AtomicLong> {

    @Override
    public AtomicLong apply(Object value) {
        final AtomicLong longValue = new AtomicLong();
        if (value instanceof Number) {
            longValue.set(((Number) value).longValue());
        } else if (value instanceof Boolean) {
            longValue.set((Boolean) value ? 1L : 0L);
        }
        final String sValue = TypeConverter.convertToStr(value);
        if (StringUtils.isBlank(sValue)) {
            return null;
        }
        longValue.set(Long.parseLong(sValue));
        return longValue;
    }
}
