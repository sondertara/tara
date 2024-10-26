
package com.sondertara.common.bean.copier;

import com.sondertara.common.struct.Pair;
import com.sondertara.common.function.TypeConverter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Registry of all added converters.
 *
 * @author huangxiaohu
 */
public class ConverterRegistry {
    private static final Map<Pair<String, String>, TypeConverter<?>> CONVERTER_MAP = new ConcurrentHashMap<>();

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
