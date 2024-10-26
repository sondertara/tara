package com.sondertara.common.convert;

import com.sondertara.common.convert.impl.AtomicIntegerTypeConverter;
import com.sondertara.common.convert.impl.AtomicLongTypeConverter;
import com.sondertara.common.convert.impl.BigDecimalTypeConverter;
import com.sondertara.common.convert.impl.BigIntegerTypeConverter;
import com.sondertara.common.convert.impl.DateTypeConverter;
import com.sondertara.common.convert.impl.NoopConverter;
import com.sondertara.common.convert.impl.NumberTypeConverter;
import com.sondertara.common.convert.impl.StringTypeConverter;
import com.sondertara.common.exception.ValueConvertException;
import com.sondertara.common.function.TypeConverter;
import com.sondertara.common.reflect.type.TypeUtils;
import org.jspecify.annotations.Nullable;

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

        DEFAULT_TYPE_CONVERTER_MAP.put(int.class, BasicTypeStoreProvider.TO_INT);
        DEFAULT_TYPE_CONVERTER_MAP.put(long.class, BasicTypeStoreProvider.TO_LONG);
        DEFAULT_TYPE_CONVERTER_MAP.put(byte.class, BasicTypeStoreProvider.TO_BYTE);
        DEFAULT_TYPE_CONVERTER_MAP.put(short.class, BasicTypeStoreProvider.TO_SHORT);
        DEFAULT_TYPE_CONVERTER_MAP.put(float.class, BasicTypeStoreProvider.TO_FLOAT);
        DEFAULT_TYPE_CONVERTER_MAP.put(double.class, BasicTypeStoreProvider.TO_DOUBLE);
        DEFAULT_TYPE_CONVERTER_MAP.put(char.class, BasicTypeStoreProvider.TO_CHAR);
        DEFAULT_TYPE_CONVERTER_MAP.put(boolean.class, BasicTypeStoreProvider.TO_BOOLEAN);

        DEFAULT_TYPE_CONVERTER_MAP.put(Integer.class, BasicTypeStoreProvider.TO_INT);
        DEFAULT_TYPE_CONVERTER_MAP.put(Long.class, BasicTypeStoreProvider.TO_LONG);
        DEFAULT_TYPE_CONVERTER_MAP.put(Byte.class, BasicTypeStoreProvider.TO_BYTE);
        DEFAULT_TYPE_CONVERTER_MAP.put(Short.class, BasicTypeStoreProvider.TO_SHORT);
        DEFAULT_TYPE_CONVERTER_MAP.put(Float.class, BasicTypeStoreProvider.TO_FLOAT);
        DEFAULT_TYPE_CONVERTER_MAP.put(Double.class, BasicTypeStoreProvider.TO_DOUBLE);
        DEFAULT_TYPE_CONVERTER_MAP.put(Boolean.class, BasicTypeStoreProvider.TO_BOOLEAN);
        DEFAULT_TYPE_CONVERTER_MAP.put(Character.class, BasicTypeStoreProvider.TO_CHAR);

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

    /**
     * 转化
     *
     * @param source 原始值
     * @param mapper 函数
     * @param <T>    ignore
     * @param <R>    ignore
     * @return 新值
     */
    public static <T, R> R convert(T source, TypeConverter<R> mapper) {
        return mapper.apply(source);
    }


    public static <T> T convert(Class<T> tClass, Object value) {
        return convert(tClass, value, null);
    }

    public static <T> T convert(Type type, Object value, T defaultValue) {
        if (null == value) {
            return defaultValue;
        }
        TypeConverter<T> converter = findTypeConvert(value.getClass(), type);
        if (null == converter) {
            throw new UnsupportedOperationException("No Converter for type [" + type.getTypeName() + "]");

        }
        return converter.convert(value, defaultValue);
    }

    @SuppressWarnings("unchecked")
    public static <T> T convert(Class<T> tClass, Object value, T defaultValue) {
        if (null == value) {
            return defaultValue;
        }
        if (tClass.isAssignableFrom(value.getClass())) {
            return (T) value;
        }
        TypeConverter<T> converter = findTypeConvert(value.getClass(), tClass);
        if (null == converter) {
            throw new ValueConvertException("No Converter for class [" + tClass.getName() + "]");

        }
        return converter.convert(value, defaultValue);
    }


    @Nullable
    @SuppressWarnings("unchecked")
    public static <T> TypeConverter<T> findTypeConvert(Class<?> from, Type to) {
        if (to instanceof Class && ((Class<?>) to).isAssignableFrom(from)) {
            return NoopConverter.INSTANCE;
        }
        TypeConverter<T> converter = TypeStore.STORE.get(from, to);
        if (null == converter) {
            converter = getConverter(to);
        }
        //兜底
        if (null == converter) {
            Map<Type, TypeConverter<?>> conversion = TypeStore.STORE.getAllSuitableConversion(from);
            for (Map.Entry<Type, TypeConverter<?>> entry : conversion.entrySet()) {
                if (entry.getKey().equals(to)) {
                    TypeStore.STORE.register(from, TypeUtils.getClass(to), entry.getValue());
                    converter = (TypeConverter<T>) entry.getValue();
                    break;
                }
            }
        }
        //兜底
        if (null == converter) {
            converter = o -> (T) TypeStore.getStore().to(o, TypeUtils.getClass(to));
        }
        return converter;
    }

    /**
     * @param type the type
     * @param <T>  the class
     * @return the target
     */
    @Nullable
    @SuppressWarnings("unchecked")
    private static <T> TypeConverter<T> getConverter(Type type) {
        TypeConverter<?> typeConverter = DEFAULT_TYPE_CONVERTER_MAP.get(type);
        if (typeConverter != null) {
            return (TypeConverter<T>) typeConverter;
        }
        return null;
    }

    public static Object convert(Type targetType, Object object) {
        return convert(targetType, object, null);
    }
}
