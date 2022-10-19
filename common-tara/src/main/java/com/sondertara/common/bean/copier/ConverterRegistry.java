
package com.sondertara.common.bean.copier;

import com.sondertara.common.convert.TypeConverter;
import com.sondertara.common.lang.Pair;
import com.sondertara.common.lang.map.WeakConcurrentMap;

import java.util.Map;

/**
 * Registry of all added converters.
 *
 * @author huangxiaohu
 */
public class ConverterRegistry {
    private static final Map<Pair<String, String>, TypeConverter<?>> CONVERTER_MAP = new WeakConcurrentMap<>();

    static TypeConverter<?> find(String fromType, String toType) {
        return CONVERTER_MAP.get(Pair.of(fromType, toType));
    }

    public static void put(String fromType, String toType, TypeConverter<?> converter) {
        CONVERTER_MAP.put(Pair.of(fromType, toType), converter);
    }

    static void clear() {
        CONVERTER_MAP.clear();
    }
}
