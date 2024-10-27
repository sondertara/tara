package com.sondertara.common.collection;


import com.sondertara.common.base.Valid;
import com.sondertara.common.convert.ConvertUtils;
import com.sondertara.common.function.ByteSupplier;
import com.sondertara.common.function.CharSupplier;
import com.sondertara.common.function.FloatSupplier;
import com.sondertara.common.function.ShortSupplier;
import com.sondertara.common.function.Suppliers;
import com.sondertara.common.math.BigDecimals;
import com.sondertara.common.math.BigIntegers;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;
import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;
import java.util.function.IntSupplier;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

/**
 * 可转换的 map
 *
 * @author Jiahang Li
 * @version 1.0.0
 */
public interface MutableMap<K, V> extends Map<K, V> {

    default Byte getByte(K k) {
        return this.getByte(k, Suppliers.nullSupplier());
    }

    default Byte getByte(K k, Byte def) {
        return this.getByte(k, () -> def);
    }

    default Byte getByte(K k, Supplier<Byte> supplier) {
        Valid.notNull(supplier);
        V v = get(k);
        if (v == null) {
            return supplier.get();
        }
        return ConvertUtils.convert(Byte.class, v);
    }

    default byte getByteValue(K k) {
        return this.getByteValue(k, Suppliers.BYTE_SUPPLIER);
    }

    default byte getByteValue(K k, byte def) {
        return this.getByteValue(k, () -> def);
    }

    default byte getByteValue(K k, ByteSupplier supplier) {
        Valid.notNull(supplier);
        V v = get(k);
        if (v == null) {
            return supplier.getAsByte();
        }
        return ConvertUtils.convert(Byte.class, v);
    }

    default Short getShort(K k) {
        return this.getShort(k, Suppliers.nullSupplier());
    }

    default Short getShort(K k, Short def) {
        return this.getShort(k, () -> def);
    }

    default Short getShort(K k, Supplier<Short> supplier) {
        Valid.notNull(supplier);
        V v = get(k);
        if (v == null) {
            return supplier.get();
        }
        return ConvertUtils.convert(Short.class, v);
    }

    default short getShortValue(K k) {
        return this.getShortValue(k, Suppliers.SHORT_SUPPLIER);
    }

    default short getShortValue(K k, short def) {
        return this.getShortValue(k, () -> def);
    }

    default short getShortValue(K k, ShortSupplier supplier) {
        Valid.notNull(supplier);
        V v = get(k);
        if (v == null) {
            return supplier.getAsShort();
        }
        return ConvertUtils.convert(Short.class, v);
    }

    default Integer getInteger(K k) {
        return this.getInteger(k, Suppliers.nullSupplier());
    }

    default Integer getInteger(K k, Integer def) {
        return this.getInteger(k, () -> def);
    }

    default Integer getInteger(K k, Supplier<Integer> supplier) {
        Valid.notNull(supplier);
        V v = get(k);
        if (v == null) {
            return supplier.get();
        }
        return ConvertUtils.convert(Integer.class, v);
    }

    default int getIntValue(K k) {
        return this.getIntValue(k, Suppliers.INT_SUPPLIER);
    }

    default int getIntValue(K k, int def) {
        return this.getIntValue(k, () -> def);
    }

    default int getIntValue(K k, IntSupplier supplier) {
        Valid.notNull(supplier);
        V v = get(k);
        if (v == null) {
            return supplier.getAsInt();
        }
        return ConvertUtils.convert(Integer.class, v);
    }

    default Long getLong(K k) {
        return this.getLong(k, Suppliers.nullSupplier());
    }

    default Long getLong(K k, Long def) {
        return this.getLong(k, () -> def);
    }

    default Long getLong(K k, Supplier<Long> supplier) {
        Valid.notNull(supplier);
        V v = get(k);
        if (v == null) {
            return supplier.get();
        }
        return ConvertUtils.convert(Long.class, v);
    }

    default long getLongValue(K k) {
        return this.getLongValue(k, Suppliers.LONG_SUPPLIER);
    }

    default long getLongValue(K k, long def) {
        return this.getLongValue(k, () -> def);
    }

    default long getLongValue(K k, LongSupplier supplier) {
        Valid.notNull(supplier);
        V v = get(k);
        if (v == null) {
            return supplier.getAsLong();
        }
        return ConvertUtils.convert(Long.class, v);
    }

    default Float getFloat(K k) {
        return this.getFloat(k, Suppliers.nullSupplier());
    }

    default Float getFloat(K k, Float def) {
        return this.getFloat(k, () -> def);
    }

    default Float getFloat(K k, Supplier<Float> supplier) {
        Valid.notNull(supplier);
        V v = get(k);
        if (v == null) {
            return supplier.get();
        }
        return ConvertUtils.convert(Float.class, v);

    }

    default float getFloatValue(K k) {
        return this.getFloatValue(k, Suppliers.FLOAT_SUPPLIER);
    }

    default float getFloatValue(K k, float def) {
        return this.getFloatValue(k, () -> def);
    }

    default float getFloatValue(K k, FloatSupplier supplier) {
        Valid.notNull(supplier);
        V v = get(k);
        if (v == null) {
            return supplier.getAsFloat();
        }
        return ConvertUtils.convert(Float.class, v);
    }

    default Double getDouble(K k) {
        return this.getDouble(k, Suppliers.nullSupplier());
    }

    default Double getDouble(K k, Double def) {
        return this.getDouble(k, () -> def);
    }

    default Double getDouble(K k, Supplier<Double> supplier) {
        Valid.notNull(supplier);
        V v = get(k);
        if (v == null) {
            return supplier.get();
        }
        return ConvertUtils.convert(Double.class, v);
    }

    default double getDoubleValue(K k) {
        return this.getDoubleValue(k, Suppliers.DOUBLE_SUPPLIER);
    }

    default double getDoubleValue(K k, double def) {
        return this.getDoubleValue(k, () -> def);
    }

    default double getDoubleValue(K k, DoubleSupplier supplier) {
        Valid.notNull(supplier);
        V v = get(k);
        if (v == null) {
            return supplier.getAsDouble();
        }
        return ConvertUtils.convert(Double.class, v);
    }

    default Boolean getBoolean(K k) {
        return this.getBoolean(k, Suppliers.nullSupplier());
    }

    default Boolean getBoolean(K k, Boolean def) {
        return this.getBoolean(k, () -> def);
    }

    default Boolean getBoolean(K k, Supplier<Boolean> supplier) {
        Valid.notNull(supplier);
        V v = get(k);
        if (v == null) {
            return supplier.get();
        }
        return ConvertUtils.convert(Boolean.class, v);
    }

    default boolean getBooleanValue(K k) {
        return this.getBooleanValue(k, Suppliers.BOOLEAN_SUPPLIER);
    }

    default boolean getBooleanValue(K k, boolean def) {
        return this.getBooleanValue(k, () -> def);
    }

    default boolean getBooleanValue(K k, BooleanSupplier supplier) {
        Valid.notNull(supplier);
        V v = get(k);
        if (v == null) {
            return supplier.getAsBoolean();
        }
        return ConvertUtils.convert(Boolean.class, v);
    }

    default Character getCharacter(K k) {
        return this.getCharacter(k, Suppliers.nullSupplier());
    }

    default Character getCharacter(K k, Character def) {
        return this.getCharacter(k, () -> def);
    }

    default Character getCharacter(K k, Supplier<Character> supplier) {
        Valid.notNull(supplier);
        V v = get(k);
        if (v == null) {
            return supplier.get();
        }
        return ConvertUtils.convert(Character.class, v);
    }

    default char getCharValue(K k) {
        return this.getCharValue(k, Suppliers.CHAR_SUPPLIER);
    }

    default char getCharValue(K k, char def) {
        return this.getCharValue(k, () -> def);
    }

    default char getCharValue(K k, CharSupplier supplier) {
        Valid.notNull(supplier);
        V v = get(k);
        if (v == null) {
            return supplier.getAsChar();
        }
        return ConvertUtils.convert(Character.class, v);
    }

    default String getString(K k) {
        return this.getString(k, Suppliers.nullSupplier());
    }

    default String getString(K k, String def) {
        return this.getString(k, () -> def);
    }

    default String getString(K k, Supplier<String> supplier) {
        Valid.notNull(supplier);
        V v = get(k);
        if (v == null) {
            return supplier.get();
        }
        return ConvertUtils.convert(String.class, v);
    }

    default Date getDate(K k) {
        return this.getDate(k, Suppliers.nullSupplier());
    }

    default Date getDate(K k, Date def) {
        return this.getDate(k, () -> def);
    }

    default Date getDate(K k, Supplier<Date> supplier) {
        Valid.notNull(supplier);
        V v = get(k);
        if (v == null) {
            return supplier.get();
        }
        return ConvertUtils.convert(Date.class, v);
    }

    default LocalDateTime getLocalDateTime(K k) {
        return this.getLocalDateTime(k, Suppliers.nullSupplier());
    }

    default LocalDateTime getLocalDateTime(K k, LocalDateTime def) {
        return this.getLocalDateTime(k, () -> def);
    }

    default LocalDateTime getLocalDateTime(K k, Supplier<LocalDateTime> supplier) {
        Valid.notNull(supplier);
        V v = get(k);
        if (v == null) {
            return supplier.get();
        }
        return ConvertUtils.convert(LocalDateTime.class, v);
    }

    default LocalDate getLocalDate(K k) {
        return this.getLocalDate(k, Suppliers.nullSupplier());
    }

    default LocalDate getLocalDate(K k, LocalDate def) {
        return this.getLocalDate(k, () -> def);
    }

    default LocalDate getLocalDate(K k, Supplier<LocalDate> supplier) {
        Valid.notNull(supplier);
        V v = get(k);
        if (v == null) {
            return supplier.get();
        }
        return ConvertUtils.convert(LocalDate.class, v);
    }

    default BigDecimal getBigDecimal(K k) {
        return this.getBigDecimal(k, Suppliers.nullSupplier());
    }

    default BigDecimal getBigDecimal(K k, BigDecimal def) {
        return this.getBigDecimal(k, () -> def);
    }

    default BigDecimal getBigDecimal(K k, Supplier<BigDecimal> supplier) {
        Valid.notNull(supplier);
        V v = get(k);
        if (v == null) {
            return supplier.get();
        }
        return BigDecimals.toBigDecimal(v);
    }

    default BigInteger getBigInteger(K k) {
        return this.getBigInteger(k, Suppliers.nullSupplier());
    }

    default BigInteger getBigInteger(K k, BigInteger def) {
        return this.getBigInteger(k, () -> def);
    }

    default BigInteger getBigInteger(K k, Supplier<BigInteger> supplier) {
        Valid.notNull(supplier);
        V v = get(k);
        if (v == null) {
            return supplier.get();
        }
        return BigIntegers.toBigInteger(v);
    }

    default <E> E getObject(K k) {
        return this.getObject(k, Suppliers.nullSupplier());
    }

    default <E> E getObject(K k, E def) {
        return this.getObject(k, () -> def);
    }

    @SuppressWarnings("unchecked")
    default <E> E getObject(K k, Supplier<E> supplier) {
        Valid.notNull(supplier);
        V v = get(k);
        if (v == null) {
            return supplier.get();
        }
        return (E) v;
    }

    default V get(K k, V def) {
        return this.get(k, () -> def);
    }

    default V get(K k, Supplier<V> supplier) {
        Valid.notNull(supplier);
        V v = get(k);
        if (v == null) {
            return supplier.get();
        }
        return v;
    }

}
