package com.sondertara.common.convert;

/**
 * 字段类型转换器
 *
 * @author huangxiaohu
 * @param <T>
 */
public interface TypeConverter<T> {

    /**
     * 将值转换成指定类型（如果类型无法确定，则使用默认值）
     *
     * @param value the source obj
     * @param defaultValue  default if null
     * @return the target obj
     */
    T convert(Object value, T defaultValue);
}
