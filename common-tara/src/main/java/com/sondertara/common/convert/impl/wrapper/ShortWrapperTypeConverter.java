package com.sondertara.common.convert.impl.wrapper;

import com.sondertara.common.convert.AbstractTypeConverter;
import org.apache.commons.lang3.StringUtils;

public class ShortWrapperTypeConverter extends AbstractTypeConverter<Short> {

    @Override
    protected Short convertInternal(Object value) {
        if (value instanceof Number) {
            return ((Number) value).shortValue();
        } else if (value instanceof Boolean) {
            return (Boolean)value?(short)1:(short)0;
        }
        String sValue = convertToStr(value);
        if (StringUtils.isBlank(sValue)) {
            return null;
        }
        return Short.valueOf(sValue);
    }
}
