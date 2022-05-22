package com.sondertara.common.convert.impl.primitive;

import com.sondertara.common.convert.AbstractTypeConverter;
import com.sondertara.common.util.StringUtils;


/**
 * @author chenzw
 */
public class IntegerPrimitiveTypeConverter extends AbstractTypeConverter<Integer> {

    @Override
    protected Integer convertInternal(Object value) {
        if (value instanceof Number) {
            return ((Number) value).intValue();
        } else if (value instanceof Boolean) {
            return (Boolean) value ? 1 : 0;
        }
        String sValue = convertToStr(value);

        if (StringUtils.isEmpty(sValue)) {
            return 0;
        }
        sValue = StringUtils.trim(sValue);
        if (StringUtils.contains(sValue, ".")) {
            return Double.valueOf(sValue).intValue();
        }
        return Integer.valueOf(sValue);
    }
}
