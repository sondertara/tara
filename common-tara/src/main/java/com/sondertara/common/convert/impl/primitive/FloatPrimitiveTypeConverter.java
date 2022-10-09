package com.sondertara.common.convert.impl.primitive;

import com.sondertara.common.convert.AbstractTypeConverter;
import org.apache.commons.lang3.math.NumberUtils;

/**
 * @author huangxiaohu
 */
public class FloatPrimitiveTypeConverter extends AbstractTypeConverter<Float> {

    @Override
    protected Float convertInternal(Object value) {

        if (value instanceof Number) {
            return ((Number) value).floatValue();
        } else if (value instanceof Boolean) {
            return (Boolean) value ? Float.valueOf("1") : Float.valueOf("0");
        }
        final String sValue = convertToStr(value);
        return NumberUtils.toFloat(sValue, 0F);
    }
}
