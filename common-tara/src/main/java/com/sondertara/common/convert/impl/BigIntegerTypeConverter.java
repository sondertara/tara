package com.sondertara.common.convert.impl;

import com.sondertara.common.text.StringUtils;
import com.sondertara.common.function.TypeConverter;

import java.math.BigInteger;

/**
 * @author huangxiaohu
 */
public class BigIntegerTypeConverter implements TypeConverter<BigInteger> {

    @Override
    public BigInteger apply(Object value) {
        if (value instanceof Long) {
            return BigInteger.valueOf((Long) value);
        } else if (value instanceof Boolean) {
            return BigInteger.valueOf((boolean) value ? 1 : 0);
        }
        final String sValue = TypeConverter.convertToStr(value);
        if (StringUtils.isBlank(sValue)) {
            return null;
        }
        return new BigInteger(sValue);
    }
}
