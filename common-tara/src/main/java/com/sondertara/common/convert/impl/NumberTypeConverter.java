package com.sondertara.common.convert.impl;

import com.sondertara.common.text.StringUtils;
import com.sondertara.common.exception.TaraException;
import com.sondertara.common.function.TypeConverter;

import java.text.NumberFormat;
import java.text.ParseException;

/**
 * @author huangxiaohu
 */
public class NumberTypeConverter implements TypeConverter<Number> {

    @Override
    public Number apply(Object value) {
        if (value instanceof Number) {
            return (Number) value;
        } else if (value instanceof Boolean) {
            return (Boolean) value ? 1 : 0;
        }
        final String sValue = TypeConverter.convertToStr(value);

        if (StringUtils.isBlank(sValue)) {
            return null;
        }

        try {
            return NumberFormat.getInstance().parse(sValue);
        } catch (ParseException e) {
            throw new TaraException(e);
        }
    }
}
