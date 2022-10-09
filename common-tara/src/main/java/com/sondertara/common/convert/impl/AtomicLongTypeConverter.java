package com.sondertara.common.convert.impl;

import com.sondertara.common.convert.AbstractTypeConverter;
import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author huangxiaohu
 */
public class AtomicLongTypeConverter extends AbstractTypeConverter<AtomicLong> {
    @Override
    protected AtomicLong convertInternal(Object value) {
        final AtomicLong longValue = new AtomicLong();
        if (value instanceof Number) {
            longValue.set(((Number) value).longValue());
        } else if (value instanceof Boolean) {
            longValue.set((Boolean) value ? 1L : 0L);
        }
        final String sValue = convertToStr(value);
        if (StringUtils.isBlank(sValue)) {
            return null;
        }
        longValue.set(Long.parseLong(sValue));
        return longValue;
    }
}
