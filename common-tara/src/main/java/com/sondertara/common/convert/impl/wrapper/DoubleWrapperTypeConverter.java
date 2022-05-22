package com.sondertara.common.convert.impl.wrapper;

import com.sondertara.common.convert.AbstractTypeConverter;
import org.apache.commons.lang3.StringUtils;

public class DoubleWrapperTypeConverter extends AbstractTypeConverter<Double> {

    @Override
    protected Double convertInternal(Object value) {
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        } else if (value instanceof Boolean) {
            return(Boolean) value ? Double.valueOf("1") : Double.valueOf("0");
        }
        final String sValue = convertToStr(value);
        if (StringUtils.isEmpty(sValue)) {
            return null;
        }
        return Double.valueOf(sValue);
    }
}
