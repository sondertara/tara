package com.sondertara.common.convert;

import java.lang.reflect.Type;

/**
 * @author huangxiaohu
 */
public class GenericConvert implements TypeConverter<Object> {

    private final Type targetType;

    public GenericConvert(Type type) {
        this.targetType = type;
    }

    @Override
    public Object convert(Object value, Object defaultValue) {
        return ConvertUtils.convert(targetType, value);
    }
}
