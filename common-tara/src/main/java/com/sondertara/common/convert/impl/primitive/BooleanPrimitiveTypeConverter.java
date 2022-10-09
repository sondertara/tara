package com.sondertara.common.convert.impl.primitive;

import com.sondertara.common.convert.AbstractTypeConverter;
import org.apache.commons.lang3.BooleanUtils;

public class BooleanPrimitiveTypeConverter extends AbstractTypeConverter<Boolean> {

    @Override
    protected Boolean convertInternal(Object value) {
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        String sValue = convertToStr(value);
        return BooleanUtils.toBoolean(sValue);
    }
}
