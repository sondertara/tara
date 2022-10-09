package com.sondertara.common.convert.impl.primitive;

import com.sondertara.common.convert.AbstractTypeConverter;
import org.apache.commons.lang3.math.NumberUtils;

public class LongPrimitiveTypeConverter extends AbstractTypeConverter<Long> {

    @Override
    protected Long convertInternal(Object value) {
        if (value instanceof Number) {
            return ((Number) value).longValue();
        } else if (value instanceof Boolean) {
            return (Boolean) value ? 1L : 0L;
        }
        final String sValue = convertToStr(value);
        return NumberUtils.toLong(sValue, 0L);
    }
}
