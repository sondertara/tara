package com.sondertara.common.convert;

import org.apache.commons.lang3.ArrayUtils;

import java.io.Serializable;

/**
 * 类型转换器
 * @param <T>
 * @author chenzw
 */
public abstract class AbstractTypeConverter<T> implements TypeConverter<T>, Serializable {

    @Override
    public T convert(Object value, T defaultValue) {
        if (value == null) {
            return null;
        }

        // @TODO
        T result = convertInternal(value);
        return ((result == null) ? defaultValue : result);
    }

    /**
     * 内部转化
     * @param value
     * @return
     */
    protected abstract T convertInternal(Object value);

    protected String convertToStr(Object value) {
        if (value == null) {
            return null;
        }

        if (value instanceof CharSequence) {
            return value.toString();
        } else if (value.getClass().isArray()) {
            // 数组转换为逗号间隔的字符串
            return ArrayUtils.toString(value);
        }
        return value.toString();
    }
}
