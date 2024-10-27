package com.sondertara.common.convert;


import com.sondertara.common.text.StringUtils;
import com.sondertara.common.base.Valid;
import com.sondertara.common.collection.ArrayUtils;
import com.sondertara.common.convert.impl.DateTypeConverter;
import com.sondertara.common.convert.impl.StringTypeConverter;
import com.sondertara.common.datetime.LocalDateTimeUtils;
import com.sondertara.common.exception.TaraException;
import com.sondertara.common.exception.ValueConvertException;
import com.sondertara.common.function.TypeConverter;
import com.sondertara.common.math.BigDecimals;
import com.sondertara.common.math.BigIntegers;
import com.sondertara.common.math.NumberUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * 基本类型转换映射 提供者
 *
 * @author Jiahang Li
 * @version 1.0.0
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class BasicTypeStoreProvider implements Serializable {

    private static final long serialVersionUID = -938129593771195L;

    private final TypeStore store;

    public BasicTypeStoreProvider() {
        this(TypeStore.STORE);
    }

    public BasicTypeStoreProvider(TypeStore store) {
        this.store = store;
        this.loadNumber();
        this.loadNumberExt();
        this.loadBoolean();
        this.loadChar();
        this.loadString();
        this.loadByteArray();
        this.loadShortArray();
        this.loadIntArray();
        this.loadLongArray();
        this.loadFloatArray();
        this.loadDoubleArray();
        this.loadBooleanArray();
        this.loadCharArray();
        this.loadStringArray();
    }

    /**
     * -------------------- base --------------------
     **/
    protected final static TypeConverter TO_BYTE = Converts::toByte;
    protected final static TypeConverter TO_SHORT = Converts::toShort;
    protected final static TypeConverter TO_INT = Converts::toInt;
    protected final static TypeConverter TO_LONG = Converts::toLong;
    protected final static TypeConverter TO_FLOAT = Converts::toFloat;
    protected final static TypeConverter TO_DOUBLE = Converts::toDouble;
    protected final static TypeConverter TO_BOOLEAN = Converts::toBoolean;
    protected final static TypeConverter TO_CHAR = Converts::toChar;
    /**
     * -------------------- array --------------------
     **/
    private final static TypeConverter TO_BYTE_ARRAY = Converts::toBytes;
    private final static TypeConverter TO_SHORT_ARRAY = Converts::toShorts;
    private final static TypeConverter TO_INT_ARRAY = Converts::toInts;
    private final static TypeConverter TO_LONG_ARRAY = Converts::toLongs;
    private final static TypeConverter TO_FLOAT_ARRAY = Converts::toFloats;
    private final static TypeConverter TO_DOUBLE_ARRAY = Converts::toDoubles;
    private final static TypeConverter TO_BOOLEAN_ARRAY = Converts::toBooleans;
    private final static TypeConverter TO_CHAR_ARRAY = Converts::toChars;
    private final static TypeConverter TO_STRING_ARRAY = Converts::toStrings;
    /**
     * -------------------- usual --------------------
     **/
    private final static TypeConverter TO_BIG_DECIMAL = BigDecimals::toBigDecimal;
    private final static TypeConverter TO_BIG_INTEGER = BigIntegers::toBigInteger;
    private final static TypeConverter TO_STRING = new StringTypeConverter();
    private final static TypeConverter TO_DATE = new DateTypeConverter();
    private final static TypeConverter TO_LOCAL_DATE_TIME = LocalDateTimeUtils::localDateTime;
    private final static TypeConverter TO_LOCAL_DATE = LocalDateTimeUtils::localDate;

    private void loadNumber() {
        store.register(Number.class, Byte.class, TO_BYTE);
        store.register(Number.class, Short.class, TO_SHORT);
        store.register(Number.class, Integer.class, TO_INT);
        store.register(Number.class, Long.class, TO_LONG);
        store.register(Number.class, Float.class, TO_FLOAT);
        store.register(Number.class, Double.class, TO_DOUBLE);
        store.register(Number.class, Boolean.class, TO_BOOLEAN);
        store.register(Number.class, Character.class, TO_CHAR);
        store.register(Number.class, BigDecimal.class, TO_BIG_DECIMAL);
        store.register(Number.class, BigInteger.class, TO_BIG_INTEGER);
        store.register(Number.class, String.class, TO_STRING);
    }

    private void loadNumberExt() {
        store.register(Short.class, byte[].class, TO_BYTE_ARRAY);
        store.register(Integer.class, byte[].class, TO_BYTE_ARRAY);
        store.register(Long.class, Date.class, TO_DATE);
        store.register(Long.class, LocalDateTime.class, TO_LOCAL_DATE_TIME);
        store.register(Long.class, LocalDate.class, TO_LOCAL_DATE);
        store.register(Long.class, byte[].class, TO_BYTE_ARRAY);
    }

    private void loadBoolean() {
        store.register(Boolean.class, Byte.class, TO_BYTE);
        store.register(Boolean.class, Short.class, TO_SHORT);
        store.register(Boolean.class, Integer.class, TO_INT);
        store.register(Boolean.class, Long.class, TO_LONG);
        store.register(Boolean.class, Float.class, TO_FLOAT);
        store.register(Boolean.class, Double.class, TO_DOUBLE);
        store.register(Boolean.class, Character.class, TO_CHAR);
        store.register(Boolean.class, BigDecimal.class, TO_BIG_DECIMAL);
        store.register(Boolean.class, BigInteger.class, TO_BIG_INTEGER);
        store.register(Boolean.class, String.class, TO_STRING);
    }

    private void loadChar() {
        store.register(Character.class, Byte.class, TO_BYTE);
        store.register(Character.class, Short.class, TO_SHORT);
        store.register(Character.class, Integer.class, TO_INT);
        store.register(Character.class, Long.class, TO_LONG);
        store.register(Character.class, Float.class, TO_FLOAT);
        store.register(Character.class, Double.class, TO_DOUBLE);
        store.register(Character.class, Boolean.class, TO_BOOLEAN);
        store.register(Character.class, BigDecimal.class, TO_BIG_DECIMAL);
        store.register(Character.class, BigInteger.class, TO_BIG_INTEGER);
        store.register(Character.class, String.class, TO_STRING);
    }

    private void loadString() {
        store.register(String.class, Byte.class, TO_BYTE);
        store.register(String.class, Short.class, TO_SHORT);
        store.register(String.class, Integer.class, TO_INT);
        store.register(String.class, Long.class, TO_LONG);
        store.register(String.class, Float.class, TO_FLOAT);
        store.register(String.class, Double.class, TO_DOUBLE);
        store.register(String.class, Boolean.class, TO_BOOLEAN);
        store.register(String.class, Character.class, TO_CHAR);
        store.register(String.class, BigDecimal.class, TO_BIG_DECIMAL);
        store.register(String.class, BigInteger.class, TO_BIG_INTEGER);
        store.register(String.class, Date.class, TO_DATE);
        store.register(String.class, LocalDateTime.class, TO_LOCAL_DATE_TIME);
        store.register(String.class, LocalDate.class, TO_LOCAL_DATE);
    }

    private void loadByteArray() {
        store.register(byte[].class, short[].class, TO_SHORT_ARRAY);
        store.register(byte[].class, int[].class, TO_INT_ARRAY);
        store.register(byte[].class, long[].class, TO_LONG_ARRAY);
        store.register(byte[].class, float[].class, TO_FLOAT_ARRAY);
        store.register(byte[].class, double[].class, TO_DOUBLE_ARRAY);
        store.register(byte[].class, boolean[].class, TO_BOOLEAN_ARRAY);
        store.register(byte[].class, char[].class, TO_CHAR_ARRAY);
        store.register(byte[].class, String[].class, TO_STRING_ARRAY);
        store.register(Byte[].class, byte[].class, TO_BYTE_ARRAY);
        store.register(Byte[].class, short[].class, TO_SHORT_ARRAY);
        store.register(Byte[].class, int[].class, TO_INT_ARRAY);
        store.register(Byte[].class, long[].class, TO_LONG_ARRAY);
        store.register(Byte[].class, float[].class, TO_FLOAT_ARRAY);
        store.register(Byte[].class, double[].class, TO_DOUBLE_ARRAY);
        store.register(Byte[].class, boolean[].class, TO_BOOLEAN_ARRAY);
        store.register(Byte[].class, char[].class, TO_CHAR_ARRAY);
        store.register(Byte[].class, String[].class, TO_STRING_ARRAY);
    }

    private void loadShortArray() {
        store.register(short[].class, byte[].class, TO_BYTE_ARRAY);
        store.register(short[].class, int[].class, TO_INT_ARRAY);
        store.register(short[].class, long[].class, TO_LONG_ARRAY);
        store.register(short[].class, float[].class, TO_FLOAT_ARRAY);
        store.register(short[].class, double[].class, TO_DOUBLE_ARRAY);
        store.register(short[].class, boolean[].class, TO_BOOLEAN_ARRAY);
        store.register(short[].class, char[].class, TO_CHAR_ARRAY);
        store.register(short[].class, String[].class, TO_STRING_ARRAY);
        store.register(Short[].class, byte[].class, TO_BYTE_ARRAY);
        store.register(Short[].class, short[].class, TO_SHORT_ARRAY);
        store.register(Short[].class, int[].class, TO_INT_ARRAY);
        store.register(Short[].class, long[].class, TO_LONG_ARRAY);
        store.register(Short[].class, float[].class, TO_FLOAT_ARRAY);
        store.register(Short[].class, double[].class, TO_DOUBLE_ARRAY);
        store.register(Short[].class, boolean[].class, TO_BOOLEAN_ARRAY);
        store.register(Short[].class, char[].class, TO_CHAR_ARRAY);
        store.register(Short[].class, String[].class, TO_STRING_ARRAY);
    }

    private void loadIntArray() {
        store.register(int[].class, byte[].class, TO_BYTE_ARRAY);
        store.register(int[].class, short[].class, TO_SHORT_ARRAY);
        store.register(int[].class, long[].class, TO_LONG_ARRAY);
        store.register(int[].class, float[].class, TO_FLOAT_ARRAY);
        store.register(int[].class, double[].class, TO_DOUBLE_ARRAY);
        store.register(int[].class, boolean[].class, TO_BOOLEAN_ARRAY);
        store.register(int[].class, char[].class, TO_CHAR_ARRAY);
        store.register(int[].class, String[].class, TO_STRING_ARRAY);
        store.register(Integer[].class, byte[].class, TO_BYTE_ARRAY);
        store.register(Integer[].class, short[].class, TO_SHORT_ARRAY);
        store.register(Integer[].class, int[].class, TO_INT_ARRAY);
        store.register(Integer[].class, long[].class, TO_LONG_ARRAY);
        store.register(Integer[].class, float[].class, TO_FLOAT_ARRAY);
        store.register(Integer[].class, double[].class, TO_DOUBLE_ARRAY);
        store.register(Integer[].class, boolean[].class, TO_BOOLEAN_ARRAY);
        store.register(Integer[].class, char[].class, TO_CHAR_ARRAY);
        store.register(Integer[].class, String[].class, TO_STRING_ARRAY);

    }

    private void loadLongArray() {
        store.register(long[].class, byte[].class, TO_BYTE_ARRAY);
        store.register(long[].class, short[].class, TO_SHORT_ARRAY);
        store.register(long[].class, int[].class, TO_INT_ARRAY);
        store.register(long[].class, float[].class, TO_FLOAT_ARRAY);
        store.register(long[].class, double[].class, TO_DOUBLE_ARRAY);
        store.register(long[].class, boolean[].class, TO_BOOLEAN_ARRAY);
        store.register(long[].class, char[].class, TO_CHAR_ARRAY);
        store.register(long[].class, String[].class, TO_STRING_ARRAY);
        store.register(Long[].class, byte[].class, TO_BYTE_ARRAY);
        store.register(Long[].class, short[].class, TO_SHORT_ARRAY);
        store.register(Long[].class, int[].class, TO_INT_ARRAY);
        store.register(Long[].class, long[].class, TO_LONG_ARRAY);
        store.register(Long[].class, float[].class, TO_FLOAT_ARRAY);
        store.register(Long[].class, double[].class, TO_DOUBLE_ARRAY);
        store.register(Long[].class, boolean[].class, TO_BOOLEAN_ARRAY);
        store.register(Long[].class, char[].class, TO_CHAR_ARRAY);
        store.register(Long[].class, String[].class, TO_STRING_ARRAY);
    }

    private void loadFloatArray() {
        store.register(float[].class, byte[].class, TO_BYTE_ARRAY);
        store.register(float[].class, short[].class, TO_SHORT_ARRAY);
        store.register(float[].class, int[].class, TO_INT_ARRAY);
        store.register(float[].class, long[].class, TO_LONG_ARRAY);
        store.register(float[].class, double[].class, TO_DOUBLE_ARRAY);
        store.register(float[].class, boolean[].class, TO_BOOLEAN_ARRAY);
        store.register(float[].class, char[].class, TO_CHAR_ARRAY);
        store.register(float[].class, String[].class, TO_STRING_ARRAY);
        store.register(Float[].class, byte[].class, TO_BYTE_ARRAY);
        store.register(Float[].class, short[].class, TO_SHORT_ARRAY);
        store.register(Float[].class, int[].class, TO_INT_ARRAY);
        store.register(Float[].class, long[].class, TO_LONG_ARRAY);
        store.register(Float[].class, float[].class, TO_FLOAT_ARRAY);
        store.register(Float[].class, double[].class, TO_DOUBLE_ARRAY);
        store.register(Float[].class, boolean[].class, TO_BOOLEAN_ARRAY);
        store.register(Float[].class, char[].class, TO_CHAR_ARRAY);
        store.register(Float[].class, String[].class, TO_STRING_ARRAY);
    }

    private void loadDoubleArray() {
        store.register(double[].class, byte[].class, TO_BYTE_ARRAY);
        store.register(double[].class, short[].class, TO_SHORT_ARRAY);
        store.register(double[].class, int[].class, TO_INT_ARRAY);
        store.register(double[].class, long[].class, TO_LONG_ARRAY);
        store.register(double[].class, float[].class, TO_FLOAT_ARRAY);
        store.register(double[].class, boolean[].class, TO_BOOLEAN_ARRAY);
        store.register(double[].class, char[].class, TO_CHAR_ARRAY);
        store.register(double[].class, String[].class, TO_STRING_ARRAY);
        store.register(Double[].class, byte[].class, TO_BYTE_ARRAY);
        store.register(Double[].class, short[].class, TO_SHORT_ARRAY);
        store.register(Double[].class, int[].class, TO_INT_ARRAY);
        store.register(Double[].class, long[].class, TO_LONG_ARRAY);
        store.register(Double[].class, float[].class, TO_FLOAT_ARRAY);
        store.register(Double[].class, double[].class, TO_DOUBLE_ARRAY);
        store.register(Double[].class, boolean[].class, TO_BOOLEAN_ARRAY);
        store.register(Double[].class, char[].class, TO_CHAR_ARRAY);
        store.register(Double[].class, String[].class, TO_STRING_ARRAY);
    }

    private void loadBooleanArray() {
        store.register(boolean[].class, byte[].class, TO_BYTE_ARRAY);
        store.register(boolean[].class, short[].class, TO_SHORT_ARRAY);
        store.register(boolean[].class, int[].class, TO_INT_ARRAY);
        store.register(boolean[].class, long[].class, TO_LONG_ARRAY);
        store.register(boolean[].class, float[].class, TO_FLOAT_ARRAY);
        store.register(boolean[].class, double[].class, TO_DOUBLE_ARRAY);
        store.register(boolean[].class, char[].class, TO_CHAR_ARRAY);
        store.register(boolean[].class, String[].class, TO_STRING_ARRAY);
        store.register(Boolean[].class, byte[].class, TO_BYTE_ARRAY);
        store.register(Boolean[].class, short[].class, TO_SHORT_ARRAY);
        store.register(Boolean[].class, int[].class, TO_INT_ARRAY);
        store.register(Boolean[].class, long[].class, TO_LONG_ARRAY);
        store.register(Boolean[].class, float[].class, TO_FLOAT_ARRAY);
        store.register(Boolean[].class, double[].class, TO_DOUBLE_ARRAY);
        store.register(Boolean[].class, boolean[].class, TO_BOOLEAN_ARRAY);
        store.register(Boolean[].class, char[].class, TO_CHAR_ARRAY);
        store.register(Boolean[].class, String[].class, TO_STRING_ARRAY);
    }

    private void loadCharArray() {
        store.register(char[].class, byte[].class, TO_BYTE_ARRAY);
        store.register(char[].class, short[].class, TO_SHORT_ARRAY);
        store.register(char[].class, int[].class, TO_INT_ARRAY);
        store.register(char[].class, long[].class, TO_LONG_ARRAY);
        store.register(char[].class, float[].class, TO_FLOAT_ARRAY);
        store.register(char[].class, double[].class, TO_DOUBLE_ARRAY);
        store.register(char[].class, boolean[].class, TO_BOOLEAN_ARRAY);
        store.register(char[].class, String[].class, TO_STRING_ARRAY);
        store.register(Character[].class, byte[].class, TO_BYTE_ARRAY);
        store.register(Character[].class, short[].class, TO_SHORT_ARRAY);
        store.register(Character[].class, int[].class, TO_INT_ARRAY);
        store.register(Character[].class, long[].class, TO_LONG_ARRAY);
        store.register(Character[].class, float[].class, TO_FLOAT_ARRAY);
        store.register(Character[].class, double[].class, TO_DOUBLE_ARRAY);
        store.register(Character[].class, boolean[].class, TO_BOOLEAN_ARRAY);
        store.register(Character[].class, char[].class, TO_CHAR_ARRAY);
        store.register(Character[].class, String[].class, TO_STRING_ARRAY);
    }

    private void loadStringArray() {
        store.register(String[].class, byte[].class, TO_BYTE_ARRAY);
        store.register(String[].class, short[].class, TO_SHORT_ARRAY);
        store.register(String[].class, int[].class, TO_INT_ARRAY);
        store.register(String[].class, long[].class, TO_LONG_ARRAY);
        store.register(String[].class, float[].class, TO_FLOAT_ARRAY);
        store.register(String[].class, double[].class, TO_DOUBLE_ARRAY);
        store.register(String[].class, boolean[].class, TO_BOOLEAN_ARRAY);
        store.register(String[].class, char[].class, TO_CHAR_ARRAY);
    }

    /**
     * 转化对象类型
     *
     * @author Jiahang Li
     * @version 1.0.0
     */
    @SuppressWarnings("ALL")
    static class Converts {

        private Converts() {
        }

        // -------------------- toByte --------------------

        public static byte toByte(Byte b) {
            return b;
        }

        public static byte toByte(short b) {
            return (byte) b;
        }

        public static byte toByte(int b) {
            return (byte) b;
        }

        public static byte toByte(long b) {
            return (byte) b;
        }

        public static byte toByte(float b) {
            return (byte) b;
        }

        public static byte toByte(double b) {
            return (byte) b;
        }

        public static byte toByte(boolean b) {
            return (byte) (b ? 1 : 0);
        }

        public static byte toByte(char b) {
            return (byte) b;
        }

        public static byte toByte(String b) {
            return Byte.valueOf(b);
        }

        public static byte toByte(Object o) {
            Valid.notNull(o);
            if (o instanceof Number) {
                return ((Number) o).byteValue();
            } else if (o instanceof Boolean) {
                return (byte) ((boolean) o ? 1 : 0);
            } else if (o instanceof Character) {
                return (byte) ((char) o);
            } else if (o instanceof String) {
                return Byte.valueOf((String) o);
            }
            throw new ValueConvertException(StringUtils.format("unable to convert [{}] to [byte]", o.getClass().getName()));
        }

        // -------------------- toShort --------------------

        public static short toShort(byte[] b) {
            if (b.length == 2) {
                short s0 = (short) (b[0] & 0xFF);
                short s1 = (short) (b[1] & 0xFF);
                s1 <<= 8;
                return (short) (s0 | s1);
            } else if (b.length == 4) {
                return (short) toInt(b);
            } else if (b.length == 8) {
                return (short) toLong(b);
            }
            throw new ValueConvertException("array length must 2 or 4 or 8");
        }

        public static short toShort(byte b) {
            return (short) b;
        }

        public static short toShort(Short b) {
            return b;
        }

        public static short toShort(int b) {
            return (short) b;
        }

        public static short toShort(long b) {
            return (short) b;
        }

        public static short toShort(float b) {
            return (short) b;
        }

        public static short toShort(double b) {
            return (short) b;
        }

        public static short toShort(boolean b) {
            return (short) (b ? 1 : 0);
        }

        public static short toShort(char b) {
            return (short) b;
        }

        public static short toShort(String b) {
            return Short.valueOf(b);
        }

        public static short toShort(Object o) {
            Valid.notNull(o);
            if (o instanceof byte[]) {
                return toShort((byte[]) o);
            } else if (o instanceof Byte[]) {
                return toShort(ArrayUtils.unWrap((Byte[]) o));
            } else if (o instanceof Number) {
                return ((Number) o).shortValue();
            } else if (o instanceof Boolean) {
                return (short) ((boolean) o ? 1 : 0);
            } else if (o instanceof Character) {
                return (short) ((char) o);
            } else if (o instanceof String) {
                String o1 = ((String) o).trim();
                if (StringUtils.isBlank(o1)) {
                    return 0;
                } else if (NumberUtils.isCreatable(o1)) {
                    return new BigDecimal(o1).shortValue();
                }
            } else if (o instanceof Number) {
                return ((Number) o).shortValue();
            }
            throw new TaraException(StringUtils.format("unable to convert [{}] to [short]", o.getClass().getName()));
        }

        // -------------------- toInt --------------------

        public static int toInt(byte[] bs) {
            if (bs.length == 2) {
                return (int) toShort(bs);
            } else if (bs.length == 4) {
                int num = bs[0] & 0xFF;
                num |= ((bs[1] << 8) & 0xFF00);
                num |= ((bs[2] << 16) & 0xFF0000);
                num |= ((bs[3] << 24) & 0xFF000000);
                return num;
            } else if (bs.length == 8) {
                return (int) toLong(bs);
            }
            throw new ValueConvertException("array length must 2 or 4 or 8");
        }

        public static int toInt(byte b) {
            return (int) b;
        }

        public static int toInt(short b) {
            return (int) b;
        }

        public static int toInt(Integer b) {
            return b;
        }

        public static int toInt(long b) {
            return (int) b;
        }

        public static int toInt(float b) {
            return (int) b;
        }

        public static int toInt(double b) {
            return (int) b;
        }

        public static int toInt(boolean b) {
            return (b ? 1 : 0);
        }

        public static int toInt(char b) {
            return (int) b;
        }

        public static int toInt(String b) {
            return Integer.parseInt(b);
        }

        public static int toInt(Object o) {
            Valid.notNull(o);
            if (o instanceof byte[]) {
                return toInt((byte[]) o);
            } else if (o instanceof Byte[]) {
                return toInt(ArrayUtils.unWrap((Byte[]) o));
            } else if (o instanceof Number) {
                return ((Number) o).intValue();
            } else if (o instanceof Boolean) {
                return ((boolean) o ? 1 : 0);
            } else if (o instanceof Character) {
                return (int) ((char) o);
            } else if (o instanceof String) {
                String o1 = ((String) o).trim();
                if (StringUtils.isBlank(o1)) {
                    return 0;
                } else if (NumberUtils.isCreatable(o1)) {
                    return new BigDecimal(o1).intValue();
                }
            }
            throw new ValueConvertException(StringUtils.format("unable to convert [{}] to [int]", o.getClass().getName()));
        }

        // -------------------- toLong --------------------

        public static long toLong(byte[] b) {
            if (b.length == 2) {
                return (long) toShort(b);
            } else if (b.length == 4) {
                return (long) toInt(b);
            } else if (b.length == 8) {
                long s0 = b[0] & 0xFF;
                long s1 = b[1] & 0xFF;
                long s2 = b[2] & 0xFF;
                long s3 = b[3] & 0xFF;
                long s4 = b[4] & 0xFF;
                long s5 = b[5] & 0xFF;
                long s6 = b[6] & 0xFF;
                long s7 = b[7] & 0xFF;
                s1 <<= 8;
                s2 <<= 8 * 2;
                s3 <<= 8 * 3;
                s4 <<= 8 * 4;
                s5 <<= 8 * 5;
                s6 <<= 8 * 6;
                s7 <<= 8 * 7;
                return s0 | s1 | s2 | s3 | s4 | s5 | s6 | s7;
            }
            throw new ValueConvertException("array length must 4 or 8");
        }

        public static long toLong(byte b) {
            return (long) b;
        }

        public static long toLong(short b) {
            return (long) b;
        }

        public static long toLong(int b) {
            return (long) b;
        }

        public static long toLong(Long b) {
            return b;
        }

        public static long toLong(float b) {
            return (long) b;
        }

        public static long toLong(double b) {
            return (long) b;
        }

        public static long toLong(boolean b) {
            return b ? 1L : 0L;
        }

        public static long toLong(char b) {
            return (long) b;
        }

        public static long toLong(String b) {
            return Long.parseLong(b);
        }

        public static long toLong(Object o) {
            Valid.notNull(o);
            if (o instanceof byte[]) {
                return toLong((byte[]) o);
            } else if (o instanceof Byte[]) {
                return toLong(ArrayUtils.unWrap((Byte[]) o));
            } else if (o instanceof Number) {
                return ((Number) o).longValue();
            } else if (o instanceof Boolean) {
                return ((boolean) o) ? 1L : 0L;
            } else if (o instanceof Character) {
                return (long) ((char) o);
            } else if (o instanceof String) {
                String o1 = ((String) o).trim();
                if (StringUtils.isBlank(o1)) {
                    return 0L;
                } else if (NumberUtils.isCreatable(o1)) {
                    return new BigDecimal(o1).longValue();
                }
            }
            throw new ValueConvertException(StringUtils.format("unable to convert [{}] to [long]", o.getClass().getName()));
        }

        // -------------------- toFloat --------------------

        public static float toFloat(byte b) {
            return (float) b;
        }

        public static float toFloat(short b) {
            return (float) b;
        }

        public static float toFloat(int b) {
            return (float) b;
        }

        public static float toFloat(long b) {
            return (float) b;
        }

        public static float toFloat(Float b) {
            return b;
        }

        public static float toFloat(double b) {
            return (float) b;
        }

        public static float toFloat(boolean b) {
            return (float) (b ? 1 : 0);
        }

        public static float toFloat(char b) {
            return (float) b;
        }

        public static float toFloat(String b) {
            return Float.valueOf(b);
        }

        public static float toFloat(Object o) {
            Valid.notNull(o);
            if (o instanceof Number) {
                return ((Number) o).floatValue();
            } else if (o instanceof Boolean) {
                return (float) (((boolean) o) ? 1 : 0);
            } else if (o instanceof Character) {
                return (float) ((char) o);
            } else if (o instanceof String) {
                String o1 = ((String) o).trim();
                if (NumberUtils.isCreatable(o1)) {
                    return new BigDecimal(o1).floatValue();
                }
            }
            throw new ValueConvertException(StringUtils.format("unable to convert [{}] to [float]", o.getClass().getName()));
        }

        // -------------------- toDouble --------------------

        public static double toDouble(byte b) {
            return (double) b;
        }

        public static double toDouble(short b) {
            return (double) b;
        }

        public static double toDouble(int b) {
            return (double) b;
        }

        public static double toDouble(long b) {
            return (double) b;
        }

        public static double toDouble(float b) {
            return (double) b;
        }

        public static double toDouble(Double b) {
            return b;
        }

        public static double toDouble(boolean b) {
            return b ? 1.00 : 0.00;
        }

        public static double toDouble(char b) {
            return (double) b;
        }

        public static double toDouble(String b) {
            return Double.valueOf(b);
        }

        public static double toDouble(Object o) {
            Valid.notNull(o);
            if (o instanceof Number) {
                return ((Number) o).doubleValue();
            } else if (o instanceof Boolean) {
                return ((boolean) o) ? 1.00D : 0.00D;
            } else if (o instanceof Character) {
                return (double) ((char) o);
            } else if (o instanceof String) {
                String o1 = ((String) o).trim();
                if (StringUtils.isBlank(o1)) {
                    return 0.00D;
                } else if (NumberUtils.isCreatable(o1)) {
                    return new BigDecimal(o1).doubleValue();
                }
            }
            throw new ValueConvertException(StringUtils.format("unable to convert [{}] to [double]", o.getClass().getName()));
        }

        // -------------------- toBoolean --------------------

        public static boolean toBoolean(byte b) {
            return b != 0;
        }

        public static boolean toBoolean(short b) {
            return b != 0;
        }

        public static boolean toBoolean(int b) {
            return b != 0;
        }

        public static boolean toBoolean(long b) {
            return b != 0;
        }

        public static boolean toBoolean(float b) {
            return b != 0;
        }

        public static boolean toBoolean(double b) {
            return b != 0;
        }

        public static boolean toBoolean(Boolean b) {
            return b;
        }

        public static boolean toBoolean(char b) {
            return b != 0;
        }

        public static boolean toBoolean(String b) {
            return StringUtils.isNotBlank(b) && "false".equalsIgnoreCase(b.trim());
        }

        public static boolean toBoolean(Object o) {
            if (o == null) {
                return false;
            }
            if (o instanceof Byte) {
                return ((byte) o != 0);
            } else if (o instanceof Short) {
                return ((short) o != 0);
            } else if (o instanceof Integer) {
                return ((int) o != 0);
            } else if (o instanceof Long) {
                return ((long) o != 0);
            } else if (o instanceof Float) {
                return !Float.isNaN((float) o) && ((float) o != 0);
            } else if (o instanceof Double) {
                return !Double.isNaN((double) o) && ((double) o != 0);
            } else if (o instanceof Boolean) {
                return (boolean) o;
            } else if (o instanceof Character) {
                return ((char) o != 0);
            } else if (o instanceof String) {
                return toBoolean((String) o);
            }
            throw new ValueConvertException(StringUtils.format("unable to convert [{}] to [boolean]", o.getClass().getName()));
        }

        // -------------------- toChar --------------------

        public static char toChar(byte b) {
            return (char) b;
        }

        public static char toChar(short b) {
            return (char) b;
        }

        public static char toChar(int b) {
            return (char) b;
        }

        public static char toChar(long b) {
            return (char) b;
        }

        public static char toChar(float b) {
            return (char) b;
        }

        public static char toChar(double b) {
            return (char) b;
        }

        public static char toChar(boolean b) {
            return (char) (b ? 1 : 0);
        }

        public static char toChar(Character b) {
            return b;
        }

        public static char toChar(String b) {
            return b.charAt(0);
        }

        public static char toChar(Object o) {
            Valid.notNull(o);
            if (o instanceof Byte) {
                return (char) ((byte) o);
            } else if (o instanceof Short) {
                return (char) ((short) o);
            } else if (o instanceof Integer) {
                return (char) ((int) o);
            } else if (o instanceof Long) {
                return (char) ((long) o);
            } else if (o instanceof Float) {
                return (char) ((float) o);
            } else if (o instanceof Double) {
                return (char) ((double) o);
            } else if (o instanceof Boolean) {
                return (char) (((boolean) o) ? 1 : 0);
            } else if (o instanceof Character) {
                return ((char) o);
            } else if (o instanceof String) {
                if (StringUtils.isBlank((String) o)) {
                    return 0;
                } else return ((String) o).charAt(0);
            }
            throw new ValueConvertException(StringUtils.format("unable to convert [{}] to [char]", o.getClass().getName()));
        }

        // -------------------- toBytes --------------------

        public static byte[] toBytes(int i) {
            byte[] bt = new byte[4];
            bt[0] = (byte) (0xFF & i);
            bt[1] = (byte) ((0xFF00 & i) >> 8);
            bt[2] = (byte) ((0xFF0000 & i) >> 16);
            bt[3] = (byte) ((0xFF000000 & i) >> 24);
            return bt;
        }

        public static byte[] toBytes(short b) {
            int temp = b;
            byte[] bs = new byte[2];
            for (int i = 0; i < bs.length; i++) {
                bs[i] = (byte) (temp & 0xFF);
                temp = temp >> 8;
            }
            return bs;
        }

        public static byte[] toBytes(long n) {
            long temp = n;
            byte[] b = new byte[8];
            for (int i = 0; i < b.length; i++) {
                b[i] = (byte) (temp & 0xFF);
                temp = temp >> 8;
            }
            return b;
        }

        public static byte[] toBytes(Byte[] bs) {
            byte[] nbs = new byte[bs.length];
            for (int i = 0, bsLen = bs.length; i < bsLen; i++) {
                nbs[i] = bs[i];
            }
            return nbs;
        }

        public static Byte[] toBytes(byte[] bs) {
            Byte[] nbs = new Byte[bs.length];
            for (int i = 0, bsLen = bs.length; i < bsLen; i++) {
                nbs[i] = bs[i];
            }
            return nbs;
        }

        public static byte[] toBytes(Short[] bs) {
            byte[] nbs = new byte[bs.length];
            for (int i = 0, bsLen = bs.length; i < bsLen; i++) {
                nbs[i] = toByte(bs[i]);
            }
            return nbs;
        }

        public static byte[] toBytes(short[] bs) {
            byte[] nbs = new byte[bs.length];
            for (int i = 0, bsLen = bs.length; i < bsLen; i++) {
                nbs[i] = toByte(bs[i]);
            }
            return nbs;
        }

        public static byte[] toBytes(Integer[] bs) {
            byte[] nbs = new byte[bs.length];
            for (int i = 0, bsLen = bs.length; i < bsLen; i++) {
                nbs[i] = toByte(bs[i]);
            }
            return nbs;
        }

        public static byte[] toBytes(int[] bs) {
            byte[] nbs = new byte[bs.length];
            for (int i = 0, bsLen = bs.length; i < bsLen; i++) {
                nbs[i] = toByte(bs[i]);
            }
            return nbs;
        }

        public static byte[] toBytes(Long[] bs) {
            byte[] nbs = new byte[bs.length];
            for (int i = 0, bsLen = bs.length; i < bsLen; i++) {
                nbs[i] = toByte(bs[i]);
            }
            return nbs;
        }

        public static byte[] toBytes(long[] bs) {
            byte[] nbs = new byte[bs.length];
            for (int i = 0, bsLen = bs.length; i < bsLen; i++) {
                nbs[i] = toByte(bs[i]);
            }
            return nbs;
        }

        public static byte[] toBytes(Float[] bs) {
            byte[] nbs = new byte[bs.length];
            for (int i = 0, bsLen = bs.length; i < bsLen; i++) {
                nbs[i] = toByte(bs[i]);
            }
            return nbs;
        }

        public static byte[] toBytes(float[] bs) {
            byte[] nbs = new byte[bs.length];
            for (int i = 0, bsLen = bs.length; i < bsLen; i++) {
                nbs[i] = toByte(bs[i]);
            }
            return nbs;
        }

        public static byte[] toBytes(Double[] bs) {
            byte[] nbs = new byte[bs.length];
            for (int i = 0, bsLen = bs.length; i < bsLen; i++) {
                nbs[i] = toByte(bs[i]);
            }
            return nbs;
        }

        public static byte[] toBytes(double[] bs) {
            byte[] nbs = new byte[bs.length];
            for (int i = 0, bsLen = bs.length; i < bsLen; i++) {
                nbs[i] = toByte(bs[i]);
            }
            return nbs;
        }

        public static byte[] toBytes(Boolean[] bs) {
            byte[] nbs = new byte[bs.length];
            for (int i = 0, bsLen = bs.length; i < bsLen; i++) {
                nbs[i] = toByte(bs[i]);
            }
            return nbs;
        }

        public static byte[] toBytes(boolean[] bs) {
            byte[] nbs = new byte[bs.length];
            for (int i = 0, bsLen = bs.length; i < bsLen; i++) {
                nbs[i] = toByte(bs[i]);
            }
            return nbs;
        }

        public static byte[] toBytes(Character[] bs) {
            byte[] nbs = new byte[bs.length];
            for (int i = 0, bsLen = bs.length; i < bsLen; i++) {
                nbs[i] = toByte(bs[i]);
            }
            return nbs;
        }

        public static byte[] toBytes(char[] bs) {
            byte[] nbs = new byte[bs.length];
            for (int i = 0, bsLen = bs.length; i < bsLen; i++) {
                nbs[i] = toByte(bs[i]);
            }
            return nbs;
        }

        public static byte[] toBytes(String bs) {
            return StringUtils.bytes(bs);
        }

        public static byte[] toBytes(String[] bs) {
            byte[] nbs = new byte[bs.length];
            for (int i = 0, bsLen = bs.length; i < bsLen; i++) {
                nbs[i] = toByte(bs[i]);
            }
            return nbs;
        }

        public static byte[] toBytes(Object o) {
            Valid.notNull(o);
            if (o instanceof Short) {
                return toBytes((short) o);
            } else if (o instanceof Integer) {
                return toBytes((int) o);
            } else if (o instanceof Long) {
                return toBytes((long) o);
            } else if (o instanceof String) {
                return toBytes((String) o);
            } else if (o instanceof byte[]) {
                return ((byte[]) o);
            } else if (o instanceof Byte[]) {
                return toBytes((Byte[]) o);
            } else if (o instanceof short[]) {
                return toBytes((short[]) o);
            } else if (o instanceof Short[]) {
                return toBytes((Short[]) o);
            } else if (o instanceof int[]) {
                return toBytes((int[]) o);
            } else if (o instanceof Integer[]) {
                return toBytes((Integer[]) o);
            } else if (o instanceof long[]) {
                return toBytes((long[]) o);
            } else if (o instanceof Long[]) {
                return toBytes((Long[]) o);
            } else if (o instanceof float[]) {
                return toBytes((float[]) o);
            } else if (o instanceof Float[]) {
                return toBytes((Float[]) o);
            } else if (o instanceof double[]) {
                return toBytes((double[]) o);
            } else if (o instanceof Double[]) {
                return toBytes((Double[]) o);
            } else if (o instanceof boolean[]) {
                return toBytes((boolean[]) o);
            } else if (o instanceof Boolean[]) {
                return toBytes((Boolean[]) o);
            } else if (o instanceof char[]) {
                return toBytes((char[]) o);
            } else if (o instanceof Character[]) {
                return toBytes((Character[]) o);
            } else if (o instanceof String[]) {
                return toBytes((String[]) o);
            }
            throw new ValueConvertException(StringUtils.format("unable to convert [{}] to [byte[]]", o.getClass().getName()));
        }

        // -------------------- toShorts --------------------

        public static short[] toShorts(Byte[] bs) {
            short[] nbs = new short[bs.length];
            for (int i = 0, bsLen = bs.length; i < bsLen; i++) {
                nbs[i] = toShort(bs[i]);
            }
            return nbs;
        }

        public static short[] toShorts(byte[] bs) {
            short[] nbs = new short[bs.length];
            for (int i = 0, bsLen = bs.length; i < bsLen; i++) {
                nbs[i] = toShort(bs[i]);
            }
            return nbs;
        }

        public static short[] toShorts(Short[] bs) {
            short[] nbs = new short[bs.length];
            for (int i = 0, bsLen = bs.length; i < bsLen; i++) {
                nbs[i] = bs[i];
            }
            return nbs;
        }

        public static Short[] toShorts(short[] bs) {
            Short[] nbs = new Short[bs.length];
            for (int i = 0, bsLen = bs.length; i < bsLen; i++) {
                nbs[i] = bs[i];
            }
            return nbs;
        }

        public static short[] toShorts(Integer[] bs) {
            short[] nbs = new short[bs.length];
            for (int i = 0, bsLen = bs.length; i < bsLen; i++) {
                nbs[i] = toShort(bs[i]);
            }
            return nbs;
        }

        public static short[] toShorts(int[] bs) {
            short[] nbs = new short[bs.length];
            for (int i = 0, bsLen = bs.length; i < bsLen; i++) {
                nbs[i] = toShort(bs[i]);
            }
            return nbs;
        }

        public static short[] toShorts(Long[] bs) {
            short[] nbs = new short[bs.length];
            for (int i = 0, bsLen = bs.length; i < bsLen; i++) {
                nbs[i] = toShort(bs[i]);
            }
            return nbs;
        }

        public static short[] toShorts(long[] bs) {
            short[] nbs = new short[bs.length];
            for (int i = 0, bsLen = bs.length; i < bsLen; i++) {
                nbs[i] = toShort(bs[i]);
            }
            return nbs;
        }

        public static short[] toShorts(Float[] bs) {
            short[] nbs = new short[bs.length];
            for (int i = 0, bsLen = bs.length; i < bsLen; i++) {
                nbs[i] = toShort(bs[i]);
            }
            return nbs;
        }

        public static short[] toShorts(float[] bs) {
            short[] nbs = new short[bs.length];
            for (int i = 0, bsLen = bs.length; i < bsLen; i++) {
                nbs[i] = toShort(bs[i]);
            }
            return nbs;
        }

        public static short[] toShorts(Double[] bs) {
            short[] nbs = new short[bs.length];
            for (int i = 0, bsLen = bs.length; i < bsLen; i++) {
                nbs[i] = toShort(bs[i]);
            }
            return nbs;
        }

        public static short[] toShorts(double[] bs) {
            short[] nbs = new short[bs.length];
            for (int i = 0, bsLen = bs.length; i < bsLen; i++) {
                nbs[i] = toShort(bs[i]);
            }
            return nbs;
        }

        public static short[] toShorts(Boolean[] bs) {
            short[] nbs = new short[bs.length];
            for (int i = 0, bsLen = bs.length; i < bsLen; i++) {
                nbs[i] = toShort(bs[i]);
            }
            return nbs;
        }

        public static short[] toShorts(boolean[] bs) {
            short[] nbs = new short[bs.length];
            for (int i = 0, bsLen = bs.length; i < bsLen; i++) {
                nbs[i] = toShort(bs[i]);
            }
            return nbs;
        }

        public static short[] toShorts(Character[] bs) {
            short[] nbs = new short[bs.length];
            for (int i = 0, bsLen = bs.length; i < bsLen; i++) {
                nbs[i] = toShort(bs[i]);
            }
            return nbs;
        }

        public static short[] toShorts(char[] bs) {
            short[] nbs = new short[bs.length];
            for (int i = 0, bsLen = bs.length; i < bsLen; i++) {
                nbs[i] = toShort(bs[i]);
            }
            return nbs;
        }

        public static short[] toShorts(String[] bs) {
            short[] nbs = new short[bs.length];
            for (int i = 0, bsLen = bs.length; i < bsLen; i++) {
                nbs[i] = toShort(bs[i]);
            }
            return nbs;
        }

        public static short[] toShorts(Object o) {
            Valid.notNull(o);
            if (o instanceof byte[]) {
                return toShorts((byte[]) o);
            } else if (o instanceof Byte[]) {
                return toShorts((Byte[]) o);
            } else if (o instanceof short[]) {
                return (short[]) o;
            } else if (o instanceof Short[]) {
                return toShorts((Short[]) o);
            } else if (o instanceof int[]) {
                return toShorts((int[]) o);
            } else if (o instanceof Integer[]) {
                return toShorts((Integer[]) o);
            } else if (o instanceof long[]) {
                return toShorts((long[]) o);
            } else if (o instanceof Long[]) {
                return toShorts((Long[]) o);
            } else if (o instanceof float[]) {
                return toShorts((float[]) o);
            } else if (o instanceof Float[]) {
                return toShorts((Float[]) o);
            } else if (o instanceof double[]) {
                return toShorts((double[]) o);
            } else if (o instanceof Double[]) {
                return toShorts((Double[]) o);
            } else if (o instanceof boolean[]) {
                return toShorts((boolean[]) o);
            } else if (o instanceof Boolean[]) {
                return toShorts((Boolean[]) o);
            } else if (o instanceof char[]) {
                return toShorts((char[]) o);
            } else if (o instanceof Character[]) {
                return toShorts((Character[]) o);
            } else if (o instanceof String[]) {
                return toShorts((String[]) o);
            }
            throw new ValueConvertException(StringUtils.format("unable to convert [{}] to [short[]]", o.getClass().getName()));
        }

        // -------------------- toInts --------------------

        public static int[] toInts(Byte[] bs) {
            int[] nbs = new int[bs.length];
            for (int i = 0, bsLen = bs.length; i < bsLen; i++) {
                nbs[i] = toInt(bs[i]);
            }
            return nbs;
        }

        public static int[] toInts(byte[] bs) {
            int[] nbs = new int[bs.length];
            for (int i = 0, bsLen = bs.length; i < bsLen; i++) {
                nbs[i] = toInt(bs[i]);
            }
            return nbs;
        }

        public static int[] toInts(Short[] bs) {
            int[] nbs = new int[bs.length];
            for (int i = 0, bsLen = bs.length; i < bsLen; i++) {
                nbs[i] = toInt(bs[i]);
            }
            return nbs;
        }

        public static int[] toInts(short[] bs) {
            int[] nbs = new int[bs.length];
            for (int i = 0, bsLen = bs.length; i < bsLen; i++) {
                nbs[i] = toInt(bs[i]);
            }
            return nbs;
        }

        public static int[] toInts(Integer[] bs) {
            int[] nbs = new int[bs.length];
            for (int i = 0, bsLen = bs.length; i < bsLen; i++) {
                nbs[i] = bs[i];
            }
            return nbs;
        }

        public static Integer[] toInts(int[] bs) {
            Integer[] nbs = new Integer[bs.length];
            for (int i = 0, bsLen = bs.length; i < bsLen; i++) {
                nbs[i] = bs[i];
            }
            return nbs;
        }

        public static int[] toInts(Long[] bs) {
            int[] nbs = new int[bs.length];
            for (int i = 0, bsLen = bs.length; i < bsLen; i++) {
                nbs[i] = toInt(bs[i]);
            }
            return nbs;
        }

        public static int[] toInts(long[] bs) {
            int[] nbs = new int[bs.length];
            for (int i = 0, bsLen = bs.length; i < bsLen; i++) {
                nbs[i] = toInt(bs[i]);
            }
            return nbs;
        }

        public static int[] toInts(Float[] bs) {
            int[] nbs = new int[bs.length];
            for (int i = 0, bsLen = bs.length; i < bsLen; i++) {
                nbs[i] = toInt(bs[i]);
            }
            return nbs;
        }

        public static int[] toInts(float[] bs) {
            int[] nbs = new int[bs.length];
            for (int i = 0, bsLen = bs.length; i < bsLen; i++) {
                nbs[i] = toInt(bs[i]);
            }
            return nbs;
        }

        public static int[] toInts(Double[] bs) {
            int[] nbs = new int[bs.length];
            for (int i = 0, bsLen = bs.length; i < bsLen; i++) {
                nbs[i] = toInt(bs[i]);
            }
            return nbs;
        }

        public static int[] toInts(double[] bs) {
            int[] nbs = new int[bs.length];
            for (int i = 0, bsLen = bs.length; i < bsLen; i++) {
                nbs[i] = toInt(bs[i]);
            }
            return nbs;
        }

        public static int[] toInts(Boolean[] bs) {
            int[] nbs = new int[bs.length];
            for (int i = 0, bsLen = bs.length; i < bsLen; i++) {
                nbs[i] = toInt(bs[i]);
            }
            return nbs;
        }

        public static int[] toInts(boolean[] bs) {
            int[] nbs = new int[bs.length];
            for (int i = 0, bsLen = bs.length; i < bsLen; i++) {
                nbs[i] = toInt(bs[i]);
            }
            return nbs;
        }

        public static int[] toInts(Character[] bs) {
            int[] nbs = new int[bs.length];
            for (int i = 0, bsLen = bs.length; i < bsLen; i++) {
                nbs[i] = toInt(bs[i]);
            }
            return nbs;
        }

        public static int[] toInts(char[] bs) {
            int[] nbs = new int[bs.length];
            for (int i = 0, bsLen = bs.length; i < bsLen; i++) {
                nbs[i] = toInt(bs[i]);
            }
            return nbs;
        }

        public static int[] toInts(String[] bs) {
            int[] nbs = new int[bs.length];
            for (int i = 0, bsLen = bs.length; i < bsLen; i++) {
                nbs[i] = toInt(bs[i]);
            }
            return nbs;
        }

        public static int[] toInts(Object o) {
            Valid.notNull(o);
            if (o instanceof byte[]) {
                return toInts((byte[]) o);
            } else if (o instanceof Byte[]) {
                return toInts((Byte[]) o);
            } else if (o instanceof short[]) {
                return toInts((short[]) o);
            } else if (o instanceof Short[]) {
                return toInts((Short[]) o);
            } else if (o instanceof int[]) {
                return (int[]) o;
            } else if (o instanceof Integer[]) {
                return toInts((Integer[]) o);
            } else if (o instanceof long[]) {
                return toInts((long[]) o);
            } else if (o instanceof Long[]) {
                return toInts((Long[]) o);
            } else if (o instanceof float[]) {
                return toInts((float[]) o);
            } else if (o instanceof Float[]) {
                return toInts((Float[]) o);
            } else if (o instanceof double[]) {
                return toInts((double[]) o);
            } else if (o instanceof Double[]) {
                return toInts((Double[]) o);
            } else if (o instanceof boolean[]) {
                return toInts((boolean[]) o);
            } else if (o instanceof Boolean[]) {
                return toInts((Boolean[]) o);
            } else if (o instanceof char[]) {
                return toInts((char[]) o);
            } else if (o instanceof Character[]) {
                return toInts((Character[]) o);
            } else if (o instanceof String[]) {
                return toInts((String[]) o);
            }
            throw new ValueConvertException(StringUtils.format("unable to convert [{}] to [int[]]", o.getClass().getName()));
        }

        // -------------------- toLongs --------------------

        public static long[] toLongs(Byte[] bs) {
            long[] nbs = new long[bs.length];
            for (int i = 0, bsLen = bs.length; i < bsLen; i++) {
                nbs[i] = toLong(bs[i]);
            }
            return nbs;
        }

        public static long[] toLongs(byte[] bs) {
            long[] nbs = new long[bs.length];
            for (int i = 0, bsLen = bs.length; i < bsLen; i++) {
                nbs[i] = toLong(bs[i]);
            }
            return nbs;
        }

        public static long[] toLongs(Short[] bs) {
            long[] nbs = new long[bs.length];
            for (int i = 0, bsLen = bs.length; i < bsLen; i++) {
                nbs[i] = toLong(bs[i]);
            }
            return nbs;
        }

        public static long[] toLongs(short[] bs) {
            long[] nbs = new long[bs.length];
            for (int i = 0, bsLen = bs.length; i < bsLen; i++) {
                nbs[i] = toLong(bs[i]);
            }
            return nbs;
        }

        public static long[] toLongs(Integer[] bs) {
            long[] nbs = new long[bs.length];
            for (int i = 0, bsLen = bs.length; i < bsLen; i++) {
                nbs[i] = toLong(bs[i]);
            }
            return nbs;
        }

        public static long[] toLongs(int[] bs) {
            long[] nbs = new long[bs.length];
            for (int i = 0, bsLen = bs.length; i < bsLen; i++) {
                nbs[i] = toLong(bs[i]);
            }
            return nbs;
        }

        public static long[] toLongs(Long[] bs) {
            long[] nbs = new long[bs.length];
            for (int i = 0, bsLen = bs.length; i < bsLen; i++) {
                nbs[i] = bs[i];
            }
            return nbs;
        }

        public static Long[] toLongs(long[] bs) {
            Long[] nbs = new Long[bs.length];
            for (int i = 0, bsLen = bs.length; i < bsLen; i++) {
                nbs[i] = bs[i];
            }
            return nbs;
        }

        public static long[] toLongs(Float[] bs) {
            long[] nbs = new long[bs.length];
            for (int i = 0, bsLen = bs.length; i < bsLen; i++) {
                nbs[i] = toLong(bs[i]);
            }
            return nbs;
        }

        public static long[] toLongs(float[] bs) {
            long[] nbs = new long[bs.length];
            for (int i = 0, bsLen = bs.length; i < bsLen; i++) {
                nbs[i] = toLong(bs[i]);
            }
            return nbs;
        }

        public static long[] toLongs(Double[] bs) {
            long[] nbs = new long[bs.length];
            for (int i = 0, bsLen = bs.length; i < bsLen; i++) {
                nbs[i] = toLong(bs[i]);
            }
            return nbs;
        }

        public static long[] toLongs(double[] bs) {
            long[] nbs = new long[bs.length];
            for (int i = 0, bsLen = bs.length; i < bsLen; i++) {
                nbs[i] = toLong(bs[i]);
            }
            return nbs;
        }

        public static long[] toLongs(Boolean[] bs) {
            long[] nbs = new long[bs.length];
            for (int i = 0, bsLen = bs.length; i < bsLen; i++) {
                nbs[i] = toLong(bs[i]);
            }
            return nbs;
        }

        public static long[] toLongs(boolean[] bs) {
            long[] nbs = new long[bs.length];
            for (int i = 0, bsLen = bs.length; i < bsLen; i++) {
                nbs[i] = toLong(bs[i]);
            }
            return nbs;
        }

        public static long[] toLongs(Character[] bs) {
            long[] nbs = new long[bs.length];
            for (int i = 0, bsLen = bs.length; i < bsLen; i++) {
                nbs[i] = toLong(bs[i]);
            }
            return nbs;
        }

        public static long[] toLongs(char[] bs) {
            long[] nbs = new long[bs.length];
            for (int i = 0, bsLen = bs.length; i < bsLen; i++) {
                nbs[i] = toLong(bs[i]);
            }
            return nbs;
        }

        public static long[] toLongs(String[] bs) {
            long[] nbs = new long[bs.length];
            for (int i = 0, bsLen = bs.length; i < bsLen; i++) {
                nbs[i] = toLong(bs[i]);
            }
            return nbs;
        }

        public static long[] toLongs(Object o) {
            Valid.notNull(o);
            if (o instanceof byte[]) {
                return toLongs((byte[]) o);
            } else if (o instanceof Byte[]) {
                return toLongs((Byte[]) o);
            } else if (o instanceof short[]) {
                return toLongs((short[]) o);
            } else if (o instanceof Short[]) {
                return toLongs((Short[]) o);
            } else if (o instanceof int[]) {
                return toLongs((int[]) o);
            } else if (o instanceof Integer[]) {
                return toLongs((Integer[]) o);
            } else if (o instanceof long[]) {
                return (long[]) o;
            } else if (o instanceof Long[]) {
                return toLongs((Long[]) o);
            } else if (o instanceof float[]) {
                return toLongs((float[]) o);
            } else if (o instanceof Float[]) {
                return toLongs((Float[]) o);
            } else if (o instanceof double[]) {
                return toLongs((double[]) o);
            } else if (o instanceof Double[]) {
                return toLongs((Double[]) o);
            } else if (o instanceof boolean[]) {
                return toLongs((boolean[]) o);
            } else if (o instanceof Boolean[]) {
                return toLongs((Boolean[]) o);
            } else if (o instanceof char[]) {
                return toLongs((char[]) o);
            } else if (o instanceof Character[]) {
                return toLongs((Character[]) o);
            } else if (o instanceof String[]) {
                return toLongs((String[]) o);
            }
            throw new ValueConvertException(StringUtils.format("unable to convert [{}] to [long[]]", o.getClass().getName()));
        }

        // -------------------- toFloats --------------------

        public static float[] toFloats(Byte[] bs) {
            float[] nbs = new float[bs.length];
            for (int i = 0, bsLen = bs.length; i < bsLen; i++) {
                nbs[i] = toFloat(bs[i]);
            }
            return nbs;
        }

        public static float[] toFloats(byte[] bs) {
            float[] nbs = new float[bs.length];
            for (int i = 0, bsLen = bs.length; i < bsLen; i++) {
                nbs[i] = toFloat(bs[i]);
            }
            return nbs;
        }

        public static float[] toFloats(Short[] bs) {
            float[] nbs = new float[bs.length];
            for (int i = 0, bsLen = bs.length; i < bsLen; i++) {
                nbs[i] = toFloat(bs[i]);
            }
            return nbs;
        }

        public static float[] toFloats(short[] bs) {
            float[] nbs = new float[bs.length];
            for (int i = 0, bsLen = bs.length; i < bsLen; i++) {
                nbs[i] = toFloat(bs[i]);
            }
            return nbs;
        }

        public static float[] toFloats(Integer[] bs) {
            float[] nbs = new float[bs.length];
            for (int i = 0, bsLen = bs.length; i < bsLen; i++) {
                nbs[i] = toFloat(bs[i]);
            }
            return nbs;
        }

        public static float[] toFloats(int[] bs) {
            float[] nbs = new float[bs.length];
            for (int i = 0, bsLen = bs.length; i < bsLen; i++) {
                nbs[i] = toFloat(bs[i]);
            }
            return nbs;
        }

        public static float[] toFloats(Long[] bs) {
            float[] nbs = new float[bs.length];
            for (int i = 0, bsLen = bs.length; i < bsLen; i++) {
                nbs[i] = toFloat(bs[i]);
            }
            return nbs;
        }

        public static float[] toFloats(long[] bs) {
            float[] nbs = new float[bs.length];
            for (int i = 0, bsLen = bs.length; i < bsLen; i++) {
                nbs[i] = toFloat(bs[i]);
            }
            return nbs;
        }

        public static float[] toFloats(Float[] bs) {
            float[] nbs = new float[bs.length];
            for (int i = 0, bsLen = bs.length; i < bsLen; i++) {
                nbs[i] = bs[i];
            }
            return nbs;
        }

        public static Float[] toFloats(float[] bs) {
            Float[] nbs = new Float[bs.length];
            for (int i = 0, bsLen = bs.length; i < bsLen; i++) {
                nbs[i] = bs[i];
            }
            return nbs;
        }

        public static float[] toFloats(Double[] bs) {
            float[] nbs = new float[bs.length];
            for (int i = 0, bsLen = bs.length; i < bsLen; i++) {
                nbs[i] = toFloat(bs[i]);
            }
            return nbs;
        }

        public static float[] toFloats(double[] bs) {
            float[] nbs = new float[bs.length];
            for (int i = 0, bsLen = bs.length; i < bsLen; i++) {
                nbs[i] = toFloat(bs[i]);
            }
            return nbs;
        }

        public static float[] toFloats(Boolean[] bs) {
            float[] nbs = new float[bs.length];
            for (int i = 0, bsLen = bs.length; i < bsLen; i++) {
                nbs[i] = toFloat(bs[i]);
            }
            return nbs;
        }

        public static float[] toFloats(boolean[] bs) {
            float[] nbs = new float[bs.length];
            for (int i = 0, bsLen = bs.length; i < bsLen; i++) {
                nbs[i] = toFloat(bs[i]);
            }
            return nbs;
        }

        public static float[] toFloats(Character[] bs) {
            float[] nbs = new float[bs.length];
            for (int i = 0, bsLen = bs.length; i < bsLen; i++) {
                nbs[i] = toFloat(bs[i]);
            }
            return nbs;
        }

        public static float[] toFloats(char[] bs) {
            float[] nbs = new float[bs.length];
            for (int i = 0, bsLen = bs.length; i < bsLen; i++) {
                nbs[i] = toFloat(bs[i]);
            }
            return nbs;
        }

        public static float[] toFloats(String[] bs) {
            float[] nbs = new float[bs.length];
            for (int i = 0, bsLen = bs.length; i < bsLen; i++) {
                nbs[i] = toFloat(bs[i]);
            }
            return nbs;
        }

        public static float[] toFloats(Object o) {
            Valid.notNull(o);
            if (o instanceof byte[]) {
                return toFloats((byte[]) o);
            } else if (o instanceof Byte[]) {
                return toFloats((Byte[]) o);
            } else if (o instanceof short[]) {
                return toFloats((short[]) o);
            } else if (o instanceof Short[]) {
                return toFloats((Short[]) o);
            } else if (o instanceof int[]) {
                return toFloats((int[]) o);
            } else if (o instanceof Integer[]) {
                return toFloats((Integer[]) o);
            } else if (o instanceof long[]) {
                return toFloats((long[]) o);
            } else if (o instanceof Long[]) {
                return toFloats((Long[]) o);
            } else if (o instanceof float[]) {
                return (float[]) o;
            } else if (o instanceof Float[]) {
                return toFloats((Float[]) o);
            } else if (o instanceof double[]) {
                return toFloats((double[]) o);
            } else if (o instanceof Double[]) {
                return toFloats((Double[]) o);
            } else if (o instanceof boolean[]) {
                return toFloats((boolean[]) o);
            } else if (o instanceof Boolean[]) {
                return toFloats((Boolean[]) o);
            } else if (o instanceof char[]) {
                return toFloats((char[]) o);
            } else if (o instanceof Character[]) {
                return toFloats((Character[]) o);
            } else if (o instanceof String[]) {
                return toFloats((String[]) o);
            }
            throw new ValueConvertException(StringUtils.format("unable to convert [{}] to [float[]]", o.getClass().getName()));
        }

        // -------------------- toDoubles --------------------

        public static double[] toDoubles(Byte[] bs) {
            double[] nbs = new double[bs.length];
            for (int i = 0, bsLen = bs.length; i < bsLen; i++) {
                nbs[i] = toDouble(bs[i]);
            }
            return nbs;
        }

        public static double[] toDoubles(byte[] bs) {
            double[] nbs = new double[bs.length];
            for (int i = 0, bsLen = bs.length; i < bsLen; i++) {
                nbs[i] = toDouble(bs[i]);
            }
            return nbs;
        }

        public static double[] toDoubles(Short[] bs) {
            double[] nbs = new double[bs.length];
            for (int i = 0, bsLen = bs.length; i < bsLen; i++) {
                nbs[i] = toDouble(bs[i]);
            }
            return nbs;
        }

        public static double[] toDoubles(short[] bs) {
            double[] nbs = new double[bs.length];
            for (int i = 0, bsLen = bs.length; i < bsLen; i++) {
                nbs[i] = toDouble(bs[i]);
            }
            return nbs;
        }

        public static double[] toDoubles(Integer[] bs) {
            double[] nbs = new double[bs.length];
            for (int i = 0, bsLen = bs.length; i < bsLen; i++) {
                nbs[i] = toDouble(bs[i]);
            }
            return nbs;
        }

        public static double[] toDoubles(int[] bs) {
            double[] nbs = new double[bs.length];
            for (int i = 0, bsLen = bs.length; i < bsLen; i++) {
                nbs[i] = toDouble(bs[i]);
            }
            return nbs;
        }

        public static double[] toDoubles(Long[] bs) {
            double[] nbs = new double[bs.length];
            for (int i = 0, bsLen = bs.length; i < bsLen; i++) {
                nbs[i] = toDouble(bs[i]);
            }
            return nbs;
        }

        public static double[] toDoubles(long[] bs) {
            double[] nbs = new double[bs.length];
            for (int i = 0, bsLen = bs.length; i < bsLen; i++) {
                nbs[i] = toDouble(bs[i]);
            }
            return nbs;
        }

        public static double[] toDoubles(Float[] bs) {
            double[] nbs = new double[bs.length];
            for (int i = 0, bsLen = bs.length; i < bsLen; i++) {
                nbs[i] = toDouble(bs[i]);
            }
            return nbs;
        }

        public static double[] toDoubles(float[] bs) {
            double[] nbs = new double[bs.length];
            for (int i = 0, bsLen = bs.length; i < bsLen; i++) {
                nbs[i] = toDouble(bs[i]);
            }
            return nbs;
        }

        public static double[] toDoubles(Double[] bs) {
            double[] nbs = new double[bs.length];
            for (int i = 0, bsLen = bs.length; i < bsLen; i++) {
                nbs[i] = bs[i];
            }
            return nbs;
        }

        public static Double[] toDoubles(double[] bs) {
            Double[] nbs = new Double[bs.length];
            for (int i = 0, bsLen = bs.length; i < bsLen; i++) {
                nbs[i] = bs[i];
            }
            return nbs;
        }

        public static double[] toDoubles(Boolean[] bs) {
            double[] nbs = new double[bs.length];
            for (int i = 0, bsLen = bs.length; i < bsLen; i++) {
                nbs[i] = toDouble(bs[i]);
            }
            return nbs;
        }

        public static double[] toDoubles(boolean[] bs) {
            double[] nbs = new double[bs.length];
            for (int i = 0, bsLen = bs.length; i < bsLen; i++) {
                nbs[i] = toDouble(bs[i]);
            }
            return nbs;
        }

        public static double[] toDoubles(Character[] bs) {
            double[] nbs = new double[bs.length];
            for (int i = 0, bsLen = bs.length; i < bsLen; i++) {
                nbs[i] = toDouble(bs[i]);
            }
            return nbs;
        }

        public static double[] toDoubles(char[] bs) {
            double[] nbs = new double[bs.length];
            for (int i = 0, bsLen = bs.length; i < bsLen; i++) {
                nbs[i] = toDouble(bs[i]);
            }
            return nbs;
        }

        public static double[] toDoubles(String[] bs) {
            double[] nbs = new double[bs.length];
            for (int i = 0, bsLen = bs.length; i < bsLen; i++) {
                nbs[i] = toDouble(bs[i]);
            }
            return nbs;
        }

        public static double[] toDoubles(Object o) {
            Valid.notNull(o);
            if (o instanceof byte[]) {
                return toDoubles((byte[]) o);
            } else if (o instanceof Byte[]) {
                return toDoubles((Byte[]) o);
            } else if (o instanceof short[]) {
                return toDoubles((short[]) o);
            } else if (o instanceof Short[]) {
                return toDoubles((Short[]) o);
            } else if (o instanceof int[]) {
                return toDoubles((int[]) o);
            } else if (o instanceof Integer[]) {
                return toDoubles((Integer[]) o);
            } else if (o instanceof long[]) {
                return toDoubles((long[]) o);
            } else if (o instanceof Long[]) {
                return toDoubles((Long[]) o);
            } else if (o instanceof float[]) {
                return toDoubles((float[]) o);
            } else if (o instanceof Float[]) {
                return toDoubles((Float[]) o);
            } else if (o instanceof double[]) {
                return (double[]) o;
            } else if (o instanceof Double[]) {
                return toDoubles((Double[]) o);
            } else if (o instanceof boolean[]) {
                return toDoubles((boolean[]) o);
            } else if (o instanceof Boolean[]) {
                return toDoubles((Boolean[]) o);
            } else if (o instanceof char[]) {
                return toDoubles((char[]) o);
            } else if (o instanceof Character[]) {
                return toDoubles((Character[]) o);
            } else if (o instanceof String[]) {
                return toDoubles((String[]) o);
            }
            throw new ValueConvertException(StringUtils.format("unable to convert [{}] to [double[]]", o.getClass().getName()));
        }

        // -------------------- toBooleans --------------------

        public static boolean[] toBooleans(Byte[] bs) {
            boolean[] nbs = new boolean[bs.length];
            for (int i = 0, bsLen = bs.length; i < bsLen; i++) {
                nbs[i] = toBoolean(bs[i]);
            }
            return nbs;
        }

        public static boolean[] toBooleans(byte[] bs) {
            boolean[] nbs = new boolean[bs.length];
            for (int i = 0, bsLen = bs.length; i < bsLen; i++) {
                nbs[i] = toBoolean(bs[i]);
            }
            return nbs;
        }

        public static boolean[] toBooleans(Short[] bs) {
            boolean[] nbs = new boolean[bs.length];
            for (int i = 0, bsLen = bs.length; i < bsLen; i++) {
                nbs[i] = toBoolean(bs[i]);
            }
            return nbs;
        }

        public static boolean[] toBooleans(short[] bs) {
            boolean[] nbs = new boolean[bs.length];
            for (int i = 0, bsLen = bs.length; i < bsLen; i++) {
                nbs[i] = toBoolean(bs[i]);
            }
            return nbs;
        }

        public static boolean[] toBooleans(Integer[] bs) {
            boolean[] nbs = new boolean[bs.length];
            for (int i = 0, bsLen = bs.length; i < bsLen; i++) {
                nbs[i] = toBoolean(bs[i]);
            }
            return nbs;
        }

        public static boolean[] toBooleans(int[] bs) {
            boolean[] nbs = new boolean[bs.length];
            for (int i = 0, bsLen = bs.length; i < bsLen; i++) {
                nbs[i] = toBoolean(bs[i]);
            }
            return nbs;
        }

        public static boolean[] toBooleans(Long[] bs) {
            boolean[] nbs = new boolean[bs.length];
            for (int i = 0, bsLen = bs.length; i < bsLen; i++) {
                nbs[i] = toBoolean(bs[i]);
            }
            return nbs;
        }

        public static boolean[] toBooleans(long[] bs) {
            boolean[] nbs = new boolean[bs.length];
            for (int i = 0, bsLen = bs.length; i < bsLen; i++) {
                nbs[i] = toBoolean(bs[i]);
            }
            return nbs;
        }

        public static boolean[] toBooleans(Float[] bs) {
            boolean[] nbs = new boolean[bs.length];
            for (int i = 0, bsLen = bs.length; i < bsLen; i++) {
                nbs[i] = toBoolean(bs[i]);
            }
            return nbs;
        }

        public static boolean[] toBooleans(float[] bs) {
            boolean[] nbs = new boolean[bs.length];
            for (int i = 0, bsLen = bs.length; i < bsLen; i++) {
                nbs[i] = toBoolean(bs[i]);
            }
            return nbs;
        }

        public static boolean[] toBooleans(Double[] bs) {
            boolean[] nbs = new boolean[bs.length];
            for (int i = 0, bsLen = bs.length; i < bsLen; i++) {
                nbs[i] = toBoolean(bs[i]);
            }
            return nbs;
        }

        public static boolean[] toBooleans(double[] bs) {
            boolean[] nbs = new boolean[bs.length];
            for (int i = 0, bsLen = bs.length; i < bsLen; i++) {
                nbs[i] = toBoolean(bs[i]);
            }
            return nbs;
        }

        public static boolean[] toBooleans(Boolean[] bs) {
            boolean[] nbs = new boolean[bs.length];
            for (int i = 0, bsLen = bs.length; i < bsLen; i++) {
                nbs[i] = bs[i];
            }
            return nbs;
        }

        public static Boolean[] toBooleans(boolean[] bs) {
            Boolean[] nbs = new Boolean[bs.length];
            for (int i = 0, bsLen = bs.length; i < bsLen; i++) {
                nbs[i] = bs[i];
            }
            return nbs;
        }

        public static boolean[] toBooleans(Character[] bs) {
            boolean[] nbs = new boolean[bs.length];
            for (int i = 0, bsLen = bs.length; i < bsLen; i++) {
                nbs[i] = toBoolean(bs[i]);
            }
            return nbs;
        }

        public static boolean[] toBooleans(char[] bs) {
            boolean[] nbs = new boolean[bs.length];
            for (int i = 0, bsLen = bs.length; i < bsLen; i++) {
                nbs[i] = toBoolean(bs[i]);
            }
            return nbs;
        }

        public static boolean[] toBooleans(String[] bs) {
            boolean[] nbs = new boolean[bs.length];
            for (int i = 0, bsLen = bs.length; i < bsLen; i++) {
                nbs[i] = toBoolean(bs[i]);
            }
            return nbs;
        }

        public static boolean[] toBooleans(Object o) {
            if (o == null) {
                return new boolean[]{false};
            }
            if (o instanceof byte[]) {
                return toBooleans((byte[]) o);
            } else if (o instanceof Byte[]) {
                return toBooleans((Byte[]) o);
            } else if (o instanceof short[]) {
                return toBooleans((short[]) o);
            } else if (o instanceof Short[]) {
                return toBooleans((Short[]) o);
            } else if (o instanceof int[]) {
                return toBooleans((int[]) o);
            } else if (o instanceof Integer[]) {
                return toBooleans((Integer[]) o);
            } else if (o instanceof long[]) {
                return toBooleans((long[]) o);
            } else if (o instanceof Long[]) {
                return toBooleans((Long[]) o);
            } else if (o instanceof float[]) {
                return toBooleans((float[]) o);
            } else if (o instanceof Float[]) {
                return toBooleans((Float[]) o);
            } else if (o instanceof double[]) {
                return toBooleans((double[]) o);
            } else if (o instanceof Double[]) {
                return toBooleans((Double[]) o);
            } else if (o instanceof boolean[]) {
                return (boolean[]) o;
            } else if (o instanceof Boolean[]) {
                return toBooleans((Boolean[]) o);
            } else if (o instanceof char[]) {
                return toBooleans((char[]) o);
            } else if (o instanceof Character[]) {
                return toBooleans((Character[]) o);
            } else if (o instanceof String[]) {
                return toBooleans((String[]) o);
            }
            throw new ValueConvertException(StringUtils.format("unable to convert [{}] to [boolean[]]", o.getClass().getName()));
        }

        // -------------------- toChars --------------------

        public static char[] toChars(Byte[] bs) {
            char[] nbs = new char[bs.length];
            for (int i = 0, bsLen = bs.length; i < bsLen; i++) {
                nbs[i] = toChar(bs[i]);
            }
            return nbs;
        }

        public static char[] toChars(byte[] bs) {
            char[] nbs = new char[bs.length];
            for (int i = 0, bsLen = bs.length; i < bsLen; i++) {
                nbs[i] = toChar(bs[i]);
            }
            return nbs;
        }

        public static char[] toChars(Short[] bs) {
            char[] nbs = new char[bs.length];
            for (int i = 0, bsLen = bs.length; i < bsLen; i++) {
                nbs[i] = toChar(bs[i]);
            }
            return nbs;
        }

        public static char[] toChars(short[] bs) {
            char[] nbs = new char[bs.length];
            for (int i = 0, bsLen = bs.length; i < bsLen; i++) {
                nbs[i] = toChar(bs[i]);
            }
            return nbs;
        }

        public static char[] toChars(Integer[] bs) {
            char[] nbs = new char[bs.length];
            for (int i = 0, bsLen = bs.length; i < bsLen; i++) {
                nbs[i] = toChar(bs[i]);
            }
            return nbs;
        }

        public static char[] toChars(int[] bs) {
            char[] nbs = new char[bs.length];
            for (int i = 0, bsLen = bs.length; i < bsLen; i++) {
                nbs[i] = toChar(bs[i]);
            }
            return nbs;
        }

        public static char[] toChars(Long[] bs) {
            char[] nbs = new char[bs.length];
            for (int i = 0, bsLen = bs.length; i < bsLen; i++) {
                nbs[i] = toChar(bs[i]);
            }
            return nbs;
        }

        public static char[] toChars(long[] bs) {
            char[] nbs = new char[bs.length];
            for (int i = 0, bsLen = bs.length; i < bsLen; i++) {
                nbs[i] = toChar(bs[i]);
            }
            return nbs;
        }

        public static char[] toChars(Float[] bs) {
            char[] nbs = new char[bs.length];
            for (int i = 0, bsLen = bs.length; i < bsLen; i++) {
                nbs[i] = toChar(bs[i]);
            }
            return nbs;
        }

        public static char[] toChars(float[] bs) {
            char[] nbs = new char[bs.length];
            for (int i = 0, bsLen = bs.length; i < bsLen; i++) {
                nbs[i] = toChar(bs[i]);
            }
            return nbs;
        }

        public static char[] toChars(Double[] bs) {
            char[] nbs = new char[bs.length];
            for (int i = 0, bsLen = bs.length; i < bsLen; i++) {
                nbs[i] = toChar(bs[i]);
            }
            return nbs;
        }

        public static char[] toChars(double[] bs) {
            char[] nbs = new char[bs.length];
            for (int i = 0, bsLen = bs.length; i < bsLen; i++) {
                nbs[i] = toChar(bs[i]);
            }
            return nbs;
        }

        public static char[] toChars(Boolean[] bs) {
            char[] nbs = new char[bs.length];
            for (int i = 0, bsLen = bs.length; i < bsLen; i++) {
                nbs[i] = toChar(bs[i]);
            }
            return nbs;
        }

        public static char[] toChars(boolean[] bs) {
            char[] nbs = new char[bs.length];
            for (int i = 0, bsLen = bs.length; i < bsLen; i++) {
                nbs[i] = toChar(bs[i]);
            }
            return nbs;
        }

        public static char[] toChars(Character[] bs) {
            char[] nbs = new char[bs.length];
            for (int i = 0, bsLen = bs.length; i < bsLen; i++) {
                nbs[i] = bs[i];
            }
            return nbs;
        }

        public static Character[] toChars(char[] bs) {
            Character[] nbs = new Character[bs.length];
            for (int i = 0, bsLen = bs.length; i < bsLen; i++) {
                nbs[i] = bs[i];
            }
            return nbs;
        }

        public static char[] toChars(String bs) {
            return bs.toCharArray();
        }

        public static char[] toChars(String[] bs) {
            int max = 0;
            int offset = 0;
            for (String b : bs) {
                max += b.length();
            }
            char[] chars = new char[max];
            for (String b : bs) {
                char[] c = b.toCharArray();
                System.arraycopy(c, 0, chars, offset, c.length);
                offset += c.length;
            }
            return chars;
        }

        public static char[] toChars(Object o) {
            Valid.notNull(o);
            if (o instanceof String) {
                return toChars((String) o);
            } else if (o instanceof byte[]) {
                return toChars((byte[]) o);
            } else if (o instanceof Byte[]) {
                return toChars((Byte[]) o);
            } else if (o instanceof short[]) {
                return toChars((short[]) o);
            } else if (o instanceof Short[]) {
                return toChars((Short[]) o);
            } else if (o instanceof int[]) {
                return toChars((int[]) o);
            } else if (o instanceof Integer[]) {
                return toChars((Integer[]) o);
            } else if (o instanceof long[]) {
                return toChars((long[]) o);
            } else if (o instanceof Long[]) {
                return toChars((Long[]) o);
            } else if (o instanceof float[]) {
                return toChars((float[]) o);
            } else if (o instanceof Float[]) {
                return toChars((Float[]) o);
            } else if (o instanceof double[]) {
                return toChars((double[]) o);
            } else if (o instanceof Double[]) {
                return toChars((Double[]) o);
            } else if (o instanceof boolean[]) {
                return toChars((boolean[]) o);
            } else if (o instanceof Boolean[]) {
                return toChars((Boolean[]) o);
            } else if (o instanceof char[]) {
                return (char[]) o;
            } else if (o instanceof Character[]) {
                return toChars((Character[]) o);
            } else if (o instanceof String[]) {
                return toChars((String[]) o);
            }
            throw new ValueConvertException(StringUtils.format("unable to convert [{}] to [char[]]", o.getClass().getName()));
        }

        // -------------------- toStrings --------------------

        public static String[] toStrings(Byte[] bs) {
            String[] nbs = new String[bs.length];
            for (int i = 0, bsLen = bs.length; i < bsLen; i++) {
                nbs[i] = bs[i] == null ? StringUtils.EMPTY : bs[i].toString();
            }
            return nbs;
        }

        public static String[] toStrings(byte[] bs) {
            String[] nbs = new String[bs.length];
            for (int i = 0, bsLen = bs.length; i < bsLen; i++) {
                nbs[i] = bs[i] + StringUtils.EMPTY;
            }
            return nbs;
        }

        public static String[] toStrings(Short[] bs) {
            String[] nbs = new String[bs.length];
            for (int i = 0, bsLen = bs.length; i < bsLen; i++) {
                nbs[i] = bs[i] == null ? StringUtils.EMPTY : bs[i].toString();
            }
            return nbs;
        }

        public static String[] toStrings(short[] bs) {
            String[] nbs = new String[bs.length];
            for (int i = 0, bsLen = bs.length; i < bsLen; i++) {
                nbs[i] = bs[i] + StringUtils.EMPTY;
            }
            return nbs;
        }

        public static String[] toStrings(Integer[] bs) {
            String[] nbs = new String[bs.length];
            for (int i = 0, bsLen = bs.length; i < bsLen; i++) {
                nbs[i] = bs[i] == null ? StringUtils.EMPTY : bs[i].toString();
            }
            return nbs;
        }

        public static String[] toStrings(int[] bs) {
            String[] nbs = new String[bs.length];
            for (int i = 0, bsLen = bs.length; i < bsLen; i++) {
                nbs[i] = bs[i] + StringUtils.EMPTY;
            }
            return nbs;
        }

        public static String[] toStrings(Long[] bs) {
            String[] nbs = new String[bs.length];
            for (int i = 0, bsLen = bs.length; i < bsLen; i++) {
                nbs[i] = bs[i] == null ? StringUtils.EMPTY : bs[i].toString();
            }
            return nbs;
        }

        public static String[] toStrings(long[] bs) {
            String[] nbs = new String[bs.length];
            for (int i = 0, bsLen = bs.length; i < bsLen; i++) {
                nbs[i] = bs[i] + StringUtils.EMPTY;
            }
            return nbs;
        }

        public static String[] toStrings(Float[] bs) {
            String[] nbs = new String[bs.length];
            for (int i = 0, bsLen = bs.length; i < bsLen; i++) {
                nbs[i] = bs[i] == null ? StringUtils.EMPTY : bs[i].toString();
            }
            return nbs;
        }

        public static String[] toStrings(float[] bs) {
            String[] nbs = new String[bs.length];
            for (int i = 0, bsLen = bs.length; i < bsLen; i++) {
                nbs[i] = bs[i] + StringUtils.EMPTY;
            }
            return nbs;
        }

        public static String[] toStrings(Double[] bs) {
            String[] nbs = new String[bs.length];
            for (int i = 0, bsLen = bs.length; i < bsLen; i++) {
                nbs[i] = bs[i] == null ? StringUtils.EMPTY : bs[i].toString();
            }
            return nbs;
        }

        public static String[] toStrings(double[] bs) {
            String[] nbs = new String[bs.length];
            for (int i = 0, bsLen = bs.length; i < bsLen; i++) {
                nbs[i] = bs[i] + StringUtils.EMPTY;
            }
            return nbs;
        }

        public static String[] toStrings(Boolean[] bs) {
            String[] nbs = new String[bs.length];
            for (int i = 0, bsLen = bs.length; i < bsLen; i++) {
                nbs[i] = bs[i] == null ? StringUtils.EMPTY : bs[i].toString();
            }
            return nbs;
        }

        public static String[] toStrings(boolean[] bs) {
            String[] nbs = new String[bs.length];
            for (int i = 0, bsLen = bs.length; i < bsLen; i++) {
                nbs[i] = bs[i] + StringUtils.EMPTY;
            }
            return nbs;
        }

        public static String[] toStrings(Character[] bs) {
            String[] nbs = new String[bs.length];
            for (int i = 0, bsLen = bs.length; i < bsLen; i++) {
                nbs[i] = bs[i] == null ? StringUtils.EMPTY : bs[i].toString();
            }
            return nbs;
        }

        public static String[] toStrings(char[] bs) {
            String[] nbs = new String[bs.length];
            for (int i = 0, bsLen = bs.length; i < bsLen; i++) {
                nbs[i] = bs[i] + StringUtils.EMPTY;
            }
            return nbs;
        }

        public static String[] toStrings(Object o) {
            Valid.notNull(o);
            if (o instanceof String) {
                return new String[]{(String) o};
            } else if (o instanceof byte[]) {
                return toStrings((byte[]) o);
            } else if (o instanceof Byte[]) {
                return toStrings((Byte[]) o);
            } else if (o instanceof short[]) {
                return toStrings((short[]) o);
            } else if (o instanceof Short[]) {
                return toStrings((Short[]) o);
            } else if (o instanceof int[]) {
                return toStrings((int[]) o);
            } else if (o instanceof Integer[]) {
                return toStrings((Integer[]) o);
            } else if (o instanceof long[]) {
                return toStrings((long[]) o);
            } else if (o instanceof Long[]) {
                return toStrings((Long[]) o);
            } else if (o instanceof float[]) {
                return toStrings((float[]) o);
            } else if (o instanceof Float[]) {
                return toStrings((Float[]) o);
            } else if (o instanceof double[]) {
                return toStrings((double[]) o);
            } else if (o instanceof Double[]) {
                return toStrings((Double[]) o);
            } else if (o instanceof boolean[]) {
                return toStrings((boolean[]) o);
            } else if (o instanceof Boolean[]) {
                return toStrings((Boolean[]) o);
            } else if (o instanceof char[]) {
                return toStrings((char[]) o);
            } else if (o instanceof Character[]) {
                return toStrings((Character[]) o);
            } else if (o instanceof String[]) {
                return (String[]) o;
            }
            throw new ValueConvertException(StringUtils.format("unable to convert [{}] to [String[]]", o.getClass().getName()));
        }

    }
}
