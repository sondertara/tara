package com.sondertara.common.convert.impl;

import com.sondertara.common.convert.AbstractTypeConverter;
import org.apache.commons.lang3.StringUtils;

import java.math.BigInteger;

public class BigIntegerTypeConverter extends AbstractTypeConverter<BigInteger> {

    @Override
    protected BigInteger convertInternal(Object value) {
        if (value instanceof Long) {
            return BigInteger.valueOf((Long) value);
        } else if (value instanceof Boolean) {
            return BigInteger.valueOf((boolean) value ? 1 : 0);
        }
        final String sValue = convertToStr(value);
        if (StringUtils.isBlank(sValue)) {
            return null;
        }
        return new BigInteger(sValue);
    }
}
