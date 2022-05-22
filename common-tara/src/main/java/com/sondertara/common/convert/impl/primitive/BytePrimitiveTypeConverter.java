package com.sondertara.common.convert.impl.primitive;

import com.sondertara.common.convert.AbstractTypeConverter;
import org.apache.commons.lang3.StringUtils;

/**
 * Byte格式转换器
 *
 * @author chenzw
 */
public class BytePrimitiveTypeConverter extends AbstractTypeConverter<Byte> {

    @Override
    protected Byte convertInternal(Object value) {
        if (value instanceof Number) {
            return ((Number) value).byteValue();
        } else if (value instanceof Boolean) {
            return (Boolean)value?(byte)1:(byte)0;
        }

        String sValue = convertToStr(value);
        if (StringUtils.isBlank(sValue)) {
            return 0;
        }
        return Byte.parseByte(sValue);
    }
}
