package com.sondertara.common.convert.impl.wrapper;

import com.sondertara.common.convert.AbstractTypeConverter;
import org.apache.commons.lang3.StringUtils;

public class ByteWrapperTypeConverter extends AbstractTypeConverter<Byte> {

    @Override
    protected Byte convertInternal(Object value) {
        if (value instanceof Number) {
            return ((Number) value).byteValue();
        } else if (value instanceof Boolean) {
            return (Boolean) value ? (byte) 1 : (byte) 0;
        }

        String sValue = convertToStr(value);
        if (StringUtils.isBlank(sValue)) {
            return null;
        }
        return Byte.valueOf(sValue);
    }
}
