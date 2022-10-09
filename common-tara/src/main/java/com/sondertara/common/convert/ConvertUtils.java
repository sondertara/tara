package com.sondertara.common.convert;

import com.sondertara.common.convert.impl.AtomicIntegerTypeConverter;
import com.sondertara.common.convert.impl.AtomicLongTypeConverter;
import com.sondertara.common.convert.impl.BigDecimalTypeConverter;
import com.sondertara.common.convert.impl.BigIntegerTypeConverter;
import com.sondertara.common.convert.impl.DateTypeConverter;
import com.sondertara.common.convert.impl.NumberTypeConverter;
import com.sondertara.common.convert.impl.StringTypeConverter;
import com.sondertara.common.convert.impl.primitive.BooleanPrimitiveTypeConverter;
import com.sondertara.common.convert.impl.primitive.BytePrimitiveTypeConverter;
import com.sondertara.common.convert.impl.primitive.CharPrimitiveTypeConverter;
import com.sondertara.common.convert.impl.primitive.DoublePrimitiveTypeConverter;
import com.sondertara.common.convert.impl.primitive.FloatPrimitiveTypeConverter;
import com.sondertara.common.convert.impl.primitive.IntegerPrimitiveTypeConverter;
import com.sondertara.common.convert.impl.primitive.LongPrimitiveTypeConverter;
import com.sondertara.common.convert.impl.primitive.ShortPrimitiveTypeConverter;
import com.sondertara.common.convert.impl.wrapper.BooleanWrapperTypeConverter;
import com.sondertara.common.convert.impl.wrapper.ByteWrapperTypeConverter;
import com.sondertara.common.convert.impl.wrapper.DoubleWrapperTypeConverter;
import com.sondertara.common.convert.impl.wrapper.FloatWrapperTypeConverter;
import com.sondertara.common.convert.impl.wrapper.IntegerWrapperTypeConverter;
import com.sondertara.common.convert.impl.wrapper.LongWrapperTypeConverter;
import com.sondertara.common.convert.impl.wrapper.ShortWrapperTypeConverter;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 转换器工具类
 *
 * @author huangxiaohu
 */
public class ConvertUtils {

    private static final Map<Type, TypeConverter<?>> DEFAULT_TYPE_CONVERTER_MAP;

    static {
        DEFAULT_TYPE_CONVERTER_MAP = new HashMap<>();

        DEFAULT_TYPE_CONVERTER_MAP.put(int.class, new IntegerPrimitiveTypeConverter());
        DEFAULT_TYPE_CONVERTER_MAP.put(long.class, new LongPrimitiveTypeConverter());
        DEFAULT_TYPE_CONVERTER_MAP.put(byte.class, new BytePrimitiveTypeConverter());
        DEFAULT_TYPE_CONVERTER_MAP.put(short.class, new ShortPrimitiveTypeConverter());
        DEFAULT_TYPE_CONVERTER_MAP.put(float.class, new FloatPrimitiveTypeConverter());
        DEFAULT_TYPE_CONVERTER_MAP.put(double.class, new DoublePrimitiveTypeConverter());
        DEFAULT_TYPE_CONVERTER_MAP.put(char.class, new CharPrimitiveTypeConverter());
        DEFAULT_TYPE_CONVERTER_MAP.put(boolean.class, new BooleanPrimitiveTypeConverter());

        DEFAULT_TYPE_CONVERTER_MAP.put(Integer.class, new IntegerWrapperTypeConverter());
        DEFAULT_TYPE_CONVERTER_MAP.put(Long.class, new LongWrapperTypeConverter());
        DEFAULT_TYPE_CONVERTER_MAP.put(Byte.class, new ByteWrapperTypeConverter());
        DEFAULT_TYPE_CONVERTER_MAP.put(Short.class, new ShortWrapperTypeConverter());
        DEFAULT_TYPE_CONVERTER_MAP.put(Float.class, new FloatWrapperTypeConverter());
        DEFAULT_TYPE_CONVERTER_MAP.put(Double.class, new DoubleWrapperTypeConverter());
        DEFAULT_TYPE_CONVERTER_MAP.put(Boolean.class, new BooleanWrapperTypeConverter());
        DEFAULT_TYPE_CONVERTER_MAP.put(Character.class, new CharPrimitiveTypeConverter());

        DEFAULT_TYPE_CONVERTER_MAP.put(java.util.Date.class, new DateTypeConverter());

        DEFAULT_TYPE_CONVERTER_MAP.put(String.class, new StringTypeConverter());
        DEFAULT_TYPE_CONVERTER_MAP.put(AtomicInteger.class, new AtomicIntegerTypeConverter());
        DEFAULT_TYPE_CONVERTER_MAP.put(AtomicLong.class, new AtomicLongTypeConverter());
        DEFAULT_TYPE_CONVERTER_MAP.put(BigDecimal.class, new BigDecimalTypeConverter());
        DEFAULT_TYPE_CONVERTER_MAP.put(BigInteger.class, new BigIntegerTypeConverter());
        DEFAULT_TYPE_CONVERTER_MAP.put(Number.class, new NumberTypeConverter());
    }

    private ConvertUtils() {

    }

    public static <T> T convert(Class<T> type, Object value) {
        return convert((Type) type, value);
    }

    @SuppressWarnings("unchecked")
    public static <T> T convert(Type type, Object value) {
        return (T) getConverter(type).convert(value, null);
    }

    /**
     * @param type the type
     * @param <T>  the class
     * @return the target
     */
    @SuppressWarnings("unchecked")
    private static <T> TypeConverter<T> getConverter(Type type) {
        TypeConverter<?> typeConverter = DEFAULT_TYPE_CONVERTER_MAP.get(type);
        if (typeConverter != null) {
            return (TypeConverter<T>) typeConverter;
        }

        throw new UnsupportedOperationException("No Converter for type [" + type.getTypeName() + "]");
    }

}
