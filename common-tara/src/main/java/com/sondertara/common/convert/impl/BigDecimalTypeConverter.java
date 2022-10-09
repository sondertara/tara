package com.sondertara.common.convert.impl;

import com.sondertara.common.convert.AbstractTypeConverter;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.math.BigInteger;

public class BigDecimalTypeConverter extends AbstractTypeConverter<BigDecimal> {
    @Override
    protected BigDecimal convertInternal(Object value) {
        if (value instanceof Long) {
            return new BigDecimal((Long) value);
        } else if (value instanceof Integer) {
            return new BigDecimal((Integer) value);
        } else if (value instanceof BigInteger) {
            return new BigDecimal((BigInteger) value);
        } else if (value instanceof Boolean) {
            return new BigDecimal((boolean) value ? 1 : 0);
        }
        final String sValue = convertToStr(value);
        if (StringUtils.isBlank(sValue)) {
            return null;
        }
        return new BigDecimal(sValue);
    }
}
