package com.sondertara.common.function;

import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;
import java.util.function.IntSupplier;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

/**
 * 提供者工具
 *
 * @author Jiahang Li
 * @version 1.0.0
 *  */
@SuppressWarnings("unchecked")
public class  Suppliers {

    private Suppliers() {
    }

    /**
     * null 提供者
     */
    public static final Supplier<?> NULL_SUPPLER = () -> null;

    public static final ByteSupplier BYTE_SUPPLIER = () -> (byte) 0;

    public static final ShortSupplier SHORT_SUPPLIER = () -> (short) 0;

    public static final IntSupplier INT_SUPPLIER = () -> 0;

    public static final LongSupplier LONG_SUPPLIER = () -> 0L;

    public static final FloatSupplier FLOAT_SUPPLIER = () -> 0F;

    public static final DoubleSupplier DOUBLE_SUPPLIER = () -> 0D;

    public static final BooleanSupplier BOOLEAN_SUPPLIER = () -> false;

    public static final CharSupplier CHAR_SUPPLIER = () -> (char) 0;

    // -------------------- getter --------------------

    public static <T> Supplier<T> nullSupplier() {
        return (Supplier<T>) NULL_SUPPLER;
    }


    /**
     * Null-safe call to {@link Supplier#get()}.
     *
     * @param <T> the type of results supplied by this supplier.
     * @param supplier the supplier or null.
     * @return Result of {@link Supplier#get()} or null.
     */
    public static <T> T get(final Supplier<T> supplier) {
        return supplier == null ? null : supplier.get();
    }

}
