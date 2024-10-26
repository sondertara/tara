package com.sondertara.common.convert.impl;

import com.sondertara.common.text.StringUtils;
import com.sondertara.common.function.TypeConverter;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author huangxiaohu
 */
public class AtomicIntegerTypeConverter implements TypeConverter<AtomicInteger> {

    @Override
    public AtomicInteger apply(Object value) {
        final AtomicInteger intValue = new AtomicInteger();
        if (value instanceof Number) {
            intValue.set(((Number) value).intValue());
        } else if (value instanceof Boolean) {
            intValue.set((Boolean) value ? 1 : 0);
        }
        final String sValue = TypeConverter.convertToStr(value);
        if (StringUtils.isBlank(sValue)) {
            return null;
        }
        intValue.set(Integer.parseInt(sValue));
        return intValue;
    }
}
