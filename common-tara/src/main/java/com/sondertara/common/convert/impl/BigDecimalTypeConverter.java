package com.sondertara.common.convert.impl;

import com.sondertara.common.text.StringUtils;
import com.sondertara.common.function.TypeConverter;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * @author huangxiaohu
 */
public class BigDecimalTypeConverter implements TypeConverter<BigDecimal> {

    @Override
    public BigDecimal apply(Object value) {
        if (value instanceof Long) {
            return new BigDecimal((Long) value);
        } else if (value instanceof Integer) {
            return new BigDecimal((Integer) value);
        } else if (value instanceof BigInteger) {
            return new BigDecimal((BigInteger) value);
        } else if (value instanceof Boolean) {
            return new BigDecimal((boolean) value ? 1 : 0);
        }
        final String sValue = TypeConverter.convertToStr(value);
        if (StringUtils.isBlank(sValue)) {
            return null;
        }
        return new BigDecimal(sValue);
    }
}
