package com.sondertara.common.convert.impl.wrapper;

import com.sondertara.common.convert.AbstractTypeConverter;
import org.apache.commons.lang3.StringUtils;

public class FloatWrapperTypeConverter extends AbstractTypeConverter<Float> {

    @Override
    protected Float convertInternal(Object value) {
        if (value instanceof Number) {
            return ((Number) value).floatValue();
        } else if (value instanceof Boolean) {
            return (Boolean) value ? Float.valueOf("1") : Float.valueOf("0");
        }
        final String sValue = convertToStr(value);
        if (StringUtils.isEmpty(sValue)) {
            return null;
        }
        return Float.valueOf(sValue);
    }
}
