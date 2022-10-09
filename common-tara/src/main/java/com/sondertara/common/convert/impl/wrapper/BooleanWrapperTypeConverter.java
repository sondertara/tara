package com.sondertara.common.convert.impl.wrapper;

import com.sondertara.common.convert.AbstractTypeConverter;
import org.apache.commons.lang3.BooleanUtils;

public class BooleanWrapperTypeConverter extends AbstractTypeConverter<Boolean> {
    @Override
    protected Boolean convertInternal(Object value) {
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        String sValue = convertToStr(value);
        return BooleanUtils.toBoolean(sValue);
    }
}
