package com.sondertara.common.convert;

import com.sondertara.common.function.TypeConverter;

import java.lang.reflect.Type;

/**
 * @author huangxiaohu
 */
public class SimpleConverter implements TypeConverter<Object> {

    private final Type targetType;

    public SimpleConverter(Type type) {
        this.targetType = type;
    }

    @Override
    public Object apply(Object object) {
        return ConvertUtils.convert(targetType, object);
    }
}
