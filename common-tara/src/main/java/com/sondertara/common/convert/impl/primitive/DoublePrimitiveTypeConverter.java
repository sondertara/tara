package com.sondertara.common.convert.impl.primitive;

import com.sondertara.common.convert.AbstractTypeConverter;
import org.apache.commons.lang3.math.NumberUtils;

/**
 * @author huangxiaohu
 */
public class DoublePrimitiveTypeConverter extends AbstractTypeConverter<Double> {

    @Override
    protected Double convertInternal(Object value) {
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        } else if (value instanceof Boolean) {
            return (Boolean) value ? Double.valueOf("1") : Double.valueOf("0");
        }
        final String sValue = convertToStr(value);
        return NumberUtils.toDouble(sValue, 0D);
    }
}
