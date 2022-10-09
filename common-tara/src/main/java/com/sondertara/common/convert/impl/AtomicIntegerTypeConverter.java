package com.sondertara.common.convert.impl;

import com.sondertara.common.convert.AbstractTypeConverter;
import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.atomic.AtomicInteger;

public class AtomicIntegerTypeConverter extends AbstractTypeConverter<AtomicInteger> {

    @Override
    protected AtomicInteger convertInternal(Object value) {
        final AtomicInteger intValue = new AtomicInteger();
        if (value instanceof Number) {
            intValue.set(((Number) value).intValue());
        } else if (value instanceof Boolean) {
            intValue.set((Boolean) value ? 1 : 0);
        }
        final String sValue = convertToStr(value);
        if (StringUtils.isBlank(sValue)) {
            return null;
        }
        intValue.set(Integer.parseInt(sValue));
        return intValue;
    }
}
