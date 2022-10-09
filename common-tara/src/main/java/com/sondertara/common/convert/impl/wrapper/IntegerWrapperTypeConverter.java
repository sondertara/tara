package com.sondertara.common.convert.impl.wrapper;

import com.sondertara.common.convert.AbstractTypeConverter;
import org.apache.commons.lang3.StringUtils;

public class IntegerWrapperTypeConverter extends AbstractTypeConverter<Integer> {

    @Override
    protected Integer convertInternal(Object value) {
        if (value instanceof Number) {
            return ((Number) value).intValue();
        } else if (value instanceof Boolean) {
            return (Boolean) value ? 1 : 0;
        }
        String sValue = convertToStr(value);
        if (StringUtils.isEmpty(sValue)) {
            return null;
        }

        if (StringUtils.contains(sValue, ".")) {
            return Double.valueOf(sValue).intValue();
        }
        return Integer.valueOf(sValue);
    }
}
