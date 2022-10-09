package com.sondertara.common.convert.impl;

import com.sondertara.common.convert.AbstractTypeConverter;
import com.sondertara.common.exception.TaraException;
import org.apache.commons.lang3.StringUtils;

import java.text.NumberFormat;
import java.text.ParseException;

/**
 * @author huangxiaohu
 */
public class NumberTypeConverter extends AbstractTypeConverter<Number> {

    @Override
    protected Number convertInternal(Object value) {
        if (value instanceof Number) {
            return (Number) value;
        } else if (value instanceof Boolean) {
            return (Boolean) value ? 1 : 0;
        }
        final String sValue = convertToStr(value);

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
