package com.sondertara.common.collection;

import com.sondertara.common.base.Assert;
import com.sondertara.common.base.Emptys;
import com.sondertara.common.base.ObjectUtils;
import com.sondertara.common.function.Reduce;
import com.sondertara.common.random.RandomUtils;
import com.sondertara.common.reflect.ClassUtils;
import com.sondertara.common.reflect.type.Primitives;
import com.sondertara.common.struct.Holder;
import com.sondertara.common.text.StringUtils;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * 数组工具类
 *
 * @author huangxiaohu
 */
public class ArrayUtils extends PrimitiveArrayUtil {

    public static <E> E[] emptyArray(@Nullable Class<E> componentType) {
        return ArrayUtils.createArray(Primitives.wrap(componentType), 0);
    }


    /**
     * Wrap any object using new Object[]{object};
     */
    public static <E> E[] wrapAsArray(@Nullable E o) {
        if (Emptys.isNull(o)) {
            return (E[]) new Object[0];
        }
        E[] array = (E[]) createArray(o.getClass(), 1);
        initArray(array, o);
        return array;
    }

    public static byte[] createByteArray(int count) {
        return (count == 0) ? Emptys.EMPTY_BYTES : new byte[count];
    }


    /**
     * Create an array with the specified length.
     * <p>
     * int.class => Integer[]
     * Integer.class => Integer[]
     */
    public static <E> E[] createArray(@Nullable Class<E> componentType, int length) {
        Assert.isTrue(length >= 0);
        if (componentType == null) {
            return (E[]) Array.newInstance(Object.class, length);
        }
        if (Primitives.isPrimitive(componentType)) {
            componentType = Primitives.wrap(componentType);
        }
        return (E[]) Array.newInstance(componentType, length);
    }

    /**
     * Create an array with the specified length and every element's value is the specified initValue
     */
    public static <E extends Object> E[] createArray(@Nullable Class<E> componentType, int length, @Nullable final E initValue) {
        E[] array = createArray(componentType, length);
        initArray(array, initValue);
        return array;
    }


    /**
     * Create an array with the specified length and every element's value is supplied by the specified initSupplier
     */
    public static <E extends Object> E[] createArray(@Nullable Class<E> componentType, int length, @NonNull Function<Integer, E> initSupplier) {
        E[] array = createArray(componentType, length);
        initArray(array, initSupplier);
        return array;
    }

    public static <E> void initArray(@NonNull E[] array, @Nullable final E initValue) {
        Objects.requireNonNull(array);
        initArray(array, new Function<Integer, E>() {
            @Override
            public E apply(Integer index) {
                return initValue;
            }
        });
    }

    public static <E> void initArray(@NonNull E[] array, @NonNull Function<Integer, E> initSupplier) {
        Objects.requireNonNull(initSupplier);
        for (int i = 0; i < array.length; i++) {
            array[i] = initSupplier.apply(i);
        }
    }


    public static boolean deepEquals(Object[] a1, Object[] a2) {
        return Arrays.deepEquals(a1, a2);
    }

    public static boolean deepEquals(Object e1, Object e2) {
        if (e1 == e2) {
            return true;
        }
        if (e1 == null || e2 == null) {
            return false;
        }
        boolean eq;
        if (e1 instanceof Object[] && e2 instanceof Object[]) {
            eq = Arrays.deepEquals((Object[]) e1, (Object[]) e2);
        } else if (e1 instanceof byte[] && e2 instanceof byte[]) {
            eq = PrimitiveArrays.equals((byte[]) e1, (byte[]) e2);
        } else if (e1 instanceof short[] && e2 instanceof short[]) {
            eq = PrimitiveArrays.equals((short[]) e1, (short[]) e2);
        } else if (e1 instanceof int[] && e2 instanceof int[]) {
            eq = PrimitiveArrays.equals((int[]) e1, (int[]) e2);
        } else if (e1 instanceof long[] && e2 instanceof long[]) {
            eq = PrimitiveArrays.equals((long[]) e1, (long[]) e2);
        } else if (e1 instanceof char[] && e2 instanceof char[]) {
            eq = PrimitiveArrays.equals((char[]) e1, (char[]) e2);
        } else if (e1 instanceof float[] && e2 instanceof float[]) {
            eq = PrimitiveArrays.equals((float[]) e1, (float[]) e2);
        } else if (e1 instanceof double[] && e2 instanceof double[]) {
            eq = PrimitiveArrays.equals((double[]) e1, (double[]) e2);
        } else if (e1 instanceof boolean[] && e2 instanceof boolean[]) {
            eq = PrimitiveArrays.equals((boolean[]) e1, (boolean[]) e2);
        } else {
            eq = e1.equals(e2);
        }
        return eq;
    }


    public static int toPositiveIndex(int length, int index) {
        Assert.isTrue(isValidIndex(length, index), "index " + index + " is invalid");
        if (isNegativeIndex(length, index)) {
            return reverseIndex(length, index, true);
        }
        return index;
    }

    public static int[] toPositiveIndexes(int length, int fromIndex, int toIndex) {
        fromIndex = ArrayUtils.toPositiveIndex(length, fromIndex);
        toIndex = ArrayUtils.toPositiveIndex(length, toIndex);
        if (fromIndex > toIndex) {
            int t = fromIndex;
            fromIndex = toIndex;
            toIndex = t;
        }
        int[] arr = new int[]{fromIndex, toIndex};
        return arr;
    }

    public static int reverseIndex(int length, int index) {
        return reverseIndex(length, index, false);
    }

    /**
     * 正负之间反转索引：
     * <p>
     * 0,   1,  2,   3,   4  // 正序遍历时的索引
     * -5,  -4, -3   -2,  -1  // 倒序遍历时的索引
     * <p>
     * 前后颠倒反转索引：
     * 0,   1,  2,   3,   4  // 正序遍历时的索引
     * 4,   3,  2    1,   0  // 倒序遍历时的索引
     */
    public static int reverseIndex(int length, int index, boolean positiveNegativeInterChangeMode) {
        if (positiveNegativeInterChangeMode) {
            Assert.isTrue(isValidIndex(length, index), "index " + index + " is invalid");
            if (isPositiveIndex(length, index)) {
                return index - length;
            } else {
                return length + index;
            }
        } else {
            Assert.isTrue(isPositiveIndex(length, index), "index " + index + " is invalid");
            return length - 1 - index;
        }
    }

    /**
     * 判断索引是否有效
     */
    public static boolean isValidIndex(int length, int index) {
        return isNegativeIndex(length, index) || isPositiveIndex(length, index);
    }

    /**
     * 判断是否为正数索引
     */
    public static boolean isPositiveIndex(int length, int index) {
        Assert.isTrue(length > 0, "length " + length + " is invalid");
        int max = length - 1;
        int min = 0;
        return index >= min && index <= max;
    }

    /**
     * 判断是否为负数索引
     */
    public static boolean isNegativeIndex(int length, int index) {
        Assert.isTrue(length > 0, "length " + length + " is invalid");
        int max = -1;
        int min = -length;
        return index >= min && index <= max;
    }


    public static int indexOf(Object[] array, Object objectToFind, int startIndex) {
        if (array != null) {
            if (startIndex < 0) {
                startIndex = 0;
            }

            int i;
            if (objectToFind == null) {
                for (i = startIndex; i < array.length; ++i) {
                    if (array[i] == null) {
                        return i;
                    }
                }
            } else {
                for (i = startIndex; i < array.length; ++i) {
                    if (objectToFind.equals(array[i])) {
                        return i;
                    }
                }
            }

        }
        return -1;
    }

    public static <E> E[] copy(final E... objs) {
        if (objs == null) {
            return null;
        }
        Class<?> componentType = objs.getClass().getComponentType();
        E[] newArray = (E[]) createArray(componentType, objs.length);
        for (int i = 0; i < newArray.length; i++) {
            newArray[i] = objs[i];
        }
        return newArray;
    }

    @SuppressWarnings("rawtypes")
    public static <E> boolean isMixedArray(E... objs) {
        final Holder<Class> elementType = new Holder<Class>();
        boolean isMixed = CollectionUtils.anyMatch(new Predicate<Object>() {
            @Override
            public boolean test(Object element) {
                if (element != null) {
                    if (elementType.isNull()) {
                        elementType.set(element.getClass());
                    } else {
                        return elementType.get() != element.getClass();
                    }
                }
                return false;
            }
        }, objs);
        // objs.getClass().getComponentType();
        return isMixed;
    }


    public static <E> E[] subArray(E[] array, int start, int end) {
        start = toPositiveIndex(array.length, start);
        end = toPositiveIndex(array.length, end);
        if (start > end) {
            int tmp = start;
            start = end;
            end = tmp;
        }
        E[] dest = (E[]) createArray(array.getClass(), end - start);
        System.arraycopy(array, start, dest, 0, dest.length);
        return dest;
    }

    public static final String[] EMPTY_STRING_ARRAY = new String[0];

    /**
     * 数组中元素未找到的下标，值为-1
     */
    public static final int INDEX_NOT_FOUND = -1;
    public static final Type[] EMPTY_TYPE_ARRAY = {};
    public static final byte[] EMPTY_BYTE_ARRAY = {};
    public static final int[] EMPTY_INT_ARRAY = {};


    private static final int BINARY = 2;

    private static final int INITIAL_HASH = 7;

    private static final int MULTIPLIER = 31;

    private static final Object[] EMPTY_OBJECT_ARR = {};
    // -------------------- new --------------------

    /**
     * 创建数组
     *
     * @param len 数组长度
     * @return 数组
     */
    public static byte[] newBytes(int len) {
        return new byte[len];
    }

    public static short[] newShorts(int len) {
        return new short[len];
    }

    public static int[] newInts(int len) {
        return new int[len];
    }

    public static long[] newLongs(int len) {
        return new long[len];
    }

    public static float[] newFloats(int len) {
        return new float[len];
    }

    public static double[] newDoubles(int len) {
        return new double[len];
    }

    public static char[] newChars(int len) {
        return new char[len];
    }

    public static boolean[] newBooleans(int len) {
        return new boolean[len];
    }

    public static Object[] newObjects(int len) {
        return new Object[len];
    }

    /**
     * 创建数组
     *
     * @param type   类型
     * @param length 空间
     * @param <T>    ignore
     * @return 数组
     */
    public static <T> T[] newArrays(Class<T> type, int length) {
        ClassUtils.isBaseArrayClass(type);
        return (T[]) Array.newInstance(type, length);
    }


    /**
     * 创建数组
     *
     * @param generator e.g. Integer[]::new
     * @param length    空间
     * @param <T>       ignore
     * @return 数组
     */
    public static <T> T[] newArrays(IntFunction<T[]> generator, int length) {
        return generator.apply(length);
    }

    // -------------------- isArray --------------------


    /**
     * 判断是否是基本类型的数组
     *
     * @param o 对象
     * @return true 是基本类型的数组
     */
    public static boolean isBaseArray(Object o) {
        if (o == null) {
            return false;
        }
        return !isNotBaseArray(o);
    }

    /**
     * 判断是否不是基本类型的数组
     *
     * @param o 对象
     * @return true 不是基本类型的数组
     */
    public static boolean isNotBaseArray(Object o) {
        if (o == null) {
            return false;
        } else if (o instanceof byte[]) {
            return false;
        } else if (o instanceof short[]) {
            return false;
        } else if (o instanceof int[]) {
            return false;
        } else if (o instanceof long[]) {
            return false;
        } else if (o instanceof float[]) {
            return false;
        } else if (o instanceof double[]) {
            return false;
        } else if (o instanceof char[]) {
            return false;
        } else {
            return !(o instanceof boolean[]);
        }
    }

    /**
     * 判断是否不是包装类型的数组
     *
     * @param o 对象
     * @return true 不是包装类型的数组
     */
    public static boolean isWrapBaseArray(Object o) {
        if (o == null) {
            return false;
        } else if (o instanceof Byte[]) {
            return false;
        } else if (o instanceof Short[]) {
            return false;
        } else if (o instanceof Integer[]) {
            return false;
        } else if (o instanceof Long[]) {
            return false;
        } else if (o instanceof Float[]) {
            return false;
        } else if (o instanceof Double[]) {
            return false;
        } else if (o instanceof Character[]) {
            return false;
        } else {
            return !(o instanceof Boolean[]);
        }
    }

    // -------------------- get --------------------

    /**
     * 获取数组下标对应的值
     *
     * @param arr 数组
     * @param i   下标
     * @param <T> ignore
     * @return value
     */
    @SuppressWarnings("unchecked")
    public static <T> T gets(Object arr, int i) {
        Assert.notNull(arr, "array is null");
        return (T) Array.get(arr, i);
    }

    public static <T> T get(T[] arr, int i) {
        Assert.notNull(arr, "array is null");
        if (arr.length <= i) {
            throw new IllegalArgumentException("array length: " + arr.length + " get index: " + i);
        }
        return arr[i];
    }

    public static byte get(byte[] arr, int i) {
        Assert.notNull(arr, "array is null");
        if (arr.length <= i) {
            throw new IllegalArgumentException("array length: " + arr.length + " get index: " + i);
        }
        return arr[i];
    }

    public static short get(short[] arr, int i) {
        Assert.notNull(arr, "array is null");
        if (arr.length <= i) {
            throw new IllegalArgumentException("array length: " + arr.length + " get index: " + i);
        }
        return arr[i];
    }

    public static int get(int[] arr, int i) {
        Assert.notNull(arr, "array is null");
        if (arr.length <= i) {
            throw new IllegalArgumentException("array length: " + arr.length + " get index: " + i);
        }
        return arr[i];
    }

    public static long get(long[] arr, int i) {
        Assert.notNull(arr, "array is null");
        if (arr.length <= i) {
            throw new IllegalArgumentException("array length: " + arr.length + " get index: " + i);
        }
        return arr[i];
    }

    public static float get(float[] arr, int i) {
        Assert.notNull(arr, "array is null");
        if (arr.length <= i) {
            throw new IllegalArgumentException("array length: " + arr.length + " get index: " + i);
        }
        return arr[i];
    }

    public static double get(double[] arr, int i) {
        Assert.notNull(arr, "array is null");
        if (arr.length <= i) {
            throw new IllegalArgumentException("array length: " + arr.length + " get index: " + i);
        }
        return arr[i];
    }

    public static boolean get(boolean[] arr, int i) {
        Assert.notNull(arr, "array is null");
        if (arr.length <= i) {
            throw new IllegalArgumentException("array length: " + arr.length + " get index: " + i);
        }
        return arr[i];
    }

    public static char get(char[] arr, int i) {
        Assert.notNull(arr, "array is null");
        if (arr.length <= i) {
            throw new IllegalArgumentException("array length: " + arr.length + " get index: " + i);
        }
        return arr[i];
    }

    // -------------------- set --------------------

    /**
     * 设置数组下标对应的值
     *
     * @param arr   数组
     * @param i     下标
     * @param value 值
     * @param <T>   ignore
     */
    public static <T> void sets(Object arr, int i, T value) {
        Assert.notNull(arr, "array is null");
        Array.set(arr, i, value);
    }

    public static <T> void set(T[] arr, int i, T value) {
        Assert.notNull(arr, "array is null");
        if (arr.length <= i) {
            throw new IllegalArgumentException("array length: " + arr.length + " get index: " + i);
        }
        arr[i] = value;
    }

    public static void set(byte[] arr, int i, byte value) {
        Assert.notNull(arr, "array is null");
        if (arr.length <= i) {
            throw new IllegalArgumentException("array length: " + arr.length + " get index: " + i);
        }
        arr[i] = value;
    }

    public static void set(short[] arr, int i, short value) {
        Assert.notNull(arr, "array is null");
        if (arr.length <= i) {
            throw new IllegalArgumentException("array length: " + arr.length + " get index: " + i);
        }
        arr[i] = value;
    }

    public static void set(int[] arr, int i, int value) {
        Assert.notNull(arr, "array is null");
        if (arr.length <= i) {
            throw new IllegalArgumentException("array length: " + arr.length + " get index: " + i);
        }
        arr[i] = value;
    }

    public static void set(long[] arr, int i, long value) {
        Assert.notNull(arr, "array is null");
        if (arr.length <= i) {
            throw new IllegalArgumentException("array length: " + arr.length + " get index: " + i);
        }
        arr[i] = value;
    }

    public static void set(float[] arr, int i, float value) {
        Assert.notNull(arr, "array is null");
        if (arr.length <= i) {
            throw new IllegalArgumentException("array length: " + arr.length + " get index: " + i);
        }
        arr[i] = value;
    }

    public static void set(double[] arr, int i, double value) {
        Assert.notNull(arr, "array is null");
        if (arr.length <= i) {
            throw new IllegalArgumentException("array length: " + arr.length + " get index: " + i);
        }
        arr[i] = value;
    }

    public static void set(boolean[] arr, int i, boolean value) {
        Assert.notNull(arr, "array is null");
        if (arr.length <= i) {
            throw new IllegalArgumentException("array length: " + arr.length + " get index: " + i);
        }
        arr[i] = value;
    }

    public static void set(char[] arr, int i, char value) {
        Assert.notNull(arr, "array is null");
        if (arr.length <= i) {
            throw new IllegalArgumentException("array length: " + arr.length + " get index: " + i);
        }
        arr[i] = value;
    }

    // -------------------- random --------------------

    public static <T> T random(T[] arr) {
        Assert.notNull(arr, "array is null");
        return arr[RandomUtils.randomInt(arr.length)];
    }

    public static byte random(byte[] arr) {
        Assert.notNull(arr, "array is null");
        return arr[RandomUtils.randomInt(arr.length)];
    }

    public static short random(short[] arr) {
        Assert.notNull(arr, "array is null");
        return arr[RandomUtils.randomInt(arr.length)];
    }

    public static int random(int[] arr) {
        Assert.notNull(arr, "array is null");
        return arr[RandomUtils.randomInt(arr.length)];
    }

    public static long random(long[] arr) {
        Assert.notNull(arr, "array is null");
        return arr[RandomUtils.randomInt(arr.length)];
    }

    public static float random(float[] arr) {
        Assert.notNull(arr, "array is null");
        return arr[RandomUtils.randomInt(arr.length)];
    }

    public static double random(double[] arr) {
        Assert.notNull(arr, "array is null");
        return arr[RandomUtils.randomInt(arr.length)];
    }

    public static boolean random(boolean[] arr) {
        Assert.notNull(arr, "array is null");
        return arr[RandomUtils.randomInt(arr.length)];
    }

    public static char random(char[] arr) {
        Assert.notNull(arr, "array is null");
        return arr[RandomUtils.randomInt(arr.length)];
    }

    // -------------------- mapper reducer --------------------

    /**
     * 映射
     *
     * @param arr       需要映射的对象
     * @param generator 数组创建接口 e.g. Integer[]::new
     * @param mapper    映射接口
     * @param <I>       输入类型
     * @param <O>       输出类型
     * @return 映射结果
     */
    public static <I, O> O[] mapper(I[] arr, IntFunction<? extends O[]> generator, Function<? super I, ? extends O> mapper) {
        int len = length(arr);
        if (len == 0) {
            return generator.apply(0);
        }
        O[] oa = generator.apply(len);
        for (int j = 0; j < len; j++) {
            O o = mapper.apply(arr[j]);
            oa[j] = o;
        }
        return oa;
    }

    /**
     * 规约
     *
     * @param reduce 规约接口
     * @param arr    需要规约的对象 该对象不能为基本类型的数组, 可以为基本类型的变量
     * @param <V>    数据类型
     * @param <R>    规约类型
     * @return 规约后的数据
     */
    public static <V, R> R reduce(V[] arr, Reduce<? super V, ? extends R> reduce) {
        int len = length(arr);
        if (len == 0) {
            throw new IllegalArgumentException("array length is 0");
        }
        return reduce.accept(arr);
    }

    // -------------------- compact --------------------

    /**
     * 去除数组中的 null
     *
     * @param arr 数组
     * @return 处理后的数组, 如果数组没有null则直接返回, 如果数组有null
     */
    public static Object[] compact(Object[] arr) {
        int len = length(arr);
        if (len == 0) {
            return new Object[0];
        } else if (len == 1) {
            if (arr[0] == null) {
                return new Object[0];
            }
        }
        int num = compactSwap(arr, len);
        Object[] na = new Object[len - num];
        System.arraycopy(arr, 0, na, 0, len - num);
        return na;
    }

    public static <T> T[] compacts(T[] arr, IntFunction<T[]> generator) {
        int len = length(arr);
        if (len == 0) {
            return generator.apply(0);
        } else if (len == 1) {
            if (arr[0] == null) {
                return generator.apply(0);
            }
        }
        int num = compactSwap(arr, len);
        T[] na = generator.apply(len - num);
        System.arraycopy(arr, 0, na, 0, len - num);
        return na;
    }

    /**
     * 交换数组中的位置
     *
     * @param arr 数组
     * @param len 数组长度
     * @return 处理空值的次数
     */
    private static int compactSwap(Object[] arr, int len) {
        int num = 0;
        for (int i = 0; i < len; i++) {
            if (arr[i] != null) {
                continue;
            }
            if (i == len - num + 1) {
                break;
            }
            num++;
            if (i != len - num) {
                System.arraycopy(arr, i + 1, arr, i, len - i - 1);
                i--;
            }
        }
        return num;
    }

    // -------------------- exclude --------------------

    /**
     * 排除数组中指定的值
     *
     * @param arr 数组
     * @param es  排除值
     * @return 处理后的数组, 如果数组没有包含的值则直接返回
     */
    public static byte[] exclude(byte[] arr, byte... es) {
        int len = length(arr);
        if (len == 0) {
            return new byte[0];
        }
        if (isEmpty(es)) {
            return arr;
        }
        int num = excludeSwap(arr, len, i -> {
            for (byte e : es) {
                if (i == e) {
                    return true;
                }
            }
            return false;
        });
        byte[] na = new byte[len - num];
        System.arraycopy(arr, 0, na, 0, len - num);
        return na;
    }

    public static short[] exclude(short[] arr, short... es) {
        int len = length(arr);
        if (len == 0) {
            return new short[0];
        }
        if (isEmpty(es)) {
            return arr;
        }
        int num = excludeSwap(arr, len, i -> {
            for (short e : es) {
                if (i == e) {
                    return true;
                }
            }
            return false;
        });
        short[] na = new short[len - num];
        System.arraycopy(arr, 0, na, 0, len - num);
        return na;
    }

    public static int[] exclude(int[] arr, int... es) {
        int len = length(arr);
        if (len == 0) {
            return new int[0];
        }
        if (isEmpty(es)) {
            return arr;
        }
        int num = excludeSwap(arr, len, i -> {
            for (int e : es) {
                if (i == e) {
                    return true;
                }
            }
            return false;
        });
        int[] na = new int[len - num];
        System.arraycopy(arr, 0, na, 0, len - num);
        return na;
    }

    public static long[] exclude(long[] arr, long... es) {
        int len = length(arr);
        if (len == 0) {
            return new long[0];
        }
        if (isEmpty(es)) {
            return arr;
        }
        int num = excludeSwap(arr, len, i -> {
            for (long e : es) {
                if (i == e) {
                    return true;
                }
            }
            return false;
        });
        long[] na = new long[len - num];
        System.arraycopy(arr, 0, na, 0, len - num);
        return na;
    }

    public static float[] exclude(float[] arr, float... es) {
        int len = length(arr);
        if (len == 0) {
            return new float[0];
        }
        if (isEmpty(es)) {
            return arr;
        }
        int num = excludeSwap(arr, len, i -> {
            for (float e : es) {
                if (i == e) {
                    return true;
                }
            }
            return false;
        });
        float[] na = new float[len - num];
        System.arraycopy(arr, 0, na, 0, len - num);
        return na;
    }

    public static double[] exclude(double[] arr, double... es) {
        int len = length(arr);
        if (len == 0) {
            return new double[0];
        }
        if (isEmpty(es)) {
            return arr;
        }
        int num = excludeSwap(arr, len, i -> {
            for (double e : es) {
                if (i == e) {
                    return true;
                }
            }
            return false;
        });
        double[] na = new double[len - num];
        System.arraycopy(arr, 0, na, 0, len - num);
        return na;
    }

    public static boolean[] exclude(boolean[] arr, boolean... es) {
        int len = length(arr);
        if (len == 0) {
            return new boolean[0];
        }
        if (isEmpty(es)) {
            return arr;
        }
        int num = excludeSwap(arr, len, i -> {
            for (boolean e : es) {
                if (i == e) {
                    return true;
                }
            }
            return false;
        });
        boolean[] na = new boolean[len - num];
        System.arraycopy(arr, 0, na, 0, len - num);
        return na;
    }

    public static char[] exclude(char[] arr, char... es) {
        int len = length(arr);
        if (len == 0) {
            return new char[0];
        }
        if (isEmpty(es)) {
            return arr;
        }
        int num = excludeSwap(arr, len, i -> {
            for (char e : es) {
                if (i == e) {
                    return true;
                }
            }
            return false;
        });
        char[] na = new char[len - num];
        System.arraycopy(arr, 0, na, 0, len - num);
        return na;
    }

    /**
     * 排除数组中指定的值
     *
     * @param arr 数组
     * @param es  排除值 这个值可以为基本类型的元素, 但是不能是基本类型的数组
     * @return 处理后的数组, 如果数组没有包含的值则直接返回
     */
    public static Object[] exclude(Object[] arr, Object... es) {
        int len = length(arr);
        if (len == 0) {
            return new Object[0];
        }
        if (isEmpty(es)) {
            return arr;
        }
        int num = excludeSwap(arr, len, i -> {
            for (Object e : es) {
                if (ObjectUtils.equals(i, e)) {
                    return true;
                }
            }
            return false;
        });
        Object[] na = new Object[len - num];
        System.arraycopy(arr, 0, na, 0, len - num);
        return na;
    }

    @SafeVarargs
    public static <T> T[] excludes(T[] arr, IntFunction<T[]> generator, T... es) {
        int len = length(arr);
        if (len == 0) {
            return generator.apply(0);
        }
        if (isEmpty(es)) {
            return arr;
        }
        int num = excludeSwap(arr, len, i -> {
            for (Object e : es) {
                if (ObjectUtils.equals(i, e)) {
                    return true;
                }
            }
            return false;
        });
        T[] na = generator.apply(len - num);
        System.arraycopy(arr, 0, na, 0, len - num);
        return na;
    }

    @SuppressWarnings("unchecked")
    public static <T> T[] excludes(T[] arr, Predicate<T> p, IntFunction<T[]> generator) {
        int len = length(arr);
        if (len == 0) {
            return generator.apply(0);
        }
        int num = excludeSwap(arr, len, (Predicate<Object>) p);
        T[] na = generator.apply(len - num);
        System.arraycopy(arr, 0, na, 0, len - num);
        return na;
    }

    /**
     * 交换数组中的位置
     *
     * @param arr 数组
     * @param len 数组长度
     * @return 处理空值的次数
     */
    private static int excludeSwap(byte[] arr, int len, Predicate<Byte> p) {
        int num = 0;
        for (int i = 0; i < len; i++) {
            if (!p.test(arr[i])) {
                continue;
            }
            if (i == len - num + 1) {
                break;
            }
            num++;
            if (i != len - num) {
                System.arraycopy(arr, i + 1, arr, i, len - i - 1);
                i--;
            }
        }
        return num;
    }

    private static int excludeSwap(short[] arr, int len, Predicate<Short> p) {
        int num = 0;
        for (int i = 0; i < len; i++) {
            if (!p.test(arr[i])) {
                continue;
            }
            if (i == len - num + 1) {
                break;
            }
            num++;
            if (i != len - num) {
                System.arraycopy(arr, i + 1, arr, i, len - i - 1);
                i--;
            }
        }
        return num;
    }

    private static int excludeSwap(int[] arr, int len, Predicate<Integer> p) {
        int num = 0;
        for (int i = 0; i < len; i++) {
            if (!p.test(arr[i])) {
                continue;
            }
            if (i == len - num + 1) {
                break;
            }
            num++;
            if (i != len - num) {
                System.arraycopy(arr, i + 1, arr, i, len - i - 1);
                i--;
            }
        }
        return num;
    }

    private static int excludeSwap(long[] arr, int len, Predicate<Long> p) {
        int num = 0;
        for (int i = 0; i < len; i++) {
            if (!p.test(arr[i])) {
                continue;
            }
            if (i == len - num + 1) {
                break;
            }
            num++;
            if (i != len - num) {
                System.arraycopy(arr, i + 1, arr, i, len - i - 1);
                i--;
            }
        }
        return num;
    }

    private static int excludeSwap(float[] arr, int len, Predicate<Float> p) {
        int num = 0;
        for (int i = 0; i < len; i++) {
            if (!p.test(arr[i])) {
                continue;
            }
            if (i == len - num + 1) {
                break;
            }
            num++;
            if (i != len - num) {
                System.arraycopy(arr, i + 1, arr, i, len - i - 1);
                i--;
            }
        }
        return num;
    }

    private static int excludeSwap(double[] arr, int len, Predicate<Double> p) {
        int num = 0;
        for (int i = 0; i < len; i++) {
            if (!p.test(arr[i])) {
                continue;
            }
            if (i == len - num + 1) {
                break;
            }
            num++;
            if (i != len - num) {
                System.arraycopy(arr, i + 1, arr, i, len - i - 1);
                i--;
            }
        }
        return num;
    }

    private static int excludeSwap(boolean[] arr, int len, Predicate<Boolean> p) {
        int num = 0;
        for (int i = 0; i < len; i++) {
            if (!p.test(arr[i])) {
                continue;
            }
            if (i == len - num + 1) {
                break;
            }
            num++;
            if (i != len - num) {
                System.arraycopy(arr, i + 1, arr, i, len - i - 1);
                i--;
            }
        }
        return num;
    }

    private static int excludeSwap(char[] arr, int len, Predicate<Character> p) {
        int num = 0;
        for (int i = 0; i < len; i++) {
            if (!p.test(arr[i])) {
                continue;
            }
            if (i == len - num + 1) {
                break;
            }
            num++;
            if (i != len - num) {
                System.arraycopy(arr, i + 1, arr, i, len - i - 1);
                i--;
            }
        }
        return num;
    }

    private static int excludeSwap(Object[] arr, int len, Predicate<Object> p) {
        int num = 0;
        for (int i = 0; i < len; i++) {
            if (!p.test(arr[i])) {
                continue;
            }
            if (i == len - num + 1) {
                break;
            }
            num++;
            if (i != len - num) {
                System.arraycopy(arr, i + 1, arr, i, len - i - 1);
                i--;
            }
        }
        return num;
    }

    private static <T> int excludeSwaps(T[] arr, int len, Predicate<T> p) {
        int num = 0;
        for (int i = 0; i < len; i++) {
            if (!p.test(arr[i])) {
                continue;
            }
            if (i == len - num + 1) {
                break;
            }
            num++;
            if (i != len - num) {
                System.arraycopy(arr, i + 1, arr, i, len - i - 1);
                i--;
            }
        }
        return num;
    }

    // -------------------- forEach --------------------

    /**
     * 消费对象
     *
     * @param consumer 消费函数
     * @param arr      元素
     * @param <T>      对象类型
     */
    public static <T> void forEach(Consumer<T> consumer, T[] arr) {
        for (int i = 0, len = length(arr); i < len; i++) {
            consumer.accept(arr[i]);
        }
    }

    public static void forEach(Consumer<Byte> consumer, byte[] arr) {
        for (int i = 0, len = length(arr); i < len; i++) {
            consumer.accept(arr[i]);
        }
    }

    public static void forEach(Consumer<Short> consumer, short[] arr) {
        for (int i = 0, len = length(arr); i < len; i++) {
            consumer.accept(arr[i]);
        }
    }

    public static void forEach(Consumer<Integer> consumer, int[] arr) {
        for (int i = 0, len = length(arr); i < len; i++) {
            consumer.accept(arr[i]);
        }
    }

    public static void forEach(Consumer<Long> consumer, long[] arr) {
        for (int i = 0, len = length(arr); i < len; i++) {
            consumer.accept(arr[i]);
        }
    }

    public static void forEach(Consumer<Float> consumer, float[] arr) {
        for (int i = 0, len = length(arr); i < len; i++) {
            consumer.accept(arr[i]);
        }
    }

    public static void forEach(Consumer<Double> consumer, double[] arr) {
        for (int i = 0, len = length(arr); i < len; i++) {
            consumer.accept(arr[i]);
        }
    }

    public static void forEach(Consumer<Character> consumer, char[] arr) {
        for (int i = 0, len = length(arr); i < len; i++) {
            consumer.accept(arr[i]);
        }
    }

    public static void forEach(Consumer<Boolean> consumer, boolean[] arr) {
        for (int i = 0, len = length(arr); i < len; i++) {
            consumer.accept(arr[i]);
        }
    }

    // -------------------- toArray --------------------

    // -------------------- asArray --------------------

    /**
     * 将可变参数转化为数组
     *
     * @param arr 可边参数
     * @return 数组
     */
    public static byte[] of(byte... arr) {
        return arr;
    }

    public static short[] of(short... arr) {
        return arr;
    }

    public static int[] of(int... arr) {
        return arr;
    }

    public static long[] of(long... arr) {
        return arr;
    }

    public static float[] of(float... arr) {
        return arr;
    }

    public static double[] of(double... arr) {
        return arr;
    }

    public static boolean[] of(boolean... arr) {
        return arr;
    }

    public static char[] of(char... arr) {
        return arr;
    }

    /**
     * 不可接收基本类型的数组, 可以使用包装类 否则会返回 T[][]
     *
     * @param arr arr
     * @param <T> arr
     * @return array
     */
    @SafeVarargs
    public static <T> T[] of(T... arr) {
        return arr;
    }

    /**
     * 可接收基本类型的数组, 返回Object[]
     *
     * @param arr arr
     * @return array
     */
    public static Object[] ofs(Object... arr) {
        int lengths = lengths(arr);
        if (lengths == 0) {
            return EMPTY_OBJECT_ARR;
        }
        Object[] r = new Object[lengths];
        int len = arr.length;
        if (len != lengths || (len == 1 && isArray(arr[0]))) {
            for (int j = 0; j < lengths; j++) {
                r[j] = Array.get(arr[0], j);
            }
        } else {
            return arr;
        }
        return r;
    }

    // -------------------- isEmpty --------------------

    /**
     * 判断数组是否为空
     *
     * @param arr 数组
     * @return true为空
     */
    public static boolean isEmpties(Object arr) {
        return arr == null || (isArray(arr) && Array.getLength(arr) == 0);
    }


    // -------------------- isNotEmpty --------------------

    /**
     * 判断数组是否不为空
     *
     * @param arr 数组
     * @return true不为空
     */
    public static boolean isNotEmpties(Object arr) {
        return !isEmpties(arr);
    }


    // -------------------- def --------------------

    /**
     * 如果数组为空返回默认数组
     *
     * @param arr arr
     * @return array
     */
    public static <T> T[] def(T[] arr, T[] def) {
        return arr == null ? def : arr;
    }

    public static byte[] def(byte[] arr, byte[] def) {
        return arr == null ? def : arr;
    }

    public static short[] def(short[] arr, short[] def) {
        return arr == null ? def : arr;
    }

    public static int[] def(int[] arr, int[] def) {
        return arr == null ? def : arr;
    }

    public static long[] def(long[] arr, long[] def) {
        return arr == null ? def : arr;
    }

    public static float[] def(float[] arr, float[] def) {
        return arr == null ? def : arr;
    }

    public static double[] def(double[] arr, double[] def) {
        return arr == null ? def : arr;
    }

    public static boolean[] def(boolean[] arr, boolean[] def) {
        return arr == null ? def : arr;
    }

    public static char[] def(char[] arr, char[] def) {
        return arr == null ? def : arr;
    }

    public static <T> T[] def(T[] arr, Supplier<T[]> def) {
        return arr == null ? def.get() : arr;
    }

    public static byte[] def(byte[] arr, Supplier<byte[]> def) {
        return arr == null ? def.get() : arr;
    }

    public static short[] def(short[] arr, Supplier<short[]> def) {
        return arr == null ? def.get() : arr;
    }

    public static int[] def(int[] arr, Supplier<int[]> def) {
        return arr == null ? def.get() : arr;
    }

    public static long[] def(long[] arr, Supplier<long[]> def) {
        return arr == null ? def.get() : arr;
    }

    public static float[] def(float[] arr, Supplier<float[]> def) {
        return arr == null ? def.get() : arr;
    }

    public static double[] def(double[] arr, Supplier<double[]> def) {
        return arr == null ? def.get() : arr;
    }

    public static boolean[] def(boolean[] arr, Supplier<boolean[]> def) {
        return arr == null ? def.get() : arr;
    }

    public static char[] def(char[] arr, Supplier<char[]> def) {
        return arr == null ? def.get() : arr;
    }

    // -------------------- length --------------------

    /**
     * 获取数组长度
     *
     * @param arr 数组 如果传参是一个基本类型数组, 返回值也是基本类型数组的长度
     * @return 长度
     * <p>
     * 在默认情况下, 如果可变参数o, 传参是基本类型的数组, 使用length属性判断, o.length = 1
     */
    public static int lengths(Object... arr) {
        if (arr == null) {
            return 0;
        }
        if (arr.length == 1) {
            Object t = arr[0];
            if (t instanceof byte[]) {
                return ((byte[]) t).length;
            } else if (t instanceof short[]) {
                return ((short[]) t).length;
            } else if (t instanceof int[]) {
                return ((int[]) t).length;
            } else if (t instanceof long[]) {
                return ((long[]) t).length;
            } else if (t instanceof float[]) {
                return ((float[]) t).length;
            } else if (t instanceof double[]) {
                return ((double[]) t).length;
            } else if (t instanceof char[]) {
                return ((char[]) t).length;
            } else if (t instanceof boolean[]) {
                return ((boolean[]) t).length;
            } else if (t instanceof Object[]) {
                return ((Object[]) t).length;
            }
        }
        return arr.length;
    }

    public static int lens(Object arr) {
        if (arr == null) {
            return 0;
        }
        if (isArray(arr)) {
            return Array.getLength(arr);
        } else {
            return 0;
        }
    }

    // -------------------- resize --------------------

    /**
     * 调整数组大小
     *
     * @param arr     原数组
     * @param newSize 新长度
     * @return 新数组
     */
    public static byte[] resize(byte[] arr, int newSize) {
        if (newSize <= 0) {
            return new byte[0];
        }
        if (arr.length < newSize) {
            byte[] nbs = new byte[newSize];
            System.arraycopy(arr, 0, nbs, 0, arr.length);
            return nbs;
        } else if (arr.length > newSize) {
            byte[] nbs = new byte[newSize];
            System.arraycopy(arr, 0, nbs, 0, newSize);
            return nbs;
        }
        return arr;
    }

    public static short[] resize(short[] arr, int newSize) {
        if (newSize <= 0) {
            return new short[0];
        }
        if (arr.length < newSize) {
            short[] nbs = new short[newSize];
            System.arraycopy(arr, 0, nbs, 0, arr.length);
            return nbs;
        } else if (arr.length > newSize) {
            short[] nbs = new short[newSize];
            System.arraycopy(arr, 0, nbs, 0, newSize);
            return nbs;
        }
        return arr;
    }

    public static int[] resize(int[] arr, int newSize) {
        if (newSize <= 0) {
            return new int[0];
        }
        if (arr.length < newSize) {
            int[] nbs = new int[newSize];
            System.arraycopy(arr, 0, nbs, 0, arr.length);
            return nbs;
        } else if (arr.length > newSize) {
            int[] nbs = new int[newSize];
            System.arraycopy(arr, 0, nbs, 0, newSize);
            return nbs;
        }
        return arr;
    }

    public static long[] resize(long[] arr, int newSize) {
        if (newSize <= 0) {
            return new long[0];
        }
        if (arr.length < newSize) {
            long[] nbs = new long[newSize];
            System.arraycopy(arr, 0, nbs, 0, arr.length);
            return nbs;
        } else if (arr.length > newSize) {
            long[] nbs = new long[newSize];
            System.arraycopy(arr, 0, nbs, 0, newSize);
            return nbs;
        }
        return arr;
    }

    public static float[] resize(float[] arr, int newSize) {
        if (newSize <= 0) {
            return new float[0];
        }
        if (arr.length < newSize) {
            float[] nbs = new float[newSize];
            System.arraycopy(arr, 0, nbs, 0, arr.length);
            return nbs;
        } else if (arr.length > newSize) {
            float[] nbs = new float[newSize];
            System.arraycopy(arr, 0, nbs, 0, newSize);
            return nbs;
        }
        return arr;
    }

    public static double[] resize(double[] arr, int newSize) {
        if (newSize <= 0) {
            return new double[0];
        }
        if (arr.length < newSize) {
            double[] nbs = new double[newSize];
            System.arraycopy(arr, 0, nbs, 0, arr.length);
            return nbs;
        } else if (arr.length > newSize) {
            double[] nbs = new double[newSize];
            System.arraycopy(arr, 0, nbs, 0, newSize);
            return nbs;
        }
        return arr;
    }

    public static boolean[] resize(boolean[] arr, int newSize) {
        if (newSize <= 0) {
            return new boolean[0];
        }
        if (arr.length < newSize) {
            boolean[] nbs = new boolean[newSize];
            System.arraycopy(arr, 0, nbs, 0, arr.length);
            return nbs;
        } else if (arr.length > newSize) {
            boolean[] nbs = new boolean[newSize];
            System.arraycopy(arr, 0, nbs, 0, newSize);
            return nbs;
        }
        return arr;
    }

    public static char[] resize(char[] arr, int newSize) {
        if (newSize <= 0) {
            return new char[0];
        }
        if (arr.length < newSize) {
            char[] nbs = new char[newSize];
            System.arraycopy(arr, 0, nbs, 0, arr.length);
            return nbs;
        } else if (arr.length > newSize) {
            char[] nbs = new char[newSize];
            System.arraycopy(arr, 0, nbs, 0, newSize);
            return nbs;
        }
        return arr;
    }


    public static <T> T[] resize(T[] arr, int newSize, IntFunction<T[]> generator) {
        if (newSize <= 0) {
            return generator.apply(0);
        }
        if (arr.length < newSize) {
            T[] nbs = generator.apply(newSize);
            System.arraycopy(arr, 0, nbs, 0, arr.length);
            return nbs;
        } else if (arr.length > newSize) {
            T[] nbs = generator.apply(newSize);
            System.arraycopy(arr, 0, nbs, 0, newSize);
            return nbs;
        }
        return arr;
    }

    // -------------------- arraycopy --------------------

    /**
     * 数组拷贝 自动扩容2倍
     *
     * @param src     原数组
     * @param srcPos  原数组起始位置
     * @param dest    目标数组
     * @param destPos 目标数组起始位置
     * @param length  拷贝原数组几位到目标数组
     * @return 扩容返回新目标数组 不扩容返回原目标数组
     */
    public static byte[] arraycopy(byte[] src, int srcPos, byte[] dest, int destPos, int length) {
        int newLen = destPos + length;
        if (newLen >= dest.length) {
            int newPos = dest.length * 2;
            if (newPos <= newLen) {
                newPos = newLen * 2;
            }
            byte[] bytes = new byte[newPos];
            System.arraycopy(dest, 0, bytes, 0, dest.length - (dest.length - destPos));
            System.arraycopy(src, srcPos, bytes, destPos, length);
            return bytes;
        } else {
            System.arraycopy(src, srcPos, dest, destPos, length);
            return dest;
        }
    }

    public static short[] arraycopy(short[] src, int srcPos, short[] dest, int destPos, int length) {
        int newLen = destPos + length;
        if (newLen >= dest.length) {
            int newPos = dest.length * 2;
            if (newPos <= newLen) {
                newPos = newLen * 2;
            }
            short[] shorts = new short[newPos];
            System.arraycopy(dest, 0, shorts, 0, dest.length - (dest.length - destPos));
            System.arraycopy(src, srcPos, shorts, destPos, length);
            return shorts;
        } else {
            System.arraycopy(src, srcPos, dest, destPos, length);
            return dest;
        }
    }

    public static int[] arraycopy(int[] src, int srcPos, int[] dest, int destPos, int length) {
        int newLen = destPos + length;
        if (newLen >= dest.length) {
            int newPos = dest.length * 2;
            if (newPos <= newLen) {
                newPos = newLen * 2;
            }
            int[] ints = new int[newPos];
            System.arraycopy(dest, 0, ints, 0, dest.length - (dest.length - destPos));
            System.arraycopy(src, srcPos, ints, destPos, length);
            return ints;
        } else {
            System.arraycopy(src, srcPos, dest, destPos, length);
            return dest;
        }
    }

    public static long[] arraycopy(long[] src, int srcPos, long[] dest, int destPos, int length) {
        int newLen = destPos + length;
        if (newLen >= dest.length) {
            int newPos = dest.length * 2;
            if (newPos <= newLen) {
                newPos = newLen * 2;
            }
            long[] longs = new long[newPos];
            System.arraycopy(dest, 0, longs, 0, dest.length - (dest.length - destPos));
            System.arraycopy(src, srcPos, longs, destPos, length);
            return longs;
        } else {
            System.arraycopy(src, srcPos, dest, destPos, length);
            return dest;
        }
    }

    public static float[] arraycopy(float[] src, int srcPos, float[] dest, int destPos, int length) {
        int newLen = destPos + length;
        if (newLen >= dest.length) {
            int newPos = dest.length * 2;
            if (newPos <= newLen) {
                newPos = newLen * 2;
            }
            float[] floats = new float[newPos];
            System.arraycopy(dest, 0, floats, 0, dest.length - (dest.length - destPos));
            System.arraycopy(src, srcPos, floats, destPos, length);
            return floats;
        } else {
            System.arraycopy(src, srcPos, dest, destPos, length);
            return dest;
        }
    }

    public static double[] arraycopy(double[] src, int srcPos, double[] dest, int destPos, int length) {
        int newLen = destPos + length;
        if (newLen >= dest.length) {
            int newPos = dest.length * 2;
            if (newPos <= newLen) {
                newPos = newLen * 2;
            }
            double[] doubles = new double[newPos];
            System.arraycopy(dest, 0, doubles, 0, dest.length - (dest.length - destPos));
            System.arraycopy(src, srcPos, doubles, destPos, length);
            return doubles;
        } else {
            System.arraycopy(src, srcPos, dest, destPos, length);
            return dest;
        }
    }

    public static boolean[] arraycopy(boolean[] src, int srcPos, boolean[] dest, int destPos, int length) {
        int newLen = destPos + length;
        if (newLen >= dest.length) {
            int newPos = dest.length * 2;
            if (newPos <= newLen) {
                newPos = newLen * 2;
            }
            boolean[] booleans = new boolean[newPos];
            System.arraycopy(dest, 0, booleans, 0, dest.length - (dest.length - destPos));
            System.arraycopy(src, srcPos, booleans, destPos, length);
            return booleans;
        } else {
            System.arraycopy(src, srcPos, dest, destPos, length);
            return dest;
        }
    }

    public static char[] arraycopy(char[] src, int srcPos, char[] dest, int destPos, int length) {
        int newLen = destPos + length;
        if (newLen >= dest.length) {
            int newPos = dest.length * 2;
            if (newPos <= newLen) {
                newPos = newLen * 2;
            }
            char[] chars = new char[newPos];
            System.arraycopy(dest, 0, chars, 0, dest.length - (dest.length - destPos));
            System.arraycopy(src, srcPos, chars, destPos, length);
            return chars;
        } else {
            System.arraycopy(src, srcPos, dest, destPos, length);
            return dest;
        }
    }

    public static Object[] arraycopy(Object[] src, int srcPos, Object[] dest, int destPos, int length) {
        int newLen = destPos + length;
        if (newLen >= dest.length) {
            int newPos = dest.length * 2;
            if (newPos <= newLen) {
                newPos = newLen * 2;
            }
            Object[] objects = new Object[newPos];
            System.arraycopy(dest, 0, objects, 0, dest.length - (dest.length - destPos));
            System.arraycopy(src, srcPos, objects, destPos, length);
            return objects;
        } else {
            System.arraycopy(src, srcPos, dest, destPos, length);
            return dest;
        }
    }

    public static <T> T[] arraycopys(T[] src, int srcPos, T[] dest, int destPos, int length, IntFunction<T[]> generator) {
        int destlen = lengths(dest);
        if (destPos + length >= destlen) {
            T[] apply = generator.apply(destlen * 2);
            System.arraycopy(dest, 0, apply, 0, destlen - (destlen - destPos));
            System.arraycopy(src, srcPos, apply, destPos, length);
            return apply;
        } else {
            System.arraycopy(src, srcPos, dest, destPos, length);
            return dest;
        }
    }

    // -------------------- length --------------------

    public static <T> int length(T[] arr) {
        return arr == null ? 0 : arr.length;
    }

    public static int length(byte[] arr) {
        return arr == null ? 0 : arr.length;
    }

    public static int length(short[] arr) {
        return arr == null ? 0 : arr.length;
    }

    public static int length(int[] arr) {
        return arr == null ? 0 : arr.length;
    }

    public static int length(long[] arr) {
        return arr == null ? 0 : arr.length;
    }

    public static int length(float[] arr) {
        return arr == null ? 0 : arr.length;
    }

    public static int length(double[] arr) {
        return arr == null ? 0 : arr.length;
    }

    public static int length(char[] arr) {
        return arr == null ? 0 : arr.length;
    }

    public static int length(boolean[] arr) {
        return arr == null ? 0 : arr.length;
    }

    // -------------------- wrap --------------------


    // -------------------- unWrap --------------------

    /**
     * 将数组转化为其基本装类
     *
     * @param arr 数组
     * @return 基本数组
     */

    public static Object unWrap(Object arr) {
        if (arr == null) {
            return null;
        } else if (arr instanceof Byte[]) {
            return unWrap((Byte[]) arr);
        } else if (arr instanceof Short[]) {
            return unWrap((Short[]) arr);
        } else if (arr instanceof Integer[]) {
            return unWrap((Integer[]) arr);
        } else if (arr instanceof Long[]) {
            return unWrap((Long[]) arr);
        } else if (arr instanceof Float[]) {
            return unWrap((Float[]) arr);
        } else if (arr instanceof Double[]) {
            return unWrap((Double[]) arr);
        } else if (arr instanceof Boolean[]) {
            return unWrap((Boolean[]) arr);
        } else if (arr instanceof Character[]) {
            return unWrap((Character[]) arr);
        } else {
            return arr;
        }
    }

    // -------------------- merge --------------------

    /**
     * 合并数组
     *
     * @param generator e.g. Integer[]::new
     * @param arr       需要合并的数组
     * @param merge     需要合并的数组
     * @param <T>       ignore
     * @return 合并后的数组
     */
    @SafeVarargs
    public static <T> T[] merges(IntFunction<T[]> generator, T[] arr, T[]... merge) {
        if (length(merge) == 0) {
            return arr;
        }
        int len = length(arr);
        int maxLen = len;
        for (Object[] m : merge) {
            maxLen += length(m);
        }
        T[] array = generator.apply(maxLen);
        if (len != 0) {
            System.arraycopy(arr, 0, array, 0, len);
        }
        for (T[] m : merge) {
            for (int j = 0, mLen = length(m); j < mLen; j++) {
                array[len++] = m[j];
            }
        }
        return array;
    }

    public static byte[] merge(byte[] arr, byte[]... merge) {
        if (length(merge) == 0) {
            return arr;
        }
        int len = length(arr);
        int maxLen = len;
        for (byte[] m : merge) {
            maxLen += length(m);
        }
        byte[] gem = new byte[maxLen];
        if (len != 0) {
            System.arraycopy(arr, 0, gem, 0, len);
        }
        for (byte[] m : merge) {
            for (int j = 0, mLen = length(m); j < mLen; j++) {
                gem[len++] = m[j];
            }
        }
        return gem;
    }

    public static short[] merge(short[] arr, short[]... merge) {
        if (length(merge) == 0) {
            return arr;
        }
        int len = length(arr);
        int maxLen = len;
        for (short[] m : merge) {
            maxLen += length(m);
        }
        short[] array = new short[maxLen];
        if (len != 0) {
            System.arraycopy(arr, 0, array, 0, len);
        }
        for (short[] m : merge) {
            for (int j = 0, mLen = length(m); j < mLen; j++) {
                array[len++] = m[j];
            }
        }
        return array;
    }

    public static int[] merge(int[] arr, int[]... merge) {
        if (length(merge) == 0) {
            return arr;
        }
        int len = length(arr);
        int maxLen = len;
        for (int[] m : merge) {
            maxLen += length(m);
        }
        int[] array = new int[maxLen];
        if (len != 0) {
            System.arraycopy(arr, 0, array, 0, len);
        }
        for (int[] m : merge) {
            for (int j = 0, mLen = length(m); j < mLen; j++) {
                array[len++] = m[j];
            }
        }
        return array;
    }

    public static long[] merge(long[] arr, long[]... merge) {
        if (length(merge) == 0) {
            return arr;
        }
        int len = length(arr);
        int maxLen = len;
        for (long[] m : merge) {
            maxLen += length(m);
        }
        long[] array = new long[maxLen];
        if (len != 0) {
            System.arraycopy(arr, 0, array, 0, len);
        }
        for (long[] m : merge) {
            for (int j = 0, mLen = length(m); j < mLen; j++) {
                array[len++] = m[j];
            }
        }
        return array;
    }

    public static float[] merge(float[] arr, float[]... merge) {
        if (length(merge) == 0) {
            return arr;
        }
        int len = length(arr);
        int maxLen = len;
        for (float[] m : merge) {
            maxLen += length(m);
        }
        float[] array = new float[maxLen];
        if (len != 0) {
            System.arraycopy(arr, 0, array, 0, len);
        }
        for (float[] m : merge) {
            for (int j = 0, mLen = length(m); j < mLen; j++) {
                array[len++] = m[j];
            }
        }
        return array;
    }

    public static double[] merge(double[] arr, double[]... merge) {
        if (length(merge) == 0) {
            return arr;
        }
        int len = length(arr);
        int maxLen = len;
        for (double[] m : merge) {
            maxLen += length(m);
        }
        double[] array = new double[maxLen];
        if (len != 0) {
            System.arraycopy(arr, 0, array, 0, len);
        }
        for (double[] m : merge) {
            for (int j = 0, mLen = length(m); j < mLen; j++) {
                array[len++] = m[j];
            }
        }
        return array;
    }

    public static boolean[] merge(boolean[] arr, boolean[]... merge) {
        if (length(merge) == 0) {
            return arr;
        }
        int len = length(arr);
        int maxLen = len;
        for (boolean[] m : merge) {
            maxLen += length(m);
        }
        boolean[] array = new boolean[maxLen];
        if (len != 0) {
            System.arraycopy(arr, 0, array, 0, len);
        }
        for (boolean[] m : merge) {
            for (int j = 0, mLen = length(m); j < mLen; j++) {
                array[len++] = m[j];
            }
        }
        return array;
    }

    public static char[] merge(char[] arr, char[]... merge) {
        if (length(merge) == 0) {
            return arr;
        }
        int len = length(arr);
        int maxLen = len;
        for (char[] m : merge) {
            maxLen += length(m);
        }
        char[] array = new char[maxLen];
        if (len != 0) {
            System.arraycopy(arr, 0, array, 0, len);
        }
        for (char[] m : merge) {
            for (int j = 0, mLen = length(m); j < mLen; j++) {
                array[len++] = m[j];
            }
        }
        return array;
    }

    public static Object[] merge(Object[] arr, Object[]... merge) {
        if (length(merge) == 0) {
            return arr;
        }
        int len = length(arr);
        int maxLen = len;
        for (Object[] m : merge) {
            maxLen += length(m);
        }
        Object[] array = new Object[maxLen];
        if (len != 0) {
            System.arraycopy(arr, 0, array, 0, len);
        }
        for (Object[] m : merge) {
            for (int j = 0, mLen = length(m); j < mLen; j++) {
                array[len++] = m[j];
            }
        }
        return array;
    }

    // -------------------- indexOf --------------------

    /**
     * 查找第一个查询到的元素的位置
     *
     * @param arr   数组
     * @param s     元素
     * @param start 开始向后查找的下标
     * @return 位置
     */
    public static int indexOf(byte[] arr, byte s, int start) {
        return indexOfs(arr, s, start);
    }

    public static int indexOf(short[] arr, short s, int start) {
        return indexOfs(arr, s, start);
    }

    public static int indexOf(int[] arr, int s, int start) {
        return indexOfs(arr, s, start);
    }

    public static int indexOf(long[] arr, long s, int start) {
        return indexOfs(arr, s, start);
    }

    public static int indexOf(float[] arr, float s, int start) {
        return indexOfs(arr, s, start);
    }

    public static int indexOf(double[] arr, double s, int start) {
        return indexOfs(arr, s, start);
    }

    public static int indexOf(char[] arr, char s, int start) {
        return indexOfs(arr, s, start);
    }

    public static int indexOf(boolean[] arr, boolean s, int start) {
        return indexOfs(arr, s, start);
    }


    private static <T> int indexOfs(T arr, T s, int start) {
        Object[] array = ofs(arr);
        int length = array.length;
        if (start < 0) {
            return -1;
        } else if (start >= length) {
            return -1;
        }
        for (int i = start; i < length; i++) {
            if (ObjectUtils.equals(array[i], s)) {
                return i;
            }
        }
        return -1;
    }

    // -------------------- lastIndexOf --------------------

    /**
     * 查找最后一个查询到的元素的位置
     *
     * @param arr   数组
     * @param s     元素
     * @param start 开始向前查找的下标
     * @return 位置
     */
    public static int lastIndexOf(byte[] arr, byte s, int start) {
        return lastIndexOfs(arr, s, start);
    }

    public static int lastIndexOf(short[] arr, short s, int start) {
        return lastIndexOfs(arr, s, start);
    }

    public static int lastIndexOf(int[] arr, int s, int start) {
        return lastIndexOfs(arr, s, start);
    }

    public static int lastIndexOf(long[] arr, long s, int start) {
        return lastIndexOfs(arr, s, start);
    }

    public static int lastIndexOf(float[] arr, float s, int start) {
        return lastIndexOfs(arr, s, start);
    }

    public static int lastIndexOf(double[] arr, double s, int start) {
        return lastIndexOfs(arr, s, start);
    }

    public static int lastIndexOf(char[] arr, char s, int start) {
        return lastIndexOfs(arr, s, start);
    }

    public static int lastIndexOf(boolean[] arr, boolean s, int start) {
        return lastIndexOfs(arr, s, start);
    }

    public static <T> int lastIndexOf(T[] arr, T s, int start) {
        return lastIndexOfs(arr, s, start);
    }


    private static <T> int lastIndexOfs(T arr, T s, int start) {
        Object[] array = ofs(arr);
        int length = array.length;
        if (start < 0) {
            return -1;
        } else if (start >= length) {
            return -1;
        }
        for (int i = start; i >= 0; i--) {
            if (ObjectUtils.equals(array[i], s)) {
                return i;
            }
        }
        return -1;
    }

    // -------------------- contains --------------------


    // -------------------- count --------------------

    /**
     * 查询目标元素在数组中出现的次数
     *
     * @param target 目标元素
     * @param arr    数组 可以传基本类型的元素 但是不可以传基本类型的数组
     * @return 次数
     */
    @SafeVarargs
    public static <T> int count(T target, T... arr) {
        if (length(arr) == 0) {
            return 0;
        }
        int count = 0;
        for (T i : arr) {
            if (ObjectUtils.equals(i, target)) {
                count++;
            }
        }
        return count;
    }

    public static int count(byte target, byte... arr) {
        if (length(arr) == 0) {
            return 0;
        }
        int count = 0;
        for (byte i : arr) {
            if (i == target) {
                count++;
            }
        }
        return count;
    }

    public static int count(short target, short... arr) {
        if (length(arr) == 0) {
            return 0;
        }
        int count = 0;
        for (short i : arr) {
            if (i == target) {
                count++;
            }
        }
        return count;
    }

    public static int count(int target, int... arr) {
        if (length(arr) == 0) {
            return 0;
        }
        int count = 0;
        for (int i : arr) {
            if (i == target) {
                count++;
            }
        }
        return count;
    }

    public static int count(long target, long... arr) {
        if (length(arr) == 0) {
            return 0;
        }
        int count = 0;
        for (long i : arr) {
            if (i == target) {
                count++;
            }
        }
        return count;
    }

    public static int count(float target, float... arr) {
        if (length(arr) == 0) {
            return 0;
        }
        int count = 0;
        for (float i : arr) {
            if (i == target) {
                count++;
            }
        }
        return count;
    }

    public static int count(double target, double... arr) {
        if (length(arr) == 0) {
            return 0;
        }
        int count = 0;
        for (double i : arr) {
            if (i == target) {
                count++;
            }
        }
        return count;
    }

    public static int count(boolean target, boolean... arr) {
        if (length(arr) == 0) {
            return 0;
        }
        int count = 0;
        for (boolean i : arr) {
            if (i == target) {
                count++;
            }
        }
        return count;
    }

    public static int count(char target, char... arr) {
        if (length(arr) == 0) {
            return 0;
        }
        int count = 0;
        for (char i : arr) {
            if (i == target) {
                count++;
            }
        }
        return count;
    }

    // -------------------- some --------------------

    /**
     * 查询目标元素是否出现在数组中
     *
     * @param target 目标元素
     * @param arr    数组, 可以传基本类型的元素, 但是不可以传基本类型的数组
     * @return true 出现
     */
    @SafeVarargs
    public static <T> boolean some(T target, T... arr) {
        if (length(arr) == 0) {
            return false;
        }
        for (T i : arr) {
            if (i == target) {
                return true;
            }
        }
        return false;
    }

    public static boolean some(byte target, byte... arr) {
        if (length(arr) == 0) {
            return false;
        }
        for (byte i : arr) {
            if (i == target) {
                return true;
            }
        }
        return false;
    }

    public static boolean some(short target, short... arr) {
        if (length(arr) == 0) {
            return false;
        }
        for (short i : arr) {
            if (i == target) {
                return true;
            }
        }
        return false;
    }

    public static boolean some(int target, int... arr) {
        if (length(arr) == 0) {
            return false;
        }
        for (int i : arr) {
            if (i == target) {
                return true;
            }
        }
        return false;
    }

    public static boolean some(long target, long... arr) {
        if (length(arr) == 0) {
            return false;
        }
        for (long i : arr) {
            if (i == target) {
                return true;
            }
        }
        return false;
    }

    public static boolean some(float target, float... arr) {
        if (length(arr) == 0) {
            return false;
        }
        for (float i : arr) {
            if (i == target) {
                return true;
            }
        }
        return false;
    }

    public static boolean some(double target, double... arr) {
        if (length(arr) == 0) {
            return false;
        }
        for (double i : arr) {
            if (i == target) {
                return true;
            }
        }
        return false;
    }

    public static boolean some(char target, char... arr) {
        if (length(arr) == 0) {
            return false;
        }
        for (char i : arr) {
            if (i == target) {
                return true;
            }
        }
        return false;
    }

    public static boolean some(boolean target, boolean... arr) {
        if (length(arr) == 0) {
            return false;
        }
        for (boolean i : arr) {
            if (i == target) {
                return true;
            }
        }
        return false;
    }

    // -------------------- reverse --------------------

    /**
     * 数组倒排
     *
     * @param arr 数组
     */


    // -------------------- first --------------------

    /**
     * 获取数组第一个元素
     *
     * @param arr array
     * @return 第一个元素 长度为0则抛出异常
     */
    public static <T> T first(T[] arr) {
        int length = length(arr);
        if (length == 0) {
            throw new IllegalArgumentException("array is empty");
        }
        return arr[0];
    }

    public static byte first(byte[] arr) {
        int length = length(arr);
        if (length == 0) {
            throw new IllegalArgumentException("array is empty");
        }
        return arr[0];
    }

    public static short first(short[] array) {
        int length = length(array);
        if (length == 0) {
            throw new IllegalArgumentException("array is empty");
        }
        return array[0];
    }

    public static int first(int[] arr) {
        int length = length(arr);
        if (length == 0) {
            throw new IllegalArgumentException("array is empty");
        }
        return arr[0];
    }

    public static long first(long[] arr) {
        int length = length(arr);
        if (length == 0) {
            throw new IllegalArgumentException("array is empty");
        }
        return arr[0];
    }

    public static float first(float[] arr) {
        int length = length(arr);
        if (length == 0) {
            throw new IllegalArgumentException("array is empty");
        }
        return arr[0];
    }

    public static double first(double[] arr) {
        int length = length(arr);
        if (length == 0) {
            throw new IllegalArgumentException("array is empty");
        }
        return arr[0];
    }

    public static boolean first(boolean[] arr) {
        int length = length(arr);
        if (length == 0) {
            throw new IllegalArgumentException("array is empty");
        }
        return arr[0];
    }

    public static char first(char[] arr) {
        int length = length(arr);
        if (length == 0) {
            throw new IllegalArgumentException("array is empty");
        }
        return arr[0];
    }

    /**
     * 获取数组第一个元素
     *
     * @param arr array
     * @param def 默认值
     * @return 第一个元素
     */
    public static <T> T first(T[] arr, T def) {
        int length = length(arr);
        if (length == 0) {
            return def;
        }
        return arr[0];
    }

    public static byte first(byte[] arr, byte def) {
        int length = length(arr);
        if (length == 0) {
            return def;
        }
        return arr[0];
    }

    public static short first(short[] arr, short def) {
        int length = length(arr);
        if (length == 0) {
            return def;
        }
        return arr[0];
    }

    public static int first(int[] arr, int def) {
        int length = length(arr);
        if (length == 0) {
            return def;
        }
        return arr[0];
    }

    public static long first(long[] arr, long def) {
        int length = length(arr);
        if (length == 0) {
            return def;
        }
        return arr[0];
    }

    public static float first(float[] arr, float def) {
        int length = length(arr);
        if (length == 0) {
            return def;
        }
        return arr[0];
    }

    public static double first(double[] arr, double def) {
        int length = length(arr);
        if (length == 0) {
            return def;
        }
        return arr[0];
    }

    public static boolean first(boolean[] arr, boolean def) {
        int length = length(arr);
        if (length == 0) {
            return def;
        }
        return arr[0];
    }

    public static char first(char[] arr, char def) {
        int length = length(arr);
        if (length == 0) {
            return def;
        }
        return arr[0];
    }

    // -------------------- last --------------------

    /**
     * 获取数组最后一个元素
     *
     * @param arr array
     * @return 第一个元素  长度为0则抛出异常
     */
    public static <T> T last(T[] arr) {
        int length = length(arr);
        if (length == 0) {
            throw new IllegalArgumentException("array is empty");
        }
        return arr[length - 1];
    }

    public static byte last(byte[] arr) {
        int length = length(arr);
        if (length == 0) {
            throw new IllegalArgumentException("array is empty");
        }
        return arr[length - 1];
    }

    public static short last(short[] arr) {
        int length = length(arr);
        if (length == 0) {
            throw new IllegalArgumentException("array is empty");
        }
        return arr[length - 1];
    }

    public static int last(int[] arr) {
        int length = length(arr);
        if (length == 0) {
            throw new IllegalArgumentException("array is empty");
        }
        return arr[length - 1];
    }

    public static long last(long[] arr) {
        int length = length(arr);
        if (length == 0) {
            throw new IllegalArgumentException("array is empty");
        }
        return arr[length - 1];
    }

    public static float last(float[] arr) {
        int length = length(arr);
        if (length == 0) {
            throw new IllegalArgumentException("array is empty");
        }
        return arr[length - 1];
    }

    public static double last(double[] arr) {
        int length = length(arr);
        if (length == 0) {
            throw new IllegalArgumentException("array is empty");
        }
        return arr[length - 1];
    }

    public static boolean last(boolean[] arr) {
        int length = length(arr);
        if (length == 0) {
            throw new IllegalArgumentException("array is empty");
        }
        return arr[length - 1];
    }

    public static char last(char[] arr) {
        int length = length(arr);
        if (length == 0) {
            throw new IllegalArgumentException("array is empty");
        }
        return arr[length - 1];
    }

    /**
     * 获取数组最后一个元素
     *
     * @param arr array
     * @param def 默认值
     * @return 第一个元素
     */
    public static <T> T last(T[] arr, T def) {
        int length = length(arr);
        if (length == 0) {
            return def;
        }
        return arr[length - 1];
    }

    public static byte last(byte[] arr, byte def) {
        int length = length(arr);
        if (length == 0) {
            return def;
        }
        return arr[length - 1];
    }

    public static short last(short[] arr, short def) {
        int length = length(arr);
        if (length == 0) {
            return def;
        }
        return arr[length - 1];
    }

    public static int last(int[] arr, int def) {
        int length = length(arr);
        if (length == 0) {
            return def;
        }
        return arr[length - 1];
    }

    public static long last(long[] arr, long def) {
        int length = length(arr);
        if (length == 0) {
            return def;
        }
        return arr[length - 1];
    }

    public static float last(float[] arr, float def) {
        int length = length(arr);
        if (length == 0) {
            return def;
        }
        return arr[length - 1];
    }

    public static double last(double[] arr, double def) {
        int length = length(arr);
        if (length == 0) {
            return def;
        }
        return arr[length - 1];
    }

    public static boolean last(boolean[] arr, boolean def) {
        int length = length(arr);
        if (length == 0) {
            return def;
        }
        return arr[length - 1];
    }

    public static char last(char[] arr, char def) {
        int length = length(arr);
        if (length == 0) {
            return def;
        }
        return arr[length - 1];
    }

    // -------------------- swap --------------------

    /**
     * 换位
     *
     * @param arr 数组
     * @param i   换位的元素1
     * @param j   换位的元素2
     */
    public static void unChangeSwap(byte[] arr, int i, int j) {
        arr[i] ^= arr[j];
        arr[j] ^= arr[i];
        arr[i] ^= arr[j];
    }


    // -------------------- max --------------------


    public static <T extends Comparable<T>> T max(T[] arr) {
        int len = length(arr);
        if (len == 0) {
            return null;
        }
        T max = arr[0];
        int offset = 1;
        if (max == null) {
            for (int blen = arr.length; offset < blen; offset++) {
                T bi = arr[offset];
                if (bi != null) {
                    max = bi;
                    offset++;
                    break;
                }
            }
            if (max == null) {
                return null;
            }
        }
        for (int i = offset; i < len; i++) {
            if (arr[i] != null && max.compareTo(arr[i]) < 0) {
                max = arr[i];
            }
        }
        return max;
    }

    public static <T> T max(T[] arr, Comparator<T> c) {
        int len = length(arr);
        if (len == 0) {
            return null;
        }
        T max = arr[0];
        int offset = 1;
        if (max == null) {
            for (int blen = arr.length; offset < blen; offset++) {
                T bi = arr[offset];
                if (bi != null) {
                    max = bi;
                    offset++;
                    break;
                }
            }
            if (max == null) {
                return null;
            }
        }
        for (int i = offset; i < len; i++) {
            if (arr[i] != null && c.compare(max, arr[i]) < 0) {
                max = arr[i];
            }
        }
        return max;
    }

    // -------------------- min --------------------

    /**
     * 最小值
     *
     * @param arr 数组
     * @return 最小值
     */


    public static <T extends Comparable<T>> T min(T[] arr) {
        int len = length(arr);
        if (len == 0) {
            return null;
        }
        T min = arr[0];
        int offset = 1;
        if (min == null) {
            for (int blen = arr.length; offset < blen; offset++) {
                T bi = arr[offset];
                if (bi != null) {
                    min = bi;
                    offset++;
                    break;
                }
            }
            if (min == null) {
                return null;
            }
        }
        for (int i = offset; i < len; i++) {
            if (arr[i] != null && min.compareTo(arr[i]) > 0) {
                min = arr[i];
            }
        }
        return min;
    }

    public static <T> T min(T[] arr, Comparator<T> c) {
        int len = length(arr);
        if (len == 0) {
            return null;
        }
        T min = arr[0];
        int offset = 1;
        if (min == null) {
            for (int blen = arr.length; offset < blen; offset++) {
                T bi = arr[offset];
                if (bi != null) {
                    min = bi;
                    offset++;
                    break;
                }
            }
            if (min == null) {
                return null;
            }
        }
        for (int i = offset; i < len; i++) {
            if (arr[i] != null && c.compare(min, arr[i]) > 0) {
                min = arr[i];
            }
        }
        return min;
    }

    // -------------------- hashcode --------------------

    /**
     * 获取数组的hashCode
     *
     * @param arr arr
     * @return hashCode
     */
    public static int hashCode(Object[] arr) {
        if (arr == null) {
            return 0;
        }
        int hash = INITIAL_HASH;
        for (Object element : arr) {
            hash = MULTIPLIER * hash + ObjectUtils.hashCode(element);
        }
        return hash;
    }

    public static int hashCode(byte[] arr) {
        if (arr == null) {
            return 0;
        }
        int hash = INITIAL_HASH;
        for (byte element : arr) {
            hash = MULTIPLIER * hash + element;
        }
        return hash;
    }

    public static int hashCode(short[] arr) {
        if (arr == null) {
            return 0;
        }
        int hash = INITIAL_HASH;
        for (short element : arr) {
            hash = MULTIPLIER * hash + element;
        }
        return hash;
    }

    public static int hashCode(int[] arr) {
        if (arr == null) {
            return 0;
        }
        int hash = INITIAL_HASH;
        for (int element : arr) {
            hash = MULTIPLIER * hash + element;
        }
        return hash;
    }

    public static int hashCode(long[] arr) {
        if (arr == null) {
            return 0;
        }
        int hash = INITIAL_HASH;
        for (long element : arr) {
            hash = MULTIPLIER * hash + Long.hashCode(element);
        }
        return hash;
    }

    public static int hashCode(float[] arr) {
        if (arr == null) {
            return 0;
        }
        int hash = INITIAL_HASH;
        for (float element : arr) {
            hash = MULTIPLIER * hash + Float.hashCode(element);
        }
        return hash;
    }

    public static int hashCode(double[] arr) {
        if (arr == null) {
            return 0;
        }
        int hash = INITIAL_HASH;
        for (double element : arr) {
            hash = MULTIPLIER * hash + Double.hashCode(element);
        }
        return hash;
    }

    public static int hashCode(boolean[] arr) {
        if (arr == null) {
            return 0;
        }
        int hash = INITIAL_HASH;
        for (boolean element : arr) {
            hash = MULTIPLIER * hash + Boolean.hashCode(element);
        }
        return hash;
    }

    public static int hashCode(char[] arr) {
        if (arr == null) {
            return 0;
        }
        int hash = INITIAL_HASH;
        for (char element : arr) {
            hash = MULTIPLIER * hash + element;
        }
        return hash;
    }

    /**
     * 判断数组是否相等
     *
     * @param o1 o1
     * @param o2 o2
     * @return ignore
     */
    public static boolean arrayEquals(Object o1, Object o2) {
        if (o1 instanceof Object[] && o2 instanceof Object[]) {
            return Arrays.equals((Object[]) o1, (Object[]) o2);
        }
        if (o1 instanceof boolean[] && o2 instanceof boolean[]) {
            return Arrays.equals((boolean[]) o1, (boolean[]) o2);
        }
        if (o1 instanceof byte[] && o2 instanceof byte[]) {
            return Arrays.equals((byte[]) o1, (byte[]) o2);
        }
        if (o1 instanceof char[] && o2 instanceof char[]) {
            return Arrays.equals((char[]) o1, (char[]) o2);
        }
        if (o1 instanceof double[] && o2 instanceof double[]) {
            return Arrays.equals((double[]) o1, (double[]) o2);
        }
        if (o1 instanceof float[] && o2 instanceof float[]) {
            return Arrays.equals((float[]) o1, (float[]) o2);
        }
        if (o1 instanceof int[] && o2 instanceof int[]) {
            return Arrays.equals((int[]) o1, (int[]) o2);
        }
        if (o1 instanceof long[] && o2 instanceof long[]) {
            return Arrays.equals((long[]) o1, (long[]) o2);
        }
        if (o1 instanceof short[] && o2 instanceof short[]) {
            return Arrays.equals((short[]) o1, (short[]) o2);
        }
        return false;
    }


    // ----------------------------------------------------------------------
    // isEmpty

    /**
     * 数组是否为空
     *
     * @param <T>   数组元素类型
     * @param array 数组
     * @return 是否为空
     */
    @SuppressWarnings("unchecked")
    public static <T> boolean isEmpty(final T... array) {
        return array == null || array.length == 0;
    }

    /**
     * 数组是否为空<br>
     * 此方法会匹配单一对象，如果此对象为{@code null}则返回true<br>
     * 如果此对象为非数组，理解为此对象为数组的第一个元素，则返回false<br>
     * 如果此对象为数组对象，数组长度大于0情况下返回false，否则返回true
     *
     * @param array 数组
     * @return 是否为空
     */
    public static boolean isEmpty(Object array) {
        if (null == array) {
            return true;
        } else if (isArray(array)) {
            return 0 == Array.getLength(array);
        }
        throw new RuntimeException("Object to provide is not a Array !");
    }

    /**
     * 数组是否为空
     *
     * @param array 数组
     * @return 是否为空
     */
    public static boolean isEmpty(final long... array) {
        return array == null || array.length == 0;
    }

    /**
     * 数组是否为空
     *
     * @param array 数组
     * @return 是否为空
     */
    public static boolean isEmpty(final int... array) {
        return array == null || array.length == 0;
    }

    /**
     * 数组是否为空
     *
     * @param array 数组
     * @return 是否为空
     */
    public static boolean isEmpty(final short... array) {
        return array == null || array.length == 0;
    }

    /**
     * 数组是否为空
     *
     * @param array 数组
     * @return 是否为空
     */
    public static boolean isEmpty(final char... array) {
        return array == null || array.length == 0;
    }

    /**
     * 数组是否为空
     *
     * @param array 数组
     * @return 是否为空
     */
    public static boolean isEmpty(final byte... array) {
        return array == null || array.length == 0;
    }

    /**
     * 数组是否为空
     *
     * @param array 数组
     * @return 是否为空
     */
    public static boolean isEmpty(final double... array) {
        return array == null || array.length == 0;
    }

    /**
     * 数组是否为空
     *
     * @param array 数组
     * @return 是否为空
     */
    public static boolean isEmpty(final float... array) {
        return array == null || array.length == 0;
    }

    /**
     * 数组是否为空
     *
     * @param array 数组
     * @return 是否为空
     */
    public static boolean isEmpty(final boolean... array) {
        return array == null || array.length == 0;
    }

    // ----------------------------------------------------------------------
    // isNotEmpty

    /**
     * 数组是否为非空
     *
     * @param <T>   数组元素类型
     * @param array 数组
     * @return 是否为非空
     */
    @SuppressWarnings("unchecked")
    public static <T> boolean isNotEmpty(final T... array) {
        return (array != null && array.length != 0);
    }

    /**
     * 数组是否为非空<br>
     * 此方法会匹配单一对象，如果此对象为{@code null}则返回false<br>
     * 如果此对象为非数组，理解为此对象为数组的第一个元素，则返回true<br>
     * 如果此对象为数组对象，数组长度大于0情况下返回true，否则返回false
     *
     * @param array 数组
     * @return 是否为非空
     */
    public static boolean isNotEmpty(final Object array) {
        return !isEmpty(array);
    }

    /**
     * 数组是否为非空
     *
     * @param array 数组
     * @return 是否为非空
     */
    public static boolean isNotEmpty(final long... array) {
        return (array != null && array.length != 0);
    }

    /**
     * 数组是否为非空
     *
     * @param array 数组
     * @return 是否为非空
     */
    public static boolean isNotEmpty(final int... array) {
        return (array != null && array.length != 0);
    }

    /**
     * 数组是否为非空
     *
     * @param array 数组
     * @return 是否为非空
     */
    public static boolean isNotEmpty(final short... array) {
        return (array != null && array.length != 0);
    }

    /**
     * 数组是否为非空
     *
     * @param array 数组
     * @return 是否为非空
     */
    public static boolean isNotEmpty(final char... array) {
        return (array != null && array.length != 0);
    }

    /**
     * 数组是否为非空
     *
     * @param array 数组
     * @return 是否为非空
     */
    public static boolean isNotEmpty(final byte... array) {
        return (array != null && array.length != 0);
    }

    /**
     * 数组是否为非空
     *
     * @param array 数组
     * @return 是否为非空
     */
    public static boolean isNotEmpty(final double... array) {
        return (array != null && array.length != 0);
    }

    /**
     * 数组是否为非空
     *
     * @param array 数组
     * @return 是否为非空
     */
    public static boolean isNotEmpty(final float... array) {
        return (array != null && array.length != 0);
    }

    /**
     * 数组是否为非空
     *
     * @param array 数组
     * @return 是否为非空
     */
    public static boolean isNotEmpty(final boolean... array) {
        return (array != null && array.length != 0);
    }

    /**
     * 是否包含{@code null}元素
     *
     * @param <T>   数组元素类型
     * @param array 被检查的数组
     * @return 是否包含{@code null}元素
     * @since 3.0.7
     */
    @SuppressWarnings("unchecked")
    public static <T> boolean hasNull(T... array) {
        if (isNotEmpty(array)) {
            for (T element : array) {
                if (null == element) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 返回数组中第一个非空元素
     *
     * @param <T>   数组元素类型
     * @param array 数组
     * @return 非空元素，如果不存在非空元素或数组为空，返回{@code null}
     * @since 3.0.7
     */
    @SuppressWarnings("unchecked")
    public static <T> T firstNonNull(T... array) {
        if (isNotEmpty(array)) {
            for (final T val : array) {
                if (null != val) {
                    return val;
                }
            }
        }
        return null;
    }

    /**
     * 新建一个空数组
     *
     * @param <T>           数组元素类型
     * @param componentType 元素类型
     * @param newSize       大小
     * @return 空数组
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] newArray(Class<?> componentType, int newSize) {
        return (T[]) Array.newInstance(componentType, newSize);
    }

    /**
     * 新建一个空数组
     *
     * @param newSize 大小
     * @return 空数组
     * @since 3.3.0
     */
    public static Object[] newArray(int newSize) {
        return new Object[newSize];
    }

    /**
     * 获取数组对象的元素类型
     *
     * @param array 数组对象
     * @return 元素类型
     * @since 3.2.2
     */
    public static Class<?> getComponentType(Object array) {
        return null == array ? null : array.getClass().getComponentType();
    }

    /**
     * 获取数组对象的元素类型
     *
     * @param arrayClass 数组类
     * @return 元素类型
     * @since 3.2.2
     */
    public static Class<?> getComponentType(Class<?> arrayClass) {
        return null == arrayClass ? null : arrayClass.getComponentType();
    }

    /**
     * 根据数组元素类型，获取数组的类型<br>
     * 方法是通过创建一个空数组从而获取其类型
     *
     * @param componentType 数组元素类型
     * @return 数组类型
     * @since 3.2.2
     */
    public static Class<?> getArrayType(Class<?> componentType) {
        return Array.newInstance(componentType, 0).getClass();
    }

    /**
     * 强转数组类型<br>
     * 强制转换的前提是数组元素类型可被强制转换<br>
     * 强制转换后会生成一个新数组
     *
     * @param type     数组类型或数组元素类型
     * @param arrayObj 原数组
     * @return 转换后的数组类型
     * @throws NullPointerException     提供参数为空
     * @throws IllegalArgumentException 参数arrayObj不是数组
     * @since 3.0.6
     */
    public static Object[] cast(Class<?> type, Object arrayObj) throws NullPointerException, IllegalArgumentException {
        if (null == arrayObj) {
            throw new NullPointerException("Argument [arrayObj] is null !");
        }
        if (false == arrayObj.getClass().isArray()) {
            throw new IllegalArgumentException("Argument [arrayObj] is not array !");
        }
        if (null == type) {
            return (Object[]) arrayObj;
        }

        final Class<?> componentType = type.isArray() ? type.getComponentType() : type;
        final Object[] array = (Object[]) arrayObj;
        final Object[] result = ArrayUtils.newArray(componentType, array.length);
        System.arraycopy(array, 0, result, 0, array.length);
        return result;
    }

    /**
     * 将新元素添加到已有数组中<br>
     * 添加新元素会生成一个新的数组，不影响原数组
     *
     * @param <T>         数组元素类型
     * @param buffer      已有数组
     * @param newElements 新元素
     * @return 新数组
     */
    @SafeVarargs
    public static <T> T[] append(T[] buffer, T... newElements) {
        if (isEmpty(buffer)) {
            return newElements;
        }
        return insert(buffer, buffer.length, newElements);
    }

    /**
     * 将新元素添加到已有数组中<br>
     * 添加新元素会生成一个新的数组，不影响原数组
     *
     * @param <T>         数组元素类型
     * @param array       已有数组
     * @param newElements 新元素
     * @return 新数组
     */
    @SafeVarargs
    public static <T> Object append(Object array, T... newElements) {
        if (isEmpty(array)) {
            return newElements;
        }
        return insert(array, length(array), newElements);
    }

    /**
     * 将元素值设置为数组的某个位置，当给定的index大于数组长度，则追加
     *
     * @param <T>    数组元素类型
     * @param buffer 已有数组
     * @param index  位置，大于长度追加，否则替换
     * @param value  新值
     * @return 新数组或原有数组
     * @since 4.1.2
     */
    public static <T> T[] setOrAppend(T[] buffer, int index, T value) {
        if (index < buffer.length) {
            Array.set(buffer, index, value);
            return buffer;
        } else {
            return append(buffer, value);
        }
    }

    /**
     * 将元素值设置为数组的某个位置，当给定的index大于数组长度，则追加
     *
     * @param array 已有数组
     * @param index 位置，大于长度追加，否则替换
     * @param value 新值
     * @return 新数组或原有数组
     * @since 4.1.2
     */
    public static Object setOrAppend(Object array, int index, Object value) {
        if (index < length(array)) {
            Array.set(array, index, value);
            return array;
        } else {
            return append(array, value);
        }
    }

    /**
     * 将新元素插入到到已有数组中的某个位置<br>
     * 添加新元素会生成一个新的数组，不影响原数组<br>
     * 如果插入位置为为负数，从原数组从后向前计数，若大于原数组长度，则空白处用null填充
     *
     * @param <T>         数组元素类型
     * @param buffer      已有数组
     * @param index       插入位置，此位置为对应此位置元素之前的空档
     * @param newElements 新元素
     * @return 新数组
     * @since 4.0.8
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] insert(T[] buffer, int index, T... newElements) {
        return (T[]) insert((Object) buffer, index, newElements);
    }

    /**
     * 将新元素插入到到已有数组中的某个位置<br>
     * 添加新元素会生成一个新的数组，不影响原数组<br>
     * 如果插入位置为为负数，从原数组从后向前计数，若大于原数组长度，则空白处用null填充
     *
     * @param <T>         数组元素类型
     * @param array       已有数组
     * @param index       插入位置，此位置为对应此位置元素之前的空档
     * @param newElements 新元素
     * @return 新数组
     * @since 4.0.8
     */
    @SuppressWarnings("unchecked")
    public static <T> Object insert(Object array, int index, T... newElements) {
        if (isEmpty(newElements)) {
            return array;
        }
        if (isEmpty(array)) {
            return newElements;
        }

        final int len = length(array);
        if (index < 0) {
            index = (index % len) + len;
        }

        final T[] result = newArray(array.getClass().getComponentType(), Math.max(len, index) + newElements.length);
        System.arraycopy(array, 0, result, 0, Math.min(len, index));
        System.arraycopy(newElements, 0, result, index, newElements.length);
        if (index < len) {
            System.arraycopy(array, index, result, index + newElements.length, len - index);
        }
        return result;
    }

    /**
     * 生成一个新的重新设置大小的数组<br>
     * 调整大小后拷贝原数组到新数组下。扩大则占位前N个位置，缩小则截断
     *
     * @param <T>           数组元素类型
     * @param buffer        原数组
     * @param newSize       新的数组大小
     * @param componentType 数组元素类型
     * @return 调整后的新数组
     */
    public static <T> T[] resize(T[] buffer, int newSize, Class<?> componentType) {
        T[] newArray = newArray(componentType, newSize);
        if (isNotEmpty(buffer)) {
            System.arraycopy(buffer, 0, newArray, 0, Math.min(buffer.length, newSize));
        }
        return newArray;
    }

    /**
     * 生成一个新的重新设置大小的数组<br>
     * 新数组的类型为原数组的类型，调整大小后拷贝原数组到新数组下。扩大则占位前N个位置，缩小则截断
     *
     * @param <T>     数组元素类型
     * @param buffer  原数组
     * @param newSize 新的数组大小
     * @return 调整后的新数组
     */
    public static <T> T[] resize(T[] buffer, int newSize) {
        return resize(buffer, newSize, buffer.getClass().getComponentType());
    }

    /**
     * 将多个数组合并在一起<br>
     * 忽略null的数组
     *
     * @param <T>    数组元素类型
     * @param arrays 数组集合
     * @return 合并后的数组
     */
    @SafeVarargs
    public static <T> T[] addAll(T[]... arrays) {
        if (arrays.length == 1) {
            return arrays[0];
        }

        int length = 0;
        for (T[] array : arrays) {
            if (array == null) {
                continue;
            }
            length += array.length;
        }
        T[] result = newArray(arrays.getClass().getComponentType().getComponentType(), length);

        length = 0;
        for (T[] array : arrays) {
            if (array == null) {
                continue;
            }
            System.arraycopy(array, 0, result, length, array.length);
            length += array.length;
        }
        return result;
    }

    /**
     * 包装 {@link System#arraycopy(Object, int, Object, int, int)}<br>
     * 数组复制
     *
     * @param src     源数组
     * @param srcPos  源数组开始位置
     * @param dest    目标数组
     * @param destPos 目标数组开始位置
     * @param length  拷贝数组长度
     * @return 目标数组
     * @since 3.0.6
     */
    public static Object copy(Object src, int srcPos, Object dest, int destPos, int length) {
        System.arraycopy(src, srcPos, dest, destPos, length);
        return dest;
    }

    /**
     * 包装 {@link System#arraycopy(Object, int, Object, int, int)}<br>
     * 数组复制，缘数组和目标数组都是从位置0开始复制
     *
     * @param src    源数组
     * @param dest   目标数组
     * @param length 拷贝数组长度
     * @return 目标数组
     * @since 3.0.6
     */
    public static Object copy(Object src, Object dest, int length) {
        System.arraycopy(src, 0, dest, 0, length);
        return dest;
    }

    /**
     * 克隆数组
     *
     * @param <T>   数组元素类型
     * @param array 被克隆的数组
     * @return 新数组
     */
    public static <T> T[] clone(T[] array) {
        if (array == null) {
            return null;
        }
        return array.clone();
    }

    /**
     * 克隆数组，如果非数组返回<code>null</code>
     *
     * @param <T> 数组元素类型
     * @param obj 数组对象
     * @return 克隆后的数组对象
     */
    @SuppressWarnings("unchecked")
    public static <T> T clone(final T obj) {
        if (null == obj) {
            return null;
        }
        if (isArray(obj)) {
            final Object result;
            final Class<?> componentType = obj.getClass().getComponentType();
            if (componentType.isPrimitive()) {// 原始类型
                int length = Array.getLength(obj);
                result = Array.newInstance(componentType, length);
                while (length-- > 0) {
                    Array.set(result, length, Array.get(obj, length));
                }
            } else {
                result = ((Object[]) obj).clone();
            }
            return (T) result;
        }
        return null;
    }

    /**
     * 生成一个从0开始的数字列表<br>
     *
     * @param excludedEnd 结束的数字（不包含）
     * @return 数字列表
     */
    public static int[] range(int excludedEnd) {
        return range(0, excludedEnd, 1);
    }

    /**
     * 生成一个数字列表<br>
     * 自动判定正序反序
     *
     * @param includedStart 开始的数字（包含）
     * @param excludedEnd   结束的数字（不包含）
     * @return 数字列表
     */
    public static int[] range(int includedStart, int excludedEnd) {
        return range(includedStart, excludedEnd, 1);
    }

    /**
     * 生成一个数字列表<br>
     * 自动判定正序反序
     *
     * @param includedStart 开始的数字（包含）
     * @param excludedEnd   结束的数字（不包含）
     * @param step          步进
     * @return 数字列表
     */
    public static int[] range(int includedStart, int excludedEnd, int step) {
        if (includedStart > excludedEnd) {
            int tmp = includedStart;
            includedStart = excludedEnd;
            excludedEnd = tmp;
        }

        if (step <= 0) {
            step = 1;
        }

        int deviation = excludedEnd - includedStart;
        int length = deviation / step;
        if (deviation % step != 0) {
            length += 1;
        }
        int[] range = new int[length];
        for (int i = 0; i < length; i++) {
            range[i] = includedStart;
            includedStart += step;
        }
        return range;
    }

    /**
     * 拆分byte数组为几个等份（最后一份可能小于len）
     *
     * @param array 数组
     * @param len   每个小节的长度
     * @return 拆分后的数组
     */
    public static byte[][] split(byte[] array, int len) {
        int x = array.length / len;
        int y = array.length % len;
        int z = 0;
        if (y != 0) {
            z = 1;
        }
        byte[][] arrays = new byte[x + z][];
        byte[] arr;
        for (int i = 0; i < x + z; i++) {
            arr = new byte[len];
            if (i == x + z - 1 && y != 0) {
                System.arraycopy(array, i * len, arr, 0, y);
            } else {
                System.arraycopy(array, i * len, arr, 0, len);
            }
            arrays[i] = arr;
        }
        return arrays;
    }

    /**
     * 映射键值（参考Python的zip()函数）<br>
     * 例如：<br>
     * keys = [a,b,c,d]<br>
     * values = [1,2,3,4]<br>
     * 则得到的Map是 {a=1, b=2, c=3, d=4}<br>
     * 如果两个数组长度不同，则只对应最短部分
     *
     * @param <K>     Key类型
     * @param <V>     Value类型
     * @param keys    键列表
     * @param values  值列表
     * @param isOrder 是否有序
     * @return Map
     * @since 3.0.4
     */
    public static <K, V> Map<K, V> zip(K[] keys, V[] values, boolean isOrder) {
        if (isEmpty(keys) || isEmpty(values)) {
            return null;
        }

        final int size = Math.min(keys.length, values.length);
        final Map<K, V> map = isOrder ? Maps.newLinkedHashMap() : Maps.newHashMap();
        for (int i = 0; i < size; i++) {
            map.put(keys[i], values[i]);
        }

        return map;
    }

    /**
     * 映射键值（参考Python的zip()函数），返回Map无序<br>
     * 例如：<br>
     * keys = [a,b,c,d]<br>
     * values = [1,2,3,4]<br>
     * 则得到的Map是 {a=1, b=2, c=3, d=4}<br>
     * 如果两个数组长度不同，则只对应最短部分
     *
     * @param <K>    Key类型
     * @param <V>    Value类型
     * @param keys   键列表
     * @param values 值列表
     * @return Map
     */
    public static <K, V> Map<K, V> zip(K[] keys, V[] values) {
        return zip(keys, values, false);
    }

    // ------------------------------------------------------------------- indexOf
    // and lastIndexOf and contains

    /**
     * 返回数组中指定元素所在位置，未找到返回{@link #INDEX_NOT_FOUND}
     *
     * @param <T>   数组类型
     * @param array 数组
     * @param value 被检查的元素
     * @return 数组中指定元素所在位置，未找到返回{@link #INDEX_NOT_FOUND}
     * @since 3.0.7
     */
    public static <T> int indexOf(T[] array, Object value) {
        if (null != array) {
            for (int i = 0; i < array.length; i++) {
                if (Objects.equals(value, array[i])) {
                    return i;
                }
            }
        }
        return INDEX_NOT_FOUND;
    }

    /**
     * 返回数组中指定元素所在位置，忽略大小写，未找到返回{@link #INDEX_NOT_FOUND}
     *
     * @param array 数组
     * @param value 被检查的元素
     * @return 数组中指定元素所在位置，未找到返回{@link #INDEX_NOT_FOUND}
     * @since 3.1.2
     */
    public static int indexOfIgnoreCase(CharSequence[] array, CharSequence value) {
        if (null != array) {
            for (int i = 0; i < array.length; i++) {
                if (StringUtils.equalsIgnoreCase(array[i], value)) {
                    return i;
                }
            }
        }
        return INDEX_NOT_FOUND;
    }

    /**
     * 返回数组中指定元素所在最后的位置，未找到返回{@link #INDEX_NOT_FOUND}
     *
     * @param <T>   数组类型
     * @param array 数组
     * @param value 被检查的元素
     * @return 数组中指定元素所在位置，未找到返回{@link #INDEX_NOT_FOUND}
     * @since 3.0.7
     */
    public static <T> int lastIndexOf(T[] array, Object value) {
        if (null != array) {
            for (int i = array.length - 1; i >= 0; i--) {
                if (Objects.equals(value, array[i])) {
                    return i;
                }
            }
        }
        return INDEX_NOT_FOUND;
    }

    /**
     * 数组中是否包含元素
     *
     * @param <T>   数组元素类型
     * @param array 数组
     * @param value 被检查的元素
     * @return 是否包含
     */
    public static <T> boolean contains(T[] array, T value) {
        return indexOf(array, value) > INDEX_NOT_FOUND;
    }

    /**
     * 数组中是否包含指定元素中的任意一个
     *
     * @param <T>    数组元素类型
     * @param array  数组
     * @param values 被检查的多个元素
     * @return 是否包含指定元素中的任意一个
     * @since 4.1.20
     */
    @SuppressWarnings("unchecked")
    public static <T> boolean containsAny(T[] array, T... values) {
        for (T value : values) {
            if (contains(array, value)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 数组中是否包含元素，忽略大小写
     *
     * @param array 数组
     * @param value 被检查的元素
     * @return 是否包含
     * @since 3.1.2
     */
    public static boolean containsIgnoreCase(CharSequence[] array, CharSequence value) {
        return indexOfIgnoreCase(array, value) > INDEX_NOT_FOUND;
    }

    /**
     * 返回数组中指定元素所在位置，未找到返回{@link #INDEX_NOT_FOUND}
     *
     * @param array 数组
     * @param value 被检查的元素
     * @return 数组中指定元素所在位置，未找到返回{@link #INDEX_NOT_FOUND}
     * @since 3.0.7
     */


    /**
     * 返回数组中指定元素所在最后的位置，未找到返回{@link #INDEX_NOT_FOUND}
     *
     * @param array 数组
     * @param value 被检查的元素
     * @return 数组中指定元素所在位置，未找到返回{@link #INDEX_NOT_FOUND}
     * @since 3.0.7
     */

    /**
     * 数组中是否包含元素
     *
     * @param array 数组
     * @param value 被检查的元素
     * @return 是否包含
     * @since 3.0.7
     */
    public static boolean contains(long[] array, long value) {
        return indexOf(array, value, 0) > INDEX_NOT_FOUND;
    }

    /**
     * 返回数组中指定元素所在位置，未找到返回{@link #INDEX_NOT_FOUND}
     *
     * @param array 数组
     * @param value 被检查的元素
     * @return 数组中指定元素所在位置，未找到返回{@link #INDEX_NOT_FOUND}
     * @since 3.0.7
     */
    public static int indexOf(int[] array, int value) {
        if (null != array) {
            for (int i = 0; i < array.length; i++) {
                if (value == array[i]) {
                    return i;
                }
            }
        }
        return INDEX_NOT_FOUND;
    }

    /**
     * 返回数组中指定元素所在最后的位置，未找到返回{@link #INDEX_NOT_FOUND}
     *
     * @param array 数组
     * @param value 被检查的元素
     * @return 数组中指定元素所在位置，未找到返回{@link #INDEX_NOT_FOUND}
     * @since 3.0.7
     */
    public static int lastIndexOf(int[] array, int value) {
        if (null != array) {
            for (int i = array.length - 1; i >= 0; i--) {
                if (value == array[i]) {
                    return i;
                }
            }
        }
        return INDEX_NOT_FOUND;
    }

    /**
     * 数组中是否包含元素
     *
     * @param array 数组
     * @param value 被检查的元素
     * @return 是否包含
     * @since 3.0.7
     */
    public static boolean contains(int[] array, int value) {
        return indexOf(array, value) > INDEX_NOT_FOUND;
    }

    /**
     * 返回数组中指定元素所在位置，未找到返回{@link #INDEX_NOT_FOUND}
     *
     * @param array 数组
     * @param value 被检查的元素
     * @return 数组中指定元素所在位置，未找到返回{@link #INDEX_NOT_FOUND}
     * @since 3.0.7
     */
    public static int indexOf(short[] array, short value) {
        if (null != array) {
            for (int i = 0; i < array.length; i++) {
                if (value == array[i]) {
                    return i;
                }
            }
        }
        return INDEX_NOT_FOUND;
    }

    /**
     * 返回数组中指定元素所在最后的位置，未找到返回{@link #INDEX_NOT_FOUND}
     *
     * @param array 数组
     * @param value 被检查的元素
     * @return 数组中指定元素所在位置，未找到返回{@link #INDEX_NOT_FOUND}
     * @since 3.0.7
     */
    public static int lastIndexOf(short[] array, short value) {
        if (null != array) {
            for (int i = array.length - 1; i >= 0; i--) {
                if (value == array[i]) {
                    return i;
                }
            }
        }
        return INDEX_NOT_FOUND;
    }

    /**
     * 数组中是否包含元素
     *
     * @param array 数组
     * @param value 被检查的元素
     * @return 是否包含
     * @since 3.0.7
     */
    public static boolean contains(short[] array, short value) {
        return indexOf(array, value) > INDEX_NOT_FOUND;
    }

    /**
     * 返回数组中指定元素所在位置，未找到返回{@link #INDEX_NOT_FOUND}
     *
     * @param array 数组
     * @param value 被检查的元素
     * @return 数组中指定元素所在位置，未找到返回{@link #INDEX_NOT_FOUND}
     * @since 3.0.7
     */
    public static int indexOf(char[] array, char value) {
        if (null != array) {
            for (int i = 0; i < array.length; i++) {
                if (value == array[i]) {
                    return i;
                }
            }
        }
        return INDEX_NOT_FOUND;
    }

    /**
     * 返回数组中指定元素所在最后的位置，未找到返回{@link #INDEX_NOT_FOUND}
     *
     * @param array 数组
     * @param value 被检查的元素
     * @return 数组中指定元素所在位置，未找到返回{@link #INDEX_NOT_FOUND}
     * @since 3.0.7
     */
    public static int lastIndexOf(char[] array, char value) {
        if (null != array) {
            for (int i = array.length - 1; i >= 0; i--) {
                if (value == array[i]) {
                    return i;
                }
            }
        }
        return INDEX_NOT_FOUND;
    }

    /**
     * 数组中是否包含元素
     *
     * @param array 数组
     * @param value 被检查的元素
     * @return 是否包含
     * @since 3.0.7
     */
    public static boolean contains(char[] array, char value) {
        return indexOf(array, value) > INDEX_NOT_FOUND;
    }

    /**
     * 返回数组中指定元素所在位置，未找到返回{@link #INDEX_NOT_FOUND}
     *
     * @param array 数组
     * @param value 被检查的元素
     * @return 数组中指定元素所在位置，未找到返回{@link #INDEX_NOT_FOUND}
     * @since 3.0.7
     */
    public static int indexOf(byte[] array, byte value) {
        if (null != array) {
            for (int i = 0; i < array.length; i++) {
                if (value == array[i]) {
                    return i;
                }
            }
        }
        return INDEX_NOT_FOUND;
    }

    /**
     * 返回数组中指定元素所在最后的位置，未找到返回{@link #INDEX_NOT_FOUND}
     *
     * @param array 数组
     * @param value 被检查的元素
     * @return 数组中指定元素所在位置，未找到返回{@link #INDEX_NOT_FOUND}
     * @since 3.0.7
     */
    public static int lastIndexOf(byte[] array, byte value) {
        if (null != array) {
            for (int i = array.length - 1; i >= 0; i--) {
                if (value == array[i]) {
                    return i;
                }
            }
        }
        return INDEX_NOT_FOUND;
    }

    /**
     * 数组中是否包含元素
     *
     * @param array 数组
     * @param value 被检查的元素
     * @return 是否包含
     * @since 3.0.7
     */
    public static boolean contains(byte[] array, byte value) {
        return indexOf(array, value) > INDEX_NOT_FOUND;
    }

    /**
     * 返回数组中指定元素所在位置，未找到返回{@link #INDEX_NOT_FOUND}
     *
     * @param array 数组
     * @param value 被检查的元素
     * @return 数组中指定元素所在位置，未找到返回{@link #INDEX_NOT_FOUND}
     * @since 3.0.7
     */
    public static int indexOf(double[] array, double value) {
        if (null != array) {
            for (int i = 0; i < array.length; i++) {
                if (value == array[i]) {
                    return i;
                }
            }
        }
        return INDEX_NOT_FOUND;
    }

    /**
     * 返回数组中指定元素所在最后的位置，未找到返回{@link #INDEX_NOT_FOUND}
     *
     * @param array 数组
     * @param value 被检查的元素
     * @return 数组中指定元素所在位置，未找到返回{@link #INDEX_NOT_FOUND}
     * @since 3.0.7
     */
    public static int lastIndexOf(double[] array, double value) {
        if (null != array) {
            for (int i = array.length - 1; i >= 0; i--) {
                if (value == array[i]) {
                    return i;
                }
            }
        }
        return INDEX_NOT_FOUND;
    }

    /**
     * 数组中是否包含元素
     *
     * @param array 数组
     * @param value 被检查的元素
     * @return 是否包含
     * @since 3.0.7
     */
    public static boolean contains(double[] array, double value) {
        return indexOf(array, value) > INDEX_NOT_FOUND;
    }

    /**
     * 返回数组中指定元素所在位置，未找到返回{@link #INDEX_NOT_FOUND}
     *
     * @param array 数组
     * @param value 被检查的元素
     * @return 数组中指定元素所在位置，未找到返回{@link #INDEX_NOT_FOUND}
     * @since 3.0.7
     */
    public static int indexOf(float[] array, float value) {
        if (null != array) {
            for (int i = 0; i < array.length; i++) {
                if (value == array[i]) {
                    return i;
                }
            }
        }
        return INDEX_NOT_FOUND;
    }

    /**
     * 返回数组中指定元素所在最后的位置，未找到返回{@link #INDEX_NOT_FOUND}
     *
     * @param array 数组
     * @param value 被检查的元素
     * @return 数组中指定元素所在位置，未找到返回{@link #INDEX_NOT_FOUND}
     * @since 3.0.7
     */
    public static int lastIndexOf(float[] array, float value) {
        if (null != array) {
            for (int i = array.length - 1; i >= 0; i--) {
                if (value == array[i]) {
                    return i;
                }
            }
        }
        return INDEX_NOT_FOUND;
    }

    /**
     * 数组中是否包含元素
     *
     * @param array 数组
     * @param value 被检查的元素
     * @return 是否包含
     * @since 3.0.7
     */
    public static boolean contains(float[] array, float value) {
        return indexOf(array, value) > INDEX_NOT_FOUND;
    }

    /**
     * 返回数组中指定元素所在位置，未找到返回{@link #INDEX_NOT_FOUND}
     *
     * @param array 数组
     * @param value 被检查的元素
     * @return 数组中指定元素所在位置，未找到返回{@link #INDEX_NOT_FOUND}
     * @since 3.0.7
     */
    public static int indexOf(boolean[] array, boolean value) {
        if (null != array) {
            for (int i = 0; i < array.length; i++) {
                if (value == array[i]) {
                    return i;
                }
            }
        }
        return INDEX_NOT_FOUND;
    }

    /**
     * 返回数组中指定元素所在最后的位置，未找到返回{@link #INDEX_NOT_FOUND}
     *
     * @param array 数组
     * @param value 被检查的元素
     * @return 数组中指定元素所在位置，未找到返回{@link #INDEX_NOT_FOUND}
     * @since 3.0.7
     */
    public static int lastIndexOf(boolean[] array, boolean value) {
        if (null != array) {
            for (int i = array.length - 1; i >= 0; i--) {
                if (value == array[i]) {
                    return i;
                }
            }
        }
        return INDEX_NOT_FOUND;
    }

    /**
     * 数组中是否包含元素
     *
     * @param array 数组
     * @param value 被检查的元素
     * @return 是否包含
     * @since 3.0.7
     */
    public static boolean contains(boolean[] array, boolean value) {
        return indexOf(array, value) > INDEX_NOT_FOUND;
    }

    // ------------------------------------------------------------------- Wrap and
    // unwrap

    /**
     * 将原始类型数组包装为包装类型
     *
     * @param values 原始类型数组
     * @return 包装类型数组
     */
    public static Integer[] wrap(int... values) {
        if (null == values) {
            return null;
        }
        final int length = values.length;
        if (0 == length) {
            return new Integer[0];
        }

        final Integer[] array = new Integer[length];
        for (int i = 0; i < length; i++) {
            array[i] = Integer.valueOf(values[i]);
        }
        return array;
    }

    /**
     * 包装类数组转为原始类型数组
     *
     * @param values 包装类型数组
     * @return 原始类型数组
     */
    public static int[] unWrap(Integer... values) {
        if (null == values) {
            return null;
        }
        final int length = values.length;
        if (0 == length) {
            return new int[0];
        }

        final int[] array = new int[length];
        for (int i = 0; i < length; i++) {
            array[i] = values[i].intValue();
        }
        return array;
    }

    /**
     * 将原始类型数组包装为包装类型
     *
     * @param values 原始类型数组
     * @return 包装类型数组
     */
    public static Long[] wrap(long... values) {
        if (null == values) {
            return null;
        }
        final int length = values.length;
        if (0 == length) {
            return new Long[0];
        }

        final Long[] array = new Long[length];
        for (int i = 0; i < length; i++) {
            array[i] = Long.valueOf(values[i]);
        }
        return array;
    }

    /**
     * 包装类数组转为原始类型数组
     *
     * @param values 包装类型数组
     * @return 原始类型数组
     */
    public static long[] unWrap(Long... values) {
        if (null == values) {
            return null;
        }
        final int length = values.length;
        if (0 == length) {
            return new long[0];
        }

        final long[] array = new long[length];
        for (int i = 0; i < length; i++) {
            array[i] = values[i].longValue();
        }
        return array;
    }

    /**
     * 将原始类型数组包装为包装类型
     *
     * @param values 原始类型数组
     * @return 包装类型数组
     */
    public static Character[] wrap(char... values) {
        if (null == values) {
            return null;
        }
        final int length = values.length;
        if (0 == length) {
            return new Character[0];
        }

        final Character[] array = new Character[length];
        for (int i = 0; i < length; i++) {
            array[i] = Character.valueOf(values[i]);
        }
        return array;
    }

    /**
     * 包装类数组转为原始类型数组
     *
     * @param values 包装类型数组
     * @return 原始类型数组
     */
    public static char[] unWrap(Character... values) {
        if (null == values) {
            return null;
        }
        final int length = values.length;
        if (0 == length) {
            return new char[0];
        }

        char[] array = new char[length];
        for (int i = 0; i < length; i++) {
            array[i] = values[i].charValue();
        }
        return array;
    }

    /**
     * 将原始类型数组包装为包装类型
     *
     * @param values 原始类型数组
     * @return 包装类型数组
     */
    public static Byte[] wrap(byte... values) {
        if (null == values) {
            return null;
        }
        final int length = values.length;
        if (0 == length) {
            return new Byte[0];
        }

        final Byte[] array = new Byte[length];
        for (int i = 0; i < length; i++) {
            array[i] = Byte.valueOf(values[i]);
        }
        return array;
    }

    /**
     * 包装类数组转为原始类型数组
     *
     * @param values 包装类型数组
     * @return 原始类型数组
     */
    public static byte[] unWrap(Byte... values) {
        if (null == values) {
            return null;
        }
        final int length = values.length;
        if (0 == length) {
            return new byte[0];
        }

        final byte[] array = new byte[length];
        for (int i = 0; i < length; i++) {
            array[i] = values[i].byteValue();
        }
        return array;
    }

    /**
     * 将原始类型数组包装为包装类型
     *
     * @param values 原始类型数组
     * @return 包装类型数组
     */
    public static Short[] wrap(short... values) {
        if (null == values) {
            return null;
        }
        final int length = values.length;
        if (0 == length) {
            return new Short[0];
        }

        final Short[] array = new Short[length];
        for (int i = 0; i < length; i++) {
            array[i] = Short.valueOf(values[i]);
        }
        return array;
    }

    /**
     * 包装类数组转为原始类型数组
     *
     * @param values 包装类型数组
     * @return 原始类型数组
     */
    public static short[] unWrap(Short... values) {
        if (null == values) {
            return null;
        }
        final int length = values.length;
        if (0 == length) {
            return new short[0];
        }

        final short[] array = new short[length];
        for (int i = 0; i < length; i++) {
            array[i] = values[i].shortValue();
        }
        return array;
    }

    /**
     * 将原始类型数组包装为包装类型
     *
     * @param values 原始类型数组
     * @return 包装类型数组
     */
    public static Float[] wrap(float... values) {
        if (null == values) {
            return null;
        }
        final int length = values.length;
        if (0 == length) {
            return new Float[0];
        }

        final Float[] array = new Float[length];
        for (int i = 0; i < length; i++) {
            array[i] = Float.valueOf(values[i]);
        }
        return array;
    }

    /**
     * 包装类数组转为原始类型数组
     *
     * @param values 包装类型数组
     * @return 原始类型数组
     */
    public static float[] unWrap(Float... values) {
        if (null == values) {
            return null;
        }
        final int length = values.length;
        if (0 == length) {
            return new float[0];
        }

        final float[] array = new float[length];
        for (int i = 0; i < length; i++) {
            array[i] = values[i].floatValue();
        }
        return array;
    }

    /**
     * 将原始类型数组包装为包装类型
     *
     * @param values 原始类型数组
     * @return 包装类型数组
     */
    public static Double[] wrap(double... values) {
        if (null == values) {
            return null;
        }
        final int length = values.length;
        if (0 == length) {
            return new Double[0];
        }

        final Double[] array = new Double[length];
        for (int i = 0; i < length; i++) {
            array[i] = Double.valueOf(values[i]);
        }
        return array;
    }

    /**
     * 包装类数组转为原始类型数组
     *
     * @param values 包装类型数组
     * @return 原始类型数组
     */
    public static double[] unWrap(Double... values) {
        if (null == values) {
            return null;
        }
        final int length = values.length;
        if (0 == length) {
            return new double[0];
        }

        final double[] array = new double[length];
        for (int i = 0; i < length; i++) {
            array[i] = values[i].doubleValue();
        }
        return array;
    }

    /**
     * 将原始类型数组包装为包装类型
     *
     * @param values 原始类型数组
     * @return 包装类型数组
     */
    public static Boolean[] wrap(boolean... values) {
        if (null == values) {
            return null;
        }
        final int length = values.length;
        if (0 == length) {
            return new Boolean[0];
        }

        final Boolean[] array = new Boolean[length];
        for (int i = 0; i < length; i++) {
            array[i] = Boolean.valueOf(values[i]);
        }
        return array;
    }

    /**
     * 包装类数组转为原始类型数组
     *
     * @param values 包装类型数组
     * @return 原始类型数组
     */
    public static boolean[] unWrap(Boolean... values) {
        if (null == values) {
            return null;
        }
        final int length = values.length;
        if (0 == length) {
            return new boolean[0];
        }

        final boolean[] array = new boolean[length];
        for (int i = 0; i < length; i++) {
            array[i] = values[i].booleanValue();
        }
        return array;
    }

    /**
     * 包装数组对象
     *
     * @param obj 对象，可以是对象数组或者基本类型数组
     * @return 包装类型数组或对象数组
     * @throws RuntimeException 对象为非数组
     */
    public static Object[] wrap(Object obj) {
        if (null == obj) {
            return null;
        }
        if (isArray(obj)) {
            try {
                return (Object[]) obj;
            } catch (Exception e) {
                final String className = obj.getClass().getComponentType().getName();
                switch (className) {
                    case "long":
                        return wrap((long[]) obj);
                    case "int":
                        return wrap((int[]) obj);
                    case "short":
                        return wrap((short[]) obj);
                    case "char":
                        return wrap((char[]) obj);
                    case "byte":
                        return wrap((byte[]) obj);
                    case "boolean":
                        return wrap((boolean[]) obj);
                    case "float":
                        return wrap((float[]) obj);
                    case "double":
                        return wrap((double[]) obj);
                    default:
                        throw new RuntimeException(e);
                }
            }
        }
        throw new RuntimeException(StringUtils.format("[{}] is not Array!", obj.getClass()));
    }

    /**
     * 对象是否为数组对象
     *
     * @param obj 对象
     * @return 是否为数组对象，如果为{@code null} 返回false
     */
    public static boolean isArray(Object obj) {
        if (null == obj) {
            // throw new NullPointerException("Object check for isArray is null");
            return false;
        }
        return obj.getClass().isArray();
    }

    /**
     * @param o1
     * @param o2
     * @return
     */
    public static boolean equals(Object o1, Object o2) {
        if (o1 instanceof Object[] && o2 instanceof Object[]) {
            return Arrays.equals((Object[]) o1, (Object[]) o2);
        }
        if (o1 instanceof boolean[] && o2 instanceof boolean[]) {
            return Arrays.equals((boolean[]) o1, (boolean[]) o2);
        }
        if (o1 instanceof byte[] && o2 instanceof byte[]) {
            return Arrays.equals((byte[]) o1, (byte[]) o2);
        }
        if (o1 instanceof char[] && o2 instanceof char[]) {
            return Arrays.equals((char[]) o1, (char[]) o2);
        }
        if (o1 instanceof double[] && o2 instanceof double[]) {
            return Arrays.equals((double[]) o1, (double[]) o2);
        }
        if (o1 instanceof float[] && o2 instanceof float[]) {
            return Arrays.equals((float[]) o1, (float[]) o2);
        }
        if (o1 instanceof int[] && o2 instanceof int[]) {
            return Arrays.equals((int[]) o1, (int[]) o2);
        }
        if (o1 instanceof long[] && o2 instanceof long[]) {
            return Arrays.equals((long[]) o1, (long[]) o2);
        }
        if (o1 instanceof short[] && o2 instanceof short[]) {
            return Arrays.equals((short[]) o1, (short[]) o2);
        }
        return false;
    }


    /**
     * 获取数组对象中指定index的值，支持负数，例如-1表示倒数第一个值<br>
     * 如果数组下标越界，返回null
     *
     * @param <T>   数组元素类型
     * @param array 数组对象
     * @param index 下标，支持负数
     * @return 值
     * @since 4.0.6
     */
    @SuppressWarnings("unchecked")
    public static <T> T get(Object array, int index) {
        if (null == array) {
            return null;
        }

        if (index < 0) {
            index += Array.getLength(array);
        }
        try {
            return (T) Array.get(array, index);
        } catch (ArrayIndexOutOfBoundsException e) {
            return null;
        }
    }

    /**
     * 获取数组中指定多个下标元素值，组成新数组
     *
     * @param <T>     数组元素类型
     * @param array   数组
     * @param indexes 下标列表
     * @return 结果
     */
    public static <T> T[] getAny(Object array, int... indexes) {
        if (null == array) {
            return null;
        }

        final T[] result = newArray(array.getClass().getComponentType(), indexes.length);
        for (int i : indexes) {
            result[i] = get(array, i);
        }
        return result;
    }

    /**
     * 获取子数组
     *
     * @param array 数组
     * @param start 开始位置（包括）
     * @param end   结束位置（不包括）
     * @return 新的数组
     * @see Arrays#copyOfRange(Object[], int, int)
     * @since 4.2.2
     */
    public static <T> T[] sub(T[] array, int start, int end) {
        int length = length(array);
        if (start < 0) {
            start += length;
        }
        if (end < 0) {
            end += length;
        }
        if (start == length) {
            return newArray(array.getClass().getComponentType(), 0);
        }
        if (start > end) {
            int tmp = start;
            start = end;
            end = tmp;
        }
        if (end > length) {
            if (start >= length) {
                return newArray(array.getClass().getComponentType(), 0);
            }
            end = length;
        }
        return Arrays.copyOfRange(array, start, end);
    }

    /**
     * 获取子数组
     *
     * @param array 数组
     * @param start 开始位置（包括）
     * @param end   结束位置（不包括）
     * @return 新的数组
     * @see Arrays#copyOfRange(Object[], int, int)
     * @since 4.5.2
     */
    public static byte[] sub(byte[] array, int start, int end) {
        int length = length(array);
        if (start < 0) {
            start += length;
        }
        if (end < 0) {
            end += length;
        }
        if (start == length) {
            return new byte[0];
        }
        if (start > end) {
            int tmp = start;
            start = end;
            end = tmp;
        }
        if (end > length) {
            if (start >= length) {
                return new byte[0];
            }
            end = length;
        }
        return Arrays.copyOfRange(array, start, end);
    }

    /**
     * 获取子数组
     *
     * @param array 数组
     * @param start 开始位置（包括）
     * @param end   结束位置（不包括）
     * @return 新的数组
     * @see Arrays#copyOfRange(Object[], int, int)
     * @since 4.5.2
     */
    public static int[] sub(int[] array, int start, int end) {
        int length = length(array);
        if (start < 0) {
            start += length;
        }
        if (end < 0) {
            end += length;
        }
        if (start == length) {
            return new int[0];
        }
        if (start > end) {
            int tmp = start;
            start = end;
            end = tmp;
        }
        if (end > length) {
            if (start >= length) {
                return new int[0];
            }
            end = length;
        }
        return Arrays.copyOfRange(array, start, end);
    }

    /**
     * 获取子数组
     *
     * @param array 数组
     * @param start 开始位置（包括）
     * @param end   结束位置（不包括）
     * @return 新的数组
     * @see Arrays#copyOfRange(Object[], int, int)
     * @since 4.5.2
     */
    public static long[] sub(long[] array, int start, int end) {
        int length = length(array);
        if (start < 0) {
            start += length;
        }
        if (end < 0) {
            end += length;
        }
        if (start == length) {
            return new long[0];
        }
        if (start > end) {
            int tmp = start;
            start = end;
            end = tmp;
        }
        if (end > length) {
            if (start >= length) {
                return new long[0];
            }
            end = length;
        }
        return Arrays.copyOfRange(array, start, end);
    }

    /**
     * 获取子数组
     *
     * @param array 数组
     * @param start 开始位置（包括）
     * @param end   结束位置（不包括）
     * @return 新的数组
     * @see Arrays#copyOfRange(Object[], int, int)
     * @since 4.5.2
     */
    public static short[] sub(short[] array, int start, int end) {
        int length = length(array);
        if (start < 0) {
            start += length;
        }
        if (end < 0) {
            end += length;
        }
        if (start == length) {
            return new short[0];
        }
        if (start > end) {
            int tmp = start;
            start = end;
            end = tmp;
        }
        if (end > length) {
            if (start >= length) {
                return new short[0];
            }
            end = length;
        }
        return Arrays.copyOfRange(array, start, end);
    }

    /**
     * 获取子数组
     *
     * @param array 数组
     * @param start 开始位置（包括）
     * @param end   结束位置（不包括）
     * @return 新的数组
     * @see Arrays#copyOfRange(Object[], int, int)
     * @since 4.5.2
     */
    public static char[] sub(char[] array, int start, int end) {
        int length = length(array);
        if (start < 0) {
            start += length;
        }
        if (end < 0) {
            end += length;
        }
        if (start == length) {
            return new char[0];
        }
        if (start > end) {
            int tmp = start;
            start = end;
            end = tmp;
        }
        if (end > length) {
            if (start >= length) {
                return new char[0];
            }
            end = length;
        }
        return Arrays.copyOfRange(array, start, end);
    }

    /**
     * 获取子数组
     *
     * @param array 数组
     * @param start 开始位置（包括）
     * @param end   结束位置（不包括）
     * @return 新的数组
     * @see Arrays#copyOfRange(Object[], int, int)
     * @since 4.5.2
     */
    public static double[] sub(double[] array, int start, int end) {
        int length = length(array);
        if (start < 0) {
            start += length;
        }
        if (end < 0) {
            end += length;
        }
        if (start == length) {
            return new double[0];
        }
        if (start > end) {
            int tmp = start;
            start = end;
            end = tmp;
        }
        if (end > length) {
            if (start >= length) {
                return new double[0];
            }
            end = length;
        }
        return Arrays.copyOfRange(array, start, end);
    }

    /**
     * 获取子数组
     *
     * @param array 数组
     * @param start 开始位置（包括）
     * @param end   结束位置（不包括）
     * @return 新的数组
     * @see Arrays#copyOfRange(Object[], int, int)
     * @since 4.5.2
     */
    public static float[] sub(float[] array, int start, int end) {
        int length = length(array);
        if (start < 0) {
            start += length;
        }
        if (end < 0) {
            end += length;
        }
        if (start == length) {
            return new float[0];
        }
        if (start > end) {
            int tmp = start;
            start = end;
            end = tmp;
        }
        if (end > length) {
            if (start >= length) {
                return new float[0];
            }
            end = length;
        }
        return Arrays.copyOfRange(array, start, end);
    }

    /**
     * 获取子数组
     *
     * @param array 数组
     * @param start 开始位置（包括）
     * @param end   结束位置（不包括）
     * @return 新的数组
     * @see Arrays#copyOfRange(Object[], int, int)
     * @since 4.5.2
     */
    public static boolean[] sub(boolean[] array, int start, int end) {
        int length = length(array);
        if (start < 0) {
            start += length;
        }
        if (end < 0) {
            end += length;
        }
        if (start == length) {
            return new boolean[0];
        }
        if (start > end) {
            int tmp = start;
            start = end;
            end = tmp;
        }
        if (end > length) {
            if (start >= length) {
                return new boolean[0];
            }
            end = length;
        }
        return Arrays.copyOfRange(array, start, end);
    }

    /**
     * 获取子数组
     *
     * @param array 数组
     * @param start 开始位置（包括）
     * @param end   结束位置（不包括）
     * @return 新的数组
     * @since 4.0.6
     */
    public static Object[] sub(Object array, int start, int end) {
        return sub(array, start, end, 1);
    }

    /**
     * 获取子数组
     *
     * @param array 数组
     * @param start 开始位置（包括）
     * @param end   结束位置（不包括）
     * @param step  步进
     * @return 新的数组
     * @since 4.0.6
     */
    public static Object[] sub(Object array, int start, int end, int step) {
        int length = length(array);
        if (start < 0) {
            start += length;
        }
        if (end < 0) {
            end += length;
        }
        if (start == length) {
            return new Object[0];
        }
        if (start > end) {
            int tmp = start;
            start = end;
            end = tmp;
        }
        if (end > length) {
            if (start >= length) {
                return new Object[0];
            }
            end = length;
        }

        if (step <= 1) {
            step = 1;
        }

        final ArrayList<Object> list = new ArrayList<>();
        for (int i = start; i < end; i += step) {
            list.add(get(array, i));
        }

        return list.toArray();
    }

    /**
     * 数组或集合转String
     *
     * @param obj 集合或数组对象
     * @return 数组字符串，与集合转字符串格式相同
     */
    public static String toString(Object obj) {
        if (null == obj) {
            return null;
        }
        if (ArrayUtils.isArray(obj)) {
            try {
                return Arrays.deepToString((Object[]) obj);
            } catch (Exception e) {
                final String className = obj.getClass().getComponentType().getName();
                switch (className) {
                    case "long":
                        return Arrays.toString((long[]) obj);
                    case "int":
                        return Arrays.toString((int[]) obj);
                    case "short":
                        return Arrays.toString((short[]) obj);
                    case "char":
                        return Arrays.toString((char[]) obj);
                    case "byte":
                        return Arrays.toString((byte[]) obj);
                    case "boolean":
                        return Arrays.toString((boolean[]) obj);
                    case "float":
                        return Arrays.toString((float[]) obj);
                    case "double":
                        return Arrays.toString((double[]) obj);
                    default:
                        throw new RuntimeException(e);
                }
            }
        }
        return obj.toString();
    }

    /**
     * 获取数组长度<br>
     * 如果参数为{@code null}，返回0
     *
     * <pre>
     * ArrayUtils.length(null)            = 0
     * ArrayUtils.length([])              = 0
     * ArrayUtils.length([null])          = 1
     * ArrayUtils.length([true, false])   = 2
     * ArrayUtils.length([1, 2, 3])       = 3
     * ArrayUtils.length(["a", "b", "c"]) = 3
     * </pre>
     *
     * @param array 数组对象
     * @return 数组长度
     * @throws IllegalArgumentException 如果参数不为数组，抛出此异常
     * @see Array#getLength(Object)
     * @since 3.0.8
     */
    public static int length(Object array) throws IllegalArgumentException {
        if (null == array) {
            return 0;
        }
        return Array.getLength(array);
    }

    /**
     * 以 conjunction 为分隔符将数组转换为字符串
     *
     * @param array       数组
     * @param conjunction 分隔符
     * @return 连接后的字符串
     */
    public static String join(long[] array, CharSequence conjunction) {
        if (null == array) {
            return null;
        }

        final StringBuilder sb = new StringBuilder();
        boolean isFirst = true;
        for (long item : array) {
            if (isFirst) {
                isFirst = false;
            } else {
                sb.append(conjunction);
            }
            sb.append(item);
        }
        return sb.toString();
    }

    /**
     * 以 conjunction 为分隔符将数组转换为字符串
     *
     * @param array       数组
     * @param conjunction 分隔符
     * @return 连接后的字符串
     */
    public static String join(int[] array, CharSequence conjunction) {
        if (null == array) {
            return null;
        }

        final StringBuilder sb = new StringBuilder();
        boolean isFirst = true;
        for (int item : array) {
            if (isFirst) {
                isFirst = false;
            } else {
                sb.append(conjunction);
            }
            sb.append(item);
        }
        return sb.toString();
    }

    /**
     * 以 conjunction 为分隔符将数组转换为字符串
     *
     * @param array       数组
     * @param conjunction 分隔符
     * @return 连接后的字符串
     */
    public static String join(short[] array, CharSequence conjunction) {
        if (null == array) {
            return null;
        }

        final StringBuilder sb = new StringBuilder();
        boolean isFirst = true;
        for (short item : array) {
            if (isFirst) {
                isFirst = false;
            } else {
                sb.append(conjunction);
            }
            sb.append(item);
        }
        return sb.toString();
    }

    /**
     * 以 conjunction 为分隔符将数组转换为字符串
     *
     * @param array       数组
     * @param conjunction 分隔符
     * @return 连接后的字符串
     */
    public static String join(char[] array, CharSequence conjunction) {
        if (null == array) {
            return null;
        }

        final StringBuilder sb = new StringBuilder();
        boolean isFirst = true;
        for (char item : array) {
            if (isFirst) {
                isFirst = false;
            } else {
                sb.append(conjunction);
            }
            sb.append(item);
        }
        return sb.toString();
    }

    /**
     * 以 conjunction 为分隔符将数组转换为字符串
     *
     * @param array       数组
     * @param conjunction 分隔符
     * @return 连接后的字符串
     */
    public static String join(byte[] array, CharSequence conjunction) {
        if (null == array) {
            return null;
        }

        final StringBuilder sb = new StringBuilder();
        boolean isFirst = true;
        for (byte item : array) {
            if (isFirst) {
                isFirst = false;
            } else {
                sb.append(conjunction);
            }
            sb.append(item);
        }
        return sb.toString();
    }

    /**
     * 以 conjunction 为分隔符将数组转换为字符串
     *
     * @param array       数组
     * @param conjunction 分隔符
     * @return 连接后的字符串
     */
    public static String join(boolean[] array, CharSequence conjunction) {
        if (null == array) {
            return null;
        }

        final StringBuilder sb = new StringBuilder();
        boolean isFirst = true;
        for (boolean item : array) {
            if (isFirst) {
                isFirst = false;
            } else {
                sb.append(conjunction);
            }
            sb.append(item);
        }
        return sb.toString();
    }

    /**
     * 以 conjunction 为分隔符将数组转换为字符串
     *
     * @param array       数组
     * @param conjunction 分隔符
     * @return 连接后的字符串
     */
    public static String join(float[] array, CharSequence conjunction) {
        if (null == array) {
            return null;
        }

        final StringBuilder sb = new StringBuilder();
        boolean isFirst = true;
        for (float item : array) {
            if (isFirst) {
                isFirst = false;
            } else {
                sb.append(conjunction);
            }
            sb.append(item);
        }
        return sb.toString();
    }

    /**
     * 以 conjunction 为分隔符将数组转换为字符串
     *
     * @param array       数组
     * @param conjunction 分隔符
     * @return 连接后的字符串
     */
    public static String join(double[] array, CharSequence conjunction) {
        if (null == array) {
            return null;
        }

        final StringBuilder sb = new StringBuilder();
        boolean isFirst = true;
        for (double item : array) {
            if (isFirst) {
                isFirst = false;
            } else {
                sb.append(conjunction);
            }
            sb.append(item);
        }
        return sb.toString();
    }

    /**
     * 以 conjunction 为分隔符将数组转换为字符串
     *
     * @param array       数组
     * @param conjunction 分隔符
     * @return 连接后的字符串
     */
    public static String join(Object array, CharSequence conjunction) {
        if (isArray(array)) {
            final Class<?> componentType = array.getClass().getComponentType();
            if (componentType.isPrimitive()) {
                final String componentTypeName = componentType.getName();
                switch (componentTypeName) {
                    case "long":
                        return join((long[]) array, conjunction);
                    case "int":
                        return join((int[]) array, conjunction);
                    case "short":
                        return join((short[]) array, conjunction);
                    case "char":
                        return join((char[]) array, conjunction);
                    case "byte":
                        return join((byte[]) array, conjunction);
                    case "boolean":
                        return join((boolean[]) array, conjunction);
                    case "float":
                        return join((float[]) array, conjunction);
                    case "double":
                        return join((double[]) array, conjunction);
                    default:
                        throw new RuntimeException("Unknown primitive type:" + componentTypeName);
                }
            } else {
                Object[] objects = (Object[]) array;
                return Arrays.stream(objects).map(Object::toString).collect(Collectors.joining(conjunction));
            }
        }
        throw new RuntimeException(StringUtils.format("[{}] is not a Array!", array.getClass()));
    }

    /**
     * {@link ByteBuffer} 转byte数组
     *
     * @param bytebuffer {@link ByteBuffer}
     * @return byte数组
     * @since 3.0.1
     */
    public static byte[] toArray(ByteBuffer bytebuffer) {
        if (false == bytebuffer.hasArray()) {
            int oldPosition = bytebuffer.position();
            bytebuffer.position(0);
            int size = bytebuffer.limit();
            byte[] buffers = new byte[size];
            bytebuffer.get(buffers);
            bytebuffer.position(oldPosition);
            return buffers;
        } else {
            return Arrays.copyOfRange(bytebuffer.array(), bytebuffer.position(), bytebuffer.limit());
        }
    }

    /**
     * 将集合转为数组
     *
     * @param <T>           数组元素类型
     * @param collection    集合
     * @param componentType 集合元素类型
     * @return 数组
     * @since 3.0.9
     */
    public static <T> T[] toArray(Collection<T> collection, Class<T> componentType) {
        final T[] array = newArray(componentType, collection.size());
        return collection.toArray(array);
    }

    // ---------------------------------------------------------------------- remove

    /**
     * 移除数组中对应位置的元素<br>
     * copy from commons-lang
     *
     * @param <T>   数组元素类型
     * @param array 数组对象，可以是对象数组，也可以原始类型数组
     * @param index 位置，如果位置小于0或者大于长度，返回原数组
     * @return 去掉指定元素后的新数组或原数组
     * @throws IllegalArgumentException 参数对象不为数组对象
     * @since 3.0.8
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] remove(T[] array, int index) throws IllegalArgumentException {
        return (T[]) remove((Object) array, index);
    }

    /**
     * 移除数组中对应位置的元素<br>
     * copy from commons-lang
     *
     * @param array 数组对象，可以是对象数组，也可以原始类型数组
     * @param index 位置，如果位置小于0或者大于长度，返回原数组
     * @return 去掉指定元素后的新数组或原数组
     * @throws IllegalArgumentException 参数对象不为数组对象
     * @since 3.0.8
     */
    public static long[] remove(long[] array, int index) throws IllegalArgumentException {
        return (long[]) remove((Object) array, index);
    }

    /**
     * 移除数组中对应位置的元素<br>
     * copy from commons-lang
     *
     * @param array 数组对象，可以是对象数组，也可以原始类型数组
     * @param index 位置，如果位置小于0或者大于长度，返回原数组
     * @return 去掉指定元素后的新数组或原数组
     * @throws IllegalArgumentException 参数对象不为数组对象
     * @since 3.0.8
     */
    public static int[] remove(int[] array, int index) throws IllegalArgumentException {
        return (int[]) remove((Object) array, index);
    }

    /**
     * 移除数组中对应位置的元素<br>
     * copy from commons-lang
     *
     * @param array 数组对象，可以是对象数组，也可以原始类型数组
     * @param index 位置，如果位置小于0或者大于长度，返回原数组
     * @return 去掉指定元素后的新数组或原数组
     * @throws IllegalArgumentException 参数对象不为数组对象
     * @since 3.0.8
     */
    public static short[] remove(short[] array, int index) throws IllegalArgumentException {
        return (short[]) remove((Object) array, index);
    }

    /**
     * 移除数组中对应位置的元素<br>
     * copy from commons-lang
     *
     * @param array 数组对象，可以是对象数组，也可以原始类型数组
     * @param index 位置，如果位置小于0或者大于长度，返回原数组
     * @return 去掉指定元素后的新数组或原数组
     * @throws IllegalArgumentException 参数对象不为数组对象
     * @since 3.0.8
     */
    public static char[] remove(char[] array, int index) throws IllegalArgumentException {
        return (char[]) remove((Object) array, index);
    }

    /**
     * 移除数组中对应位置的元素<br>
     * copy from commons-lang
     *
     * @param array 数组对象，可以是对象数组，也可以原始类型数组
     * @param index 位置，如果位置小于0或者大于长度，返回原数组
     * @return 去掉指定元素后的新数组或原数组
     * @throws IllegalArgumentException 参数对象不为数组对象
     * @since 3.0.8
     */
    public static byte[] remove(byte[] array, int index) throws IllegalArgumentException {
        return (byte[]) remove((Object) array, index);
    }

    /**
     * 移除数组中对应位置的元素<br>
     * copy from commons-lang
     *
     * @param array 数组对象，可以是对象数组，也可以原始类型数组
     * @param index 位置，如果位置小于0或者大于长度，返回原数组
     * @return 去掉指定元素后的新数组或原数组
     * @throws IllegalArgumentException 参数对象不为数组对象
     * @since 3.0.8
     */
    public static double[] remove(double[] array, int index) throws IllegalArgumentException {
        return (double[]) remove((Object) array, index);
    }

    /**
     * 移除数组中对应位置的元素<br>
     * copy from commons-lang
     *
     * @param array 数组对象，可以是对象数组，也可以原始类型数组
     * @param index 位置，如果位置小于0或者大于长度，返回原数组
     * @return 去掉指定元素后的新数组或原数组
     * @throws IllegalArgumentException 参数对象不为数组对象
     * @since 3.0.8
     */
    public static float[] remove(float[] array, int index) throws IllegalArgumentException {
        return (float[]) remove((Object) array, index);
    }

    /**
     * 移除数组中对应位置的元素<br>
     * copy from commons-lang
     *
     * @param array 数组对象，可以是对象数组，也可以原始类型数组
     * @param index 位置，如果位置小于0或者大于长度，返回原数组
     * @return 去掉指定元素后的新数组或原数组
     * @throws IllegalArgumentException 参数对象不为数组对象
     * @since 3.0.8
     */
    public static boolean[] remove(boolean[] array, int index) throws IllegalArgumentException {
        return (boolean[]) remove((Object) array, index);
    }

    /**
     * 移除数组中对应位置的元素<br>
     * copy from commons-lang
     *
     * @param array 数组对象，可以是对象数组，也可以原始类型数组
     * @param index 位置，如果位置小于0或者大于长度，返回原数组
     * @return 去掉指定元素后的新数组或原数组
     * @throws IllegalArgumentException 参数对象不为数组对象
     * @since 3.0.8
     */
    public static Object remove(Object array, int index) throws IllegalArgumentException {
        if (null == array) {
            return array;
        }
        int length = length(array);
        if (index < 0 || index >= length) {
            return array;
        }

        final Object result = Array.newInstance(array.getClass().getComponentType(), length - 1);
        System.arraycopy(array, 0, result, 0, index);
        if (index < length - 1) {
            // 后半部分
            System.arraycopy(array, index + 1, result, index, length - index - 1);
        }

        return result;
    }

    // ---------------------------------------------------------------------- remove

    /**
     * 移除数组中指定的元素<br>
     * 只会移除匹配到的第一个元素 copy from commons-lang
     *
     * @param <T>     数组元素类型
     * @param array   数组对象，可以是对象数组，也可以原始类型数组
     * @param element 要移除的元素
     * @return 去掉指定元素后的新数组或原数组
     * @throws IllegalArgumentException 参数对象不为数组对象
     * @since 3.0.8
     */
    public static <T> T[] removeEle(T[] array, T element) throws IllegalArgumentException {
        return remove(array, indexOf(array, element));
    }

    /**
     * 移除数组中指定的元素<br>
     * 只会移除匹配到的第一个元素 copy from commons-lang
     *
     * @param array   数组对象，可以是对象数组，也可以原始类型数组
     * @param element 要移除的元素
     * @return 去掉指定元素后的新数组或原数组
     * @throws IllegalArgumentException 参数对象不为数组对象
     * @since 3.0.8
     */
    public static long[] removeEle(long[] array, long element) throws IllegalArgumentException {
        return remove(array, indexOf(array, element, 0));
    }

    /**
     * 移除数组中指定的元素<br>
     * 只会移除匹配到的第一个元素 copy from commons-lang
     *
     * @param array   数组对象，可以是对象数组，也可以原始类型数组
     * @param element 要移除的元素
     * @return 去掉指定元素后的新数组或原数组
     * @throws IllegalArgumentException 参数对象不为数组对象
     * @since 3.0.8
     */
    public static int[] removeEle(int[] array, int element) throws IllegalArgumentException {
        return remove(array, indexOf(array, element));
    }

    /**
     * 移除数组中指定的元素<br>
     * 只会移除匹配到的第一个元素 copy from commons-lang
     *
     * @param array   数组对象，可以是对象数组，也可以原始类型数组
     * @param element 要移除的元素
     * @return 去掉指定元素后的新数组或原数组
     * @throws IllegalArgumentException 参数对象不为数组对象
     * @since 3.0.8
     */
    public static short[] removeEle(short[] array, short element) throws IllegalArgumentException {
        return remove(array, indexOf(array, element));
    }

    /**
     * 移除数组中指定的元素<br>
     * 只会移除匹配到的第一个元素 copy from commons-lang
     *
     * @param array   数组对象，可以是对象数组，也可以原始类型数组
     * @param element 要移除的元素
     * @return 去掉指定元素后的新数组或原数组
     * @throws IllegalArgumentException 参数对象不为数组对象
     * @since 3.0.8
     */
    public static char[] removeEle(char[] array, char element) throws IllegalArgumentException {
        return remove(array, indexOf(array, element));
    }

    /**
     * 移除数组中指定的元素<br>
     * 只会移除匹配到的第一个元素 copy from commons-lang
     *
     * @param array   数组对象，可以是对象数组，也可以原始类型数组
     * @param element 要移除的元素
     * @return 去掉指定元素后的新数组或原数组
     * @throws IllegalArgumentException 参数对象不为数组对象
     * @since 3.0.8
     */
    public static byte[] removeEle(byte[] array, byte element) throws IllegalArgumentException {
        return remove(array, indexOf(array, element));
    }

    /**
     * 移除数组中指定的元素<br>
     * 只会移除匹配到的第一个元素 copy from commons-lang
     *
     * @param array   数组对象，可以是对象数组，也可以原始类型数组
     * @param element 要移除的元素
     * @return 去掉指定元素后的新数组或原数组
     * @throws IllegalArgumentException 参数对象不为数组对象
     * @since 3.0.8
     */
    public static double[] removeEle(double[] array, double element) throws IllegalArgumentException {
        return remove(array, indexOf(array, element));
    }

    /**
     * 移除数组中指定的元素<br>
     * 只会移除匹配到的第一个元素 copy from commons-lang
     *
     * @param array   数组对象，可以是对象数组，也可以原始类型数组
     * @param element 要移除的元素
     * @return 去掉指定元素后的新数组或原数组
     * @throws IllegalArgumentException 参数对象不为数组对象
     * @since 3.0.8
     */
    public static float[] removeEle(float[] array, float element) throws IllegalArgumentException {
        return remove(array, indexOf(array, element));
    }

    /**
     * 移除数组中指定的元素<br>
     * 只会移除匹配到的第一个元素 copy from commons-lang
     *
     * @param array   数组对象，可以是对象数组，也可以原始类型数组
     * @param element 要移除的元素
     * @return 去掉指定元素后的新数组或原数组
     * @throws IllegalArgumentException 参数对象不为数组对象
     * @since 3.0.8
     */
    public static boolean[] removeEle(boolean[] array, boolean element) throws IllegalArgumentException {
        return remove(array, indexOf(array, element));
    }

    // ------------------------------------------------------------------------------------------------------------
    // Reverse array

    /**
     * 反转数组，会变更原数组
     *
     * @param <T>                 数组元素类型
     * @param array               数组，会变更
     * @param startIndexInclusive 其实位置（包含）
     * @param endIndexExclusive   结束位置（不包含）
     * @return 变更后的原数组
     * @since 3.0.9
     */
    public static <T> T[] reverse(final T[] array, final int startIndexInclusive, final int endIndexExclusive) {
        if (isEmpty(array)) {
            return array;
        }
        int i = startIndexInclusive < 0 ? 0 : startIndexInclusive;
        int j = Math.min(array.length, endIndexExclusive) - 1;
        T tmp;
        while (j > i) {
            tmp = array[j];
            array[j] = array[i];
            array[i] = tmp;
            j--;
            i++;
        }
        return array;
    }

    /**
     * 反转数组，会变更原数组
     *
     * @param <T>   数组元素类型
     * @param array 数组，会变更
     * @return 变更后的原数组
     * @since 3.0.9
     */
    public static <T> T[] reverse(final T[] array) {
        return reverse(array, 0, array.length);
    }

    /**
     * 反转数组，会变更原数组
     *
     * @param array               数组，会变更
     * @param startIndexInclusive 其实位置（包含）
     * @param endIndexExclusive   结束位置（不包含）
     * @return 变更后的原数组
     * @since 3.0.9
     */
    public static long[] reverse(final long[] array, final int startIndexInclusive, final int endIndexExclusive) {
        if (isEmpty(array)) {
            return array;
        }
        int i = startIndexInclusive < 0 ? 0 : startIndexInclusive;
        int j = Math.min(array.length, endIndexExclusive) - 1;
        long tmp;
        while (j > i) {
            tmp = array[j];
            array[j] = array[i];
            array[i] = tmp;
            j--;
            i++;
        }
        return array;
    }

    /**
     * 反转数组，会变更原数组
     *
     * @param array 数组，会变更
     * @return 变更后的原数组
     * @since 3.0.9
     */
    public static long[] reverse(final long[] array) {
        return reverse(array, 0, array.length);
    }

    /**
     * 反转数组，会变更原数组
     *
     * @param array               数组，会变更
     * @param startIndexInclusive 其实位置（包含）
     * @param endIndexExclusive   结束位置（不包含）
     * @return 变更后的原数组
     * @since 3.0.9
     */
    public static int[] reverse(final int[] array, final int startIndexInclusive, final int endIndexExclusive) {
        if (isEmpty(array)) {
            return array;
        }
        int i = startIndexInclusive < 0 ? 0 : startIndexInclusive;
        int j = Math.min(array.length, endIndexExclusive) - 1;
        int tmp;
        while (j > i) {
            tmp = array[j];
            array[j] = array[i];
            array[i] = tmp;
            j--;
            i++;
        }
        return array;
    }

    /**
     * 反转数组，会变更原数组
     *
     * @param array 数组，会变更
     * @return 变更后的原数组
     * @since 3.0.9
     */
    public static int[] reverse(final int[] array) {
        return reverse(array, 0, array.length);
    }

    /**
     * 反转数组，会变更原数组
     *
     * @param array               数组，会变更
     * @param startIndexInclusive 其实位置（包含）
     * @param endIndexExclusive   结束位置（不包含）
     * @return 变更后的原数组
     * @since 3.0.9
     */
    public static short[] reverse(final short[] array, final int startIndexInclusive, final int endIndexExclusive) {
        if (isEmpty(array)) {
            return array;
        }
        int i = startIndexInclusive < 0 ? 0 : startIndexInclusive;
        int j = Math.min(array.length, endIndexExclusive) - 1;
        short tmp;
        while (j > i) {
            tmp = array[j];
            array[j] = array[i];
            array[i] = tmp;
            j--;
            i++;
        }
        return array;
    }

    /**
     * 反转数组，会变更原数组
     *
     * @param array 数组，会变更
     * @return 变更后的原数组
     * @since 3.0.9
     */
    public static short[] reverse(final short[] array) {
        return reverse(array, 0, array.length);
    }

    /**
     * 反转数组，会变更原数组
     *
     * @param array               数组，会变更
     * @param startIndexInclusive 其实位置（包含）
     * @param endIndexExclusive   结束位置（不包含）
     * @return 变更后的原数组
     * @since 3.0.9
     */
    public static char[] reverse(final char[] array, final int startIndexInclusive, final int endIndexExclusive) {
        if (isEmpty(array)) {
            return array;
        }
        int i = startIndexInclusive < 0 ? 0 : startIndexInclusive;
        int j = Math.min(array.length, endIndexExclusive) - 1;
        char tmp;
        while (j > i) {
            tmp = array[j];
            array[j] = array[i];
            array[i] = tmp;
            j--;
            i++;
        }
        return array;
    }

    /**
     * 反转数组，会变更原数组
     *
     * @param array 数组，会变更
     * @return 变更后的原数组
     * @since 3.0.9
     */
    public static char[] reverse(final char[] array) {
        return reverse(array, 0, array.length);
    }

    /**
     * 反转数组，会变更原数组
     *
     * @param array               数组，会变更
     * @param startIndexInclusive 其实位置（包含）
     * @param endIndexExclusive   结束位置（不包含）
     * @return 变更后的原数组
     * @since 3.0.9
     */
    public static byte[] reverse(final byte[] array, final int startIndexInclusive, final int endIndexExclusive) {
        if (isEmpty(array)) {
            return array;
        }
        int i = startIndexInclusive < 0 ? 0 : startIndexInclusive;
        int j = Math.min(array.length, endIndexExclusive) - 1;
        byte tmp;
        while (j > i) {
            tmp = array[j];
            array[j] = array[i];
            array[i] = tmp;
            j--;
            i++;
        }
        return array;
    }

    /**
     * 反转数组，会变更原数组
     *
     * @param array 数组，会变更
     * @return 变更后的原数组
     * @since 3.0.9
     */
    public static byte[] reverse(final byte[] array) {
        return reverse(array, 0, array.length);
    }

    /**
     * 反转数组，会变更原数组
     *
     * @param array               数组，会变更
     * @param startIndexInclusive 其实位置（包含）
     * @param endIndexExclusive   结束位置（不包含）
     * @return 变更后的原数组
     * @since 3.0.9
     */
    public static double[] reverse(final double[] array, final int startIndexInclusive, final int endIndexExclusive) {
        if (isEmpty(array)) {
            return array;
        }
        int i = startIndexInclusive < 0 ? 0 : startIndexInclusive;
        int j = Math.min(array.length, endIndexExclusive) - 1;
        double tmp;
        while (j > i) {
            tmp = array[j];
            array[j] = array[i];
            array[i] = tmp;
            j--;
            i++;
        }
        return array;
    }

    /**
     * 反转数组，会变更原数组
     *
     * @param array 数组，会变更
     * @return 变更后的原数组
     * @since 3.0.9
     */
    public static double[] reverse(final double[] array) {
        return reverse(array, 0, array.length);
    }

    /**
     * 反转数组，会变更原数组
     *
     * @param array               数组，会变更
     * @param startIndexInclusive 其实位置（包含）
     * @param endIndexExclusive   结束位置（不包含）
     * @return 变更后的原数组
     * @since 3.0.9
     */
    public static float[] reverse(final float[] array, final int startIndexInclusive, final int endIndexExclusive) {
        if (isEmpty(array)) {
            return array;
        }
        int i = startIndexInclusive < 0 ? 0 : startIndexInclusive;
        int j = Math.min(array.length, endIndexExclusive) - 1;
        float tmp;
        while (j > i) {
            tmp = array[j];
            array[j] = array[i];
            array[i] = tmp;
            j--;
            i++;
        }
        return array;
    }

    /**
     * 反转数组，会变更原数组
     *
     * @param array 数组，会变更
     * @return 变更后的原数组
     * @since 3.0.9
     */
    public static float[] reverse(final float[] array) {
        return reverse(array, 0, array.length);
    }

    /**
     * 反转数组，会变更原数组
     *
     * @param array               数组，会变更
     * @param startIndexInclusive 其实位置（包含）
     * @param endIndexExclusive   结束位置（不包含）
     * @return 变更后的原数组
     * @since 3.0.9
     */
    public static boolean[] reverse(final boolean[] array, final int startIndexInclusive, final int endIndexExclusive) {
        if (isEmpty(array)) {
            return array;
        }
        int i = startIndexInclusive < 0 ? 0 : startIndexInclusive;
        int j = Math.min(array.length, endIndexExclusive) - 1;
        boolean tmp;
        while (j > i) {
            tmp = array[j];
            array[j] = array[i];
            array[i] = tmp;
            j--;
            i++;
        }
        return array;
    }

    /**
     * 反转数组，会变更原数组
     *
     * @param array 数组，会变更
     * @return 变更后的原数组
     * @since 3.0.9
     */
    public static boolean[] reverse(final boolean[] array) {
        return reverse(array, 0, array.length);
    }

    /**
     * 取最小值
     *
     * @param numberArray 数字数组
     * @return 最小值
     * @since 3.0.9
     */
    public static long min(long... numberArray) {
        if (isEmpty(numberArray)) {
            throw new IllegalArgumentException("Number array must not empty !");
        }
        long min = numberArray[0];
        for (int i = 0; i < numberArray.length; i++) {
            if (min > numberArray[i]) {
                min = numberArray[i];
            }
        }
        return min;
    }

    /**
     * 取最小值
     *
     * @param numberArray 数字数组
     * @return 最小值
     * @since 3.0.9
     */
    public static int min(int... numberArray) {
        if (isEmpty(numberArray)) {
            throw new IllegalArgumentException("Number array must not empty !");
        }
        int min = numberArray[0];
        for (int i = 0; i < numberArray.length; i++) {
            if (min > numberArray[i]) {
                min = numberArray[i];
            }
        }
        return min;
    }

    /**
     * 取最小值
     *
     * @param numberArray 数字数组
     * @return 最小值
     * @since 3.0.9
     */
    public static short min(short... numberArray) {
        if (isEmpty(numberArray)) {
            throw new IllegalArgumentException("Number array must not empty !");
        }
        short min = numberArray[0];
        for (int i = 0; i < numberArray.length; i++) {
            if (min > numberArray[i]) {
                min = numberArray[i];
            }
        }
        return min;
    }

    /**
     * 取最小值
     *
     * @param numberArray 数字数组
     * @return 最小值
     * @since 3.0.9
     */
    public static char min(char... numberArray) {
        if (isEmpty(numberArray)) {
            throw new IllegalArgumentException("Number array must not empty !");
        }
        char min = numberArray[0];
        for (int i = 0; i < numberArray.length; i++) {
            if (min > numberArray[i]) {
                min = numberArray[i];
            }
        }
        return min;
    }

    /**
     * 取最小值
     *
     * @param numberArray 数字数组
     * @return 最小值
     * @since 3.0.9
     */
    public static byte min(byte... numberArray) {
        if (isEmpty(numberArray)) {
            throw new IllegalArgumentException("Number array must not empty !");
        }
        byte min = numberArray[0];
        for (int i = 0; i < numberArray.length; i++) {
            if (min > numberArray[i]) {
                min = numberArray[i];
            }
        }
        return min;
    }

    /**
     * 取最小值
     *
     * @param numberArray 数字数组
     * @return 最小值
     * @since 3.0.9
     */
    public static double min(double... numberArray) {
        if (isEmpty(numberArray)) {
            throw new IllegalArgumentException("Number array must not empty !");
        }
        double min = numberArray[0];
        for (int i = 0; i < numberArray.length; i++) {
            if (min > numberArray[i]) {
                min = numberArray[i];
            }
        }
        return min;
    }

    /**
     * 取最小值
     *
     * @param numberArray 数字数组
     * @return 最小值
     * @since 3.0.9
     */
    public static float min(float... numberArray) {
        if (isEmpty(numberArray)) {
            throw new IllegalArgumentException("Number array must not empty !");
        }
        float min = numberArray[0];
        for (int i = 0; i < numberArray.length; i++) {
            if (min > numberArray[i]) {
                min = numberArray[i];
            }
        }
        return min;
    }

    /**
     * 取最大值
     *
     * @param numberArray 数字数组
     * @return 最大值
     * @since 3.0.9
     */
    public static long max(long... numberArray) {
        if (isEmpty(numberArray)) {
            throw new IllegalArgumentException("Number array must not empty !");
        }
        long max = numberArray[0];
        for (int i = 0; i < numberArray.length; i++) {
            if (max < numberArray[i]) {
                max = numberArray[i];
            }
        }
        return max;
    }

    /**
     * 取最大值
     *
     * @param numberArray 数字数组
     * @return 最大值
     * @since 3.0.9
     */
    public static int max(int... numberArray) {
        if (isEmpty(numberArray)) {
            throw new IllegalArgumentException("Number array must not empty !");
        }
        int max = numberArray[0];
        for (int i = 0; i < numberArray.length; i++) {
            if (max < numberArray[i]) {
                max = numberArray[i];
            }
        }
        return max;
    }

    /**
     * 取最大值
     *
     * @param numberArray 数字数组
     * @return 最大值
     * @since 3.0.9
     */
    public static short max(short... numberArray) {
        if (isEmpty(numberArray)) {
            throw new IllegalArgumentException("Number array must not empty !");
        }
        short max = numberArray[0];
        for (int i = 0; i < numberArray.length; i++) {
            if (max < numberArray[i]) {
                max = numberArray[i];
            }
        }
        return max;
    }

    /**
     * 取最大值
     *
     * @param numberArray 数字数组
     * @return 最大值
     * @since 3.0.9
     */
    public static char max(char... numberArray) {
        if (isEmpty(numberArray)) {
            throw new IllegalArgumentException("Number array must not empty !");
        }
        char max = numberArray[0];
        for (int i = 0; i < numberArray.length; i++) {
            if (max < numberArray[i]) {
                max = numberArray[i];
            }
        }
        return max;
    }

    /**
     * 取最大值
     *
     * @param numberArray 数字数组
     * @return 最大值
     * @since 3.0.9
     */
    public static byte max(byte... numberArray) {
        if (isEmpty(numberArray)) {
            throw new IllegalArgumentException("Number array must not empty !");
        }
        byte max = numberArray[0];
        for (int i = 0; i < numberArray.length; i++) {
            if (max < numberArray[i]) {
                max = numberArray[i];
            }
        }
        return max;
    }

    /**
     * 取最大值
     *
     * @param numberArray 数字数组
     * @return 最大值
     * @since 3.0.9
     */
    public static double max(double... numberArray) {
        if (isEmpty(numberArray)) {
            throw new IllegalArgumentException("Number array must not empty !");
        }
        double max = numberArray[0];
        for (int i = 0; i < numberArray.length; i++) {
            if (max < numberArray[i]) {
                max = numberArray[i];
            }
        }
        return max;
    }

    /**
     * 取最大值
     *
     * @param numberArray 数字数组
     * @return 最大值
     * @since 3.0.9
     */
    public static float max(float... numberArray) {
        if (isEmpty(numberArray)) {
            throw new IllegalArgumentException("Number array must not empty !");
        }
        float max = numberArray[0];
        for (int i = 0; i < numberArray.length; i++) {
            if (max < numberArray[i]) {
                max = numberArray[i];
            }
        }
        return max;
    }

    /**
     * 交换数组中两个位置的值
     *
     * @param array  数组
     * @param index1 位置1
     * @param index2 位置2
     * @return 交换后的数组，与传入数组为同一对象
     * @since 4.0.7
     */
    public static int[] swap(int[] array, int index1, int index2) {
        if (isEmpty(array)) {
            throw new IllegalArgumentException("Number array must not empty !");
        }
        int tmp = array[index1];
        array[index1] = array[index2];
        array[index2] = tmp;
        return array;
    }

    /**
     * 交换数组中两个位置的值
     *
     * @param array  数组
     * @param index1 位置1
     * @param index2 位置2
     * @return 交换后的数组，与传入数组为同一对象
     * @since 4.0.7
     */
    public static long[] swap(long[] array, int index1, int index2) {
        if (isEmpty(array)) {
            throw new IllegalArgumentException("Number array must not empty !");
        }
        long tmp = array[index1];
        array[index1] = array[index2];
        array[index2] = tmp;
        return array;
    }

    /**
     * 交换数组中两个位置的值
     *
     * @param array  数组
     * @param index1 位置1
     * @param index2 位置2
     * @return 交换后的数组，与传入数组为同一对象
     * @since 4.0.7
     */
    public static double[] swap(double[] array, int index1, int index2) {
        if (isEmpty(array)) {
            throw new IllegalArgumentException("Number array must not empty !");
        }
        double tmp = array[index1];
        array[index1] = array[index2];
        array[index2] = tmp;
        return array;
    }

    /**
     * 交换数组中两个位置的值
     *
     * @param array  数组
     * @param index1 位置1
     * @param index2 位置2
     * @return 交换后的数组，与传入数组为同一对象
     * @since 4.0.7
     */
    public static float[] swap(float[] array, int index1, int index2) {
        if (isEmpty(array)) {
            throw new IllegalArgumentException("Number array must not empty !");
        }
        float tmp = array[index1];
        array[index1] = array[index2];
        array[index2] = tmp;
        return array;
    }

    /**
     * 交换数组中两个位置的值
     *
     * @param array  数组
     * @param index1 位置1
     * @param index2 位置2
     * @return 交换后的数组，与传入数组为同一对象
     * @since 4.0.7
     */
    public static boolean[] swap(boolean[] array, int index1, int index2) {
        if (isEmpty(array)) {
            throw new IllegalArgumentException("Number array must not empty !");
        }
        boolean tmp = array[index1];
        array[index1] = array[index2];
        array[index2] = tmp;
        return array;
    }

    /**
     * 交换数组中两个位置的值
     *
     * @param array  数组
     * @param index1 位置1
     * @param index2 位置2
     * @return 交换后的数组，与传入数组为同一对象
     * @since 4.0.7
     */
    public static byte[] swap(byte[] array, int index1, int index2) {
        if (isEmpty(array)) {
            throw new IllegalArgumentException("Number array must not empty !");
        }
        byte tmp = array[index1];
        array[index1] = array[index2];
        array[index2] = tmp;
        return array;
    }

    /**
     * 交换数组中两个位置的值
     *
     * @param array  数组
     * @param index1 位置1
     * @param index2 位置2
     * @return 交换后的数组，与传入数组为同一对象
     * @since 4.0.7
     */
    public static char[] swap(char[] array, int index1, int index2) {
        if (isEmpty(array)) {
            throw new IllegalArgumentException("Number array must not empty !");
        }
        char tmp = array[index1];
        array[index1] = array[index2];
        array[index2] = tmp;
        return array;
    }

    /**
     * 交换数组中两个位置的值
     *
     * @param array  数组
     * @param index1 位置1
     * @param index2 位置2
     * @return 交换后的数组，与传入数组为同一对象
     * @since 4.0.7
     */
    public static short[] swap(short[] array, int index1, int index2) {
        if (isEmpty(array)) {
            throw new IllegalArgumentException("Number array must not empty !");
        }
        short tmp = array[index1];
        array[index1] = array[index2];
        array[index2] = tmp;
        return array;
    }

    /**
     * 交换数组中两个位置的值
     *
     * @param <T>    元素类型
     * @param array  数组
     * @param index1 位置1
     * @param index2 位置2
     * @return 交换后的数组，与传入数组为同一对象
     * @since 4.0.7
     */
    public static <T> T[] swap(T[] array, int index1, int index2) {
        if (isEmpty(array)) {
            throw new IllegalArgumentException("Array must not empty !");
        }
        T tmp = array[index1];
        array[index1] = array[index2];
        array[index2] = tmp;
        return array;
    }

    /**
     * 交换数组中两个位置的值
     *
     * @param array  数组对象
     * @param index1 位置1
     * @param index2 位置2
     * @return 交换后的数组，与传入数组为同一对象
     * @since 4.0.7
     */
    public static Object swap(Object array, int index1, int index2) {
        if (isEmpty(array)) {
            throw new IllegalArgumentException("Array must not empty !");
        }
        Object tmp = get(array, index1);
        Array.set(array, index1, Array.get(array, index2));
        Array.set(array, index2, tmp);
        return array;
    }

    /**
     * 过滤<br>
     * 过滤过程通过传入的Filter实现来过滤返回需要的元素内容，这个Filter实现可以实现以下功能：
     *
     * <pre>
     * 1、过滤出需要的对象，{@link Predicate#test(Object)}方法返回true的对象将被加入结果集合中
     * </pre>
     *
     * @param <T>    数组元素类型
     * @param array  数组
     * @param filter 过滤器接口，用于定义过滤规则，{@code null}返回原集合
     * @return 过滤后的数组
     * @since 3.2.1
     */
    public static <T> T[] filter(T[] array, Predicate<T> filter) {
        if (null == array || null == filter) {
            return array;
        }
        return edit(array, t -> filter.test(t) ? t : null);
    }

    /**
     * 编辑数组<br>
     * 编辑过程通过传入的Editor实现来返回需要的元素内容，这个Editor实现可以实现以下功能：
     *
     * <pre>
     * 1、过滤出需要的对象，如果返回{@code
     * null
     * }表示这个元素对象抛弃
     * 2、修改元素对象，返回集合中为修改后的对象
     * </pre>
     * <p>
     *
     * @param <T>    数组元素类型
     * @param array  数组
     * @param editor 编辑器接口，{@code null}返回原集合
     * @return 编辑后的数组
     * @since 5.3.3
     */
    public static <T> T[] edit(T[] array, Function<T, T> editor) {
        if (null == editor) {
            return array;
        }

        final ArrayList<T> list = new ArrayList<>(array.length);
        T modified;
        for (T t : array) {
            modified = editor.apply(t);
            if (null != modified) {
                list.add(modified);
            }
        }
        final T[] result = newArray(array.getClass().getComponentType(), list.size());
        return list.toArray(result);
    }


    /**
     * Removes the elements at the specified positions from the specified array.
     * All remaining elements are shifted to the left.
     * <p>
     * This method returns a new array with the same elements of the input
     * array except those at the specified positions. The component
     * type of the returned array is always the same as that of the input
     * array.
     * </p>
     * <p>
     * If the input array is {@code null}, an IndexOutOfBoundsException
     * will be thrown, because in that case no valid index can be specified.
     * </p>
     * <pre>
     * ArrayUtils.removeAll(["a", "b", "c"], 0, 2) = ["b"]
     * ArrayUtils.removeAll(["a", "b", "c"], 1, 2) = ["a"]
     * </pre>
     *
     * @param <T>     the component type of the array
     * @param array   the array to remove the element from, may not be {@code null}
     * @param indices the positions of the elements to be removed
     * @return A new array containing the existing elements except those
     * at the specified positions.
     * @throws IndexOutOfBoundsException if any index is out of range
     *                                   (index &lt; 0 || index &gt;= array.length), or if the array is {@code null}.
     * @since 3.0.1
     */
    @SuppressWarnings("unchecked") // removeAll() always creates an array of the same type as its input
    public static <T> T[] removeAll(final T[] array, final int... indices) {
        return (T[]) removeAll((Object) array, indices);
    }

    /**
     * Returns the length of the specified array.
     * This method can deal with {@link Object} arrays and with primitive arrays.
     * <p>
     * If the input array is {@code null}, {@code 0} is returned.
     * </p>
     * <pre>
     * ArrayUtils.getLength(null)            = 0
     * ArrayUtils.getLength([])              = 0
     * ArrayUtils.getLength([null])          = 1
     * ArrayUtils.getLength([true, false])   = 2
     * ArrayUtils.getLength([1, 2, 3])       = 3
     * ArrayUtils.getLength(["a", "b", "c"]) = 3
     * </pre>
     *
     * @param array the array to retrieve the length from, may be null
     * @return The length of the array, or {@code 0} if the array is {@code null}
     * @throws IllegalArgumentException if the object argument is not an array.
     * @since 2.1
     */
    public static int getLength(final Object array) {
        return array != null ? Array.getLength(array) : 0;
    }

    /**
     * Removes multiple array elements specified by index.
     *
     * @param array   source
     * @param indices to remove
     * @return new array of same type minus elements specified by unique values of {@code indices}
     * @since 3.0.1
     */
    // package protected for access by unit tests
    static Object removeAll(final Object array, final int... indices) {
        final int length = getLength(array);
        int diff = 0; // number of distinct indexes, i.e. number of entries that will be removed
        final int[] clonedIndices = Arrays.stream(clone(indices)).sorted().toArray();

        // identify length of result array
        if (isNotEmpty(clonedIndices)) {
            int i = clonedIndices.length;
            int prevIndex = length;
            while (--i >= 0) {
                final int index = clonedIndices[i];
                if (index < 0 || index >= length) {
                    throw new IndexOutOfBoundsException("Index: " + index + ", Length: " + length);
                }
                if (index >= prevIndex) {
                    continue;
                }
                diff++;
                prevIndex = index;
            }
        }

        // create result array
        final Object result = Array.newInstance(array.getClass().getComponentType(), length - diff);
        if (diff < length) {
            int end = length; // index just after last copy
            int dest = length - diff; // number of entries so far not copied
            for (int i = clonedIndices.length - 1; i >= 0; i--) {
                final int index = clonedIndices[i];
                if (end - index > 1) { // same as (cp > 0)
                    final int cp = end - index - 1;
                    dest -= cp;
                    System.arraycopy(array, index + 1, result, dest, cp);
                    // After this copy, we still have room for dest items.
                }
                end = index;
            }
            if (end > 0) {
                System.arraycopy(array, 0, result, 0, end);
            }
        }
        return result;
    }
}
