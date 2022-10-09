package com.sondertara.common.convert.impl.primitive;

import com.sondertara.common.convert.AbstractTypeConverter;
import org.apache.commons.lang3.StringUtils;

public class CharPrimitiveTypeConverter extends AbstractTypeConverter<Character> {

    @Override
    protected Character convertInternal(Object value) {
        if (value instanceof Character) {
            return (Character) value;
        } else if (value instanceof Boolean) {
            return (Boolean) value ? (char) 1 : (char) 0;
        }
        final String sValue = convertToStr(value);
        if (StringUtils.isBlank(sValue)) {
            return 0;
        }
        return sValue.charAt(0);
    }
}
