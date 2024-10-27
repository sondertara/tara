package com.sondertara.common.function;

import com.sondertara.common.collection.ArrayUtils;

import java.util.function.Function;

/**
 * 字段类型转换器
 *
 * @param <T>
 * @author huangxiaohu
 */
@FunctionalInterface
public interface TypeConverter<T> extends Function<Object, T> {

    /**
     * 将值转换成指定类型（如果类型无法确定，则使用默认值）
     *
     * @param value        the source obj
     * @param defaultValue default if null
     * @return the target obj
     */
    default T convert(Object value, T defaultValue) {
        if (null == value) {
            return defaultValue;
        }
        T applied = apply(value);
        return ((applied == null) ? defaultValue : applied);
    }

    /**
     * 将值转换成字符串
     * @param value 原始值
     * @return 字符串
     */

    static String convertToStr(Object value) {
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
