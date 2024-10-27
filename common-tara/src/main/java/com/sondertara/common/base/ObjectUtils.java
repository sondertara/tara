package com.sondertara.common.base;

import com.sondertara.common.collection.ArrayUtils;
import com.sondertara.common.collection.CollectionUtils;
import com.sondertara.common.function.Functions;
import com.sondertara.common.hash.Hashes;
import com.sondertara.common.math.NumberUtils;
import com.sondertara.common.reflect.ClassUtils;
import com.sondertara.common.reflect.Methods;
import com.sondertara.common.text.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * 对象工具类，包括判空、克隆、序列化等操作
 *
 * @author huangxiaohu
 */
public class ObjectUtils {

    /**
     * Checks if any value in the given array is {@code null}.
     *
     * <p>
     * If any of the values are {@code null} or the array is {@code null},
     * then {@code true} is returned, otherwise {@code false} is returned.
     * </p>
     *
     * <pre>
     * ObjectUtils.anyNull(*)             = false
     * ObjectUtils.anyNull(*, *)          = false
     * ObjectUtils.anyNull(null)          = true
     * ObjectUtils.anyNull(null, null)    = true
     * ObjectUtils.anyNull(null, *)       = true
     * ObjectUtils.anyNull(*, null)       = true
     * ObjectUtils.anyNull(*, *, null, *) = true
     * </pre>
     *
     * @param values the values to test, may be {@code null} or empty
     * @return {@code true} if there is at least one {@code null} value in the array,
     * {@code false} if all the values are non-null.
     * If the array is {@code null} or empty, {@code true} is also returned.
     * @since 3.11
     */
    public static boolean anyNull(final Object... values) {
        return !allNotNull(values);
    }

    public static boolean allNotNull(final Object... values) {
        return values != null && Stream.of(values).noneMatch(Objects::isNull);
    }

    private static final char AT_SIGN = '@';

    /**
     * 判断对象是否为空
     *
     * @param o 对象
     * @return true空
     */
    public static boolean isEmpty(Object o) {
        if (o == null) {
            return true;
        }
        if (o.getClass().isArray()) {
            return Array.getLength(o) == 0;
        } else if (o instanceof Collection) {
            return CollectionUtils.isEmpty((Collection<?>) o);
        } else if (o instanceof Map) {
            return CollectionUtils.isEmpty((Map<?, ?>) o);
        } else if (o instanceof String) {
            return StringUtils.isBlank((String) o);
        }
        return false;
    }


    /**
     * 比较两个对象是否相等，
     * 相同的条件有两个，满足其一即可：<br>
     * <ol>
     * <li>obj1 == null &amp;&amp; obj2 == null</li>
     * <li>obj1.equals(obj2)</li>
     * <li>如果是BigDecimal比较，0 == obj1.compareTo(obj2)</li>
     * </ol>
     *
     * @param o1 对象1
     * @param o2 对象2
     * @return 是否相等
     */
    public static boolean equals(Object o1, Object o2) {
        if (o1 == o2) {
            return true;
        }
        if (o1 == null || o2 == null) {
            return false;
        }
        if (o1.equals(o2)) {
            return true;
        }
        if (o1 instanceof BigDecimal && o2 instanceof BigDecimal) {
            return NumberUtils.equals((BigDecimal) o1, (BigDecimal) o2);
        }
        if (o1.getClass().isArray() && o2.getClass().isArray()) {
            return ArrayUtils.equals(o1, o2);
        }
        return Objects.equals(o1, o2);
    }

    /**
     * 比较两个对象是否不相等。<br>
     *
     * @param obj1 对象1
     * @param obj2 对象2
     * @return 是否不等
     */
    public static boolean notEqual(Object obj1, Object obj2) {
        return !equals(obj1, obj2);
    }

    /**
     * 计算对象长度，如果是字符串调用其length函数，集合类调用其size函数，数组调用其length属性，其他可遍历对象遍历计算长度<br>
     * 支持的类型包括：
     * <ul>
     * <li>CharSequence</li>
     * <li>Map</li>
     * <li>Iterator</li>
     * <li>Enumeration</li>
     * <li>Array</li>
     * </ul>
     *
     * @param obj 被计算长度的对象
     * @return 长度
     */
    public static int length(Object obj) {
        if (obj == null) {
            return 0;
        }
        if (obj instanceof CharSequence) {
            return ((CharSequence) obj).length();
        }
        if (obj instanceof Collection) {
            return ((Collection<?>) obj).size();
        }
        if (obj instanceof Map) {
            return ((Map<?, ?>) obj).size();
        }

        int count;
        if (obj instanceof Iterator) {
            final Iterator<?> iter = (Iterator<?>) obj;
            count = 0;
            while (iter.hasNext()) {
                count++;
                iter.next();
            }
            return count;
        }
        if (obj instanceof Enumeration) {
            final Enumeration<?> enumeration = (Enumeration<?>) obj;
            count = 0;
            while (enumeration.hasMoreElements()) {
                count++;
                enumeration.nextElement();
            }
            return count;
        }
        if (obj.getClass().isArray()) {
            return Array.getLength(obj);
        }
        return -1;
    }

    /**
     * 对象中是否包含元素<br>
     * 支持的对象类型包括：
     * <ul>
     * <li>String</li>
     * <li>Collection</li>
     * <li>Map</li>
     * <li>Iterator</li>
     * <li>Enumeration</li>
     * <li>Array</li>
     * </ul>
     *
     * @param obj     对象
     * @param element 元素
     * @return 是否包含
     */
    public static boolean contains(Object obj, Object element) {
        if (obj == null) {
            return false;
        }
        if (obj instanceof String) {
            if (element == null) {
                return false;
            }
            return ((String) obj).contains(element.toString());
        }
        if (obj instanceof Collection) {
            return ((Collection<?>) obj).contains(element);
        }
        if (obj instanceof Map) {
            return ((Map<?, ?>) obj).containsValue(element);
        }

        if (obj instanceof Iterator) {
            final Iterator<?> iter = (Iterator<?>) obj;
            while (iter.hasNext()) {
                final Object o = iter.next();
                if (equals(o, element)) {
                    return true;
                }
            }
            return false;
        }
        if (obj instanceof Enumeration) {
            final Enumeration<?> enumeration = (Enumeration<?>) obj;
            while (enumeration.hasMoreElements()) {
                final Object o = enumeration.nextElement();
                if (equals(o, element)) {
                    return true;
                }
            }
            return false;
        }
        if (obj.getClass().isArray()) {
            final int len = Array.getLength(obj);
            for (int i = 0; i < len; i++) {
                final Object o = Array.get(obj, i);
                if (equals(o, element)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 检查对象是否为null<br>
     * 判断标准为：
     *
     * <pre>
     * 1. == null
     * 2. equals(null)
     * </pre>
     *
     * @param obj 对象
     * @return 是否为null
     */
    public static boolean isNull(Object obj) {
        // noinspection ConstantConditions
        return null == obj || obj.equals(null);
    }

    /**
     * 检查对象是否不为null
     *
     * <pre>
     * 1. != null
     * 2. not equals(null)
     * </pre>
     *
     * @param obj 对象
     * @return 是否为非null
     */
    public static boolean isNotNull(Object obj) {
        // noinspection ConstantConditions
        return null != obj && !obj.equals(null);
    }

    /**
     * 如果给定对象为{@code null}返回默认值
     *
     * <pre>
     * ObjectUtil.defaultIfNull(null, null)      = null
     * ObjectUtil.defaultIfNull(null, "")        = ""
     * ObjectUtil.defaultIfNull(null, "zz")      = "zz"
     * ObjectUtil.defaultIfNull("abc", *)        = "abc"
     * ObjectUtil.defaultIfNull(Boolean.TRUE, *) = Boolean.TRUE
     * </pre>
     *
     * @param <T>          对象类型
     * @param object       被检查对象，可能为{@code null}
     * @param defaultValue 被检查对象为{@code null}返回的默认值，可以为{@code null}
     * @return 被检查对象为{@code null}返回默认值，否则返回原值
     */
    public static <T> T defaultIfNull(final T object, final T defaultValue) {
        return isNull(object) ? defaultValue : object;
    }

    /**
     * 如果被检查对象为 {@code null}， 返回默认值（由 defaultValueSupplier 提供）；否则直接返回
     *
     * @param source               被检查对象
     * @param defaultValueSupplier 默认值提供者
     * @param <T>                  对象类型
     * @return 被检查对象为{@code null}返回默认值，否则返回自定义handle处理后的返回值
     * @throws NullPointerException {@code defaultValueSupplier == null} 时，抛出
     */
    public static <T> T defaultIfNull(T source, Supplier<? extends T> defaultValueSupplier) {
        if (isNull(source)) {
            return defaultValueSupplier.get();
        }
        return source;
    }




    /**
     * 如果给定对象为{@code null}或者""或者空白符返回默认值
     *
     * <pre>
     * ObjectUtil.defaultIfBlank(null, null)      = null
     * ObjectUtil.defaultIfBlank(null, "")        = ""
     * ObjectUtil.defaultIfBlank("", "zz")      = "zz"
     * ObjectUtil.defaultIfBlank(" ", "zz")      = "zz"
     * ObjectUtil.defaultIfBlank("abc", *)        = "abc"
     * </pre>
     *
     * @param <T>          对象类型（必须实现CharSequence接口）
     * @param str          被检查对象，可能为{@code null}
     * @param defaultValue 被检查对象为{@code null}或者 ""或者空白符返回的默认值，可以为{@code null}或者
     *                     ""或者空白符
     * @return 被检查对象为{@code null}或者 ""或者空白符返回默认值，否则返回原值
     */
    public static <T extends CharSequence> T defaultIfBlank(final T str, final T defaultValue) {
        return StringUtils.isBlank(str) ? defaultValue : str;
    }

    /**
     * 如果被检查对象为 {@code null} 或 "" 或 空白字符串时，返回默认值（由 defaultValueSupplier 提供）；否则直接返回
     *
     * @param str                  被检查对象
     * @param defaultValueSupplier 默认值提供者
     * @param <T>                  对象类型（必须实现CharSequence接口）
     * @return 被检查对象为{@code null}返回默认值，否则返回自定义handle处理后的返回值
     * @throws NullPointerException {@code defaultValueSupplier == null} 时，抛出
     */
    public static <T extends CharSequence> T defaultIfBlank(T str, Supplier<? extends T> defaultValueSupplier) {
        if (StringUtils.isBlank(str)) {
            return defaultValueSupplier.get();
        }
        return str;
    }

    /**
     * 是否为基本类型，包括包装类型和非包装类型
     *
     * @param object 被检查对象，{@code null}返回{@code false}
     * @return 是否为基本类型
     * @see ClassUtils#isBasicType(Class)
     */
    public static boolean isBasicType(Object object) {
        if (null == object) {
            return false;
        }
        return ClassUtils.isBasicType(object.getClass());
    }


    /**
     * 获取对象标识
     *
     * @param obj 对象
     * @return 对象标识
     */
    public static String getIdentity(Object obj) {
        return Integer.toHexString(System.identityHashCode(obj));
    }

    /**
     * 获取对象的hash值
     *
     * @param obj obj
     * @return hashCode
     */
    public static int hashCode(Object obj) {
        return Hashes.hashCode(obj);
    }

    /**
     * 转为string
     *
     * @param o ignore
     * @return ignore
     */
    public static String toString(Object o) {
        if (o == null) {
            return StringUtils.EMPTY;
        } else if (o instanceof String) {
            return (String) o;
        } else if (o instanceof Object[]) {
            return Arrays.toString((Object[]) o);
        } else if (o instanceof byte[]) {
            return Arrays.toString((byte[]) o);
        } else if (o instanceof short[]) {
            return Arrays.toString((short[]) o);
        } else if (o instanceof int[]) {
            return Arrays.toString((int[]) o);
        } else if (o instanceof long[]) {
            return Arrays.toString((long[]) o);
        } else if (o instanceof float[]) {
            return Arrays.toString((float[]) o);
        } else if (o instanceof double[]) {
            return Arrays.toString((double[]) o);
        } else if (o instanceof char[]) {
            return Arrays.toString((char[]) o);
        } else if (o instanceof boolean[]) {
            return Arrays.toString((boolean[]) o);
        } else {
            return defaultIfNull(o.toString(), StringUtils.EMPTY);
        }
    }


    /**
     * 序列化对象
     *
     * @param o 对象
     * @return ignore
     */
    public static <T extends Serializable> byte[] serialize(T o) {
        if (o == null) {
            return null;
        }
        try (ByteArrayOutputStream bs = new ByteArrayOutputStream(1024);
             ObjectOutputStream oos = new ObjectOutputStream(bs)) {
            oos.writeObject(o);
            oos.flush();
            return bs.toByteArray();
        } catch (IOException e) {
            throw new IllegalArgumentException("failed to serialize object of type: " + o.getClass(), e);
        }
    }

    /**
     * 反序列化
     *
     * @param bytes ignore
     * @return ignore
     */
    @SuppressWarnings("unchecked")
    public static <T> T deserialize(byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes))) {
            return (T) ois.readObject();
        } catch (Exception e) {
            throw new IllegalArgumentException("failed to deserialize object", e);
        }
    }

    /**
     * 如果实现了cloneable 会调用clone方法
     * 如果没实现cloneable 则会深拷贝对象 对象必须实现序列化接口
     *
     * @param o   对象
     * @param <T> T
     * @return clone T
     */
    public static <T> T clone(T o) {
        if (o == null) {
            return null;
        }
        if (ArrayUtils.isArray(o)) {
            return deserialize(serialize(((Serializable) o)));
        }
        if (o.getClass() != Object.class && o instanceof Cloneable) {
            return Methods.invokeMethod(o, "clone");
        } else if (o instanceof Serializable) {
            return deserialize(serialize(((Serializable) o)));
        } else {
            throw new IllegalArgumentException("Failed to serialize object, not implements Cloneable and not implements Serializable");
        }
    }


    public static <T> T requireNonNull(T obj, final Supplier<String> messageSupplier) {
        return Objects.requireNonNull(obj, messageSupplier);
    }


    public static <T> T defaultIfEmpty(T value, T defaultValue) {
        return useValueIfMatch(value, Functions.<T>emptyPredicate(), defaultValue);
    }

    public static <T> T defaultIfEmpty(T value, Supplier<T> supplier) {
        return useValueIfMatch(value, Functions.<T>emptyPredicate(), supplier);
    }

    private static <T> T useValueIfMatch(T value, Predicate<T> predicate, T defaultValue) {
        if (predicate.test(value)) {
            return defaultValue;
        }
        return value;
    }

    public static <T> T defaultIfNotEquals(T value, T expectValue, T defaultValue) {
        return useValueIfMatch(value, Functions.notEqualsPredicate(expectValue), defaultValue);
    }

    public static <T> T defaultIfEquals(T value, T expectValue, T defaultValue) {
        return useValueIfMatch(value, Functions.equalsPredicate(expectValue), defaultValue);
    }

    public static <T> T defaultIfNotMatch(T value, Predicate<T> predicate, T defaultValue) {
        if (!predicate.test(value)) {
            return defaultValue;
        }
        return value;
    }

    private static <T> T useValueIfMatch(T value, Predicate<T> predicate, Supplier<T> defaultSupplier) {
        if (predicate.test(value)) {
            return defaultSupplier.get();
        }
        return value;
    }


    public static <T> T useValueIfNotMatch(T value, Predicate<T> predicate, Function<T, T> supplier) {
        if (!predicate.test(value)) {
            return supplier.apply(value);
        }
        return value;
    }



    public static boolean isNotEmpty(Object o) {
        return Emptys.isNotEmpty(o);
    }


    public static int id(Object o) {
        return System.identityHashCode(o);
    }

    /**
     * Gets the toString that would be produced by {@link Object}
     * if a class did not override toString itself. {@code null}
     * will return {@code null}.
     *
     * <pre>
     * ObjectUtils.identityToString(null)         = null
     * ObjectUtils.identityToString("")           = "java.lang.String@1e23"
     * ObjectUtils.identityToString(Boolean.TRUE) = "java.lang.Boolean@7fa"
     * </pre>
     *
     * @param object the object to create a toString for, may be
     *               {@code null}
     * @return the default toString text, or {@code null} if
     * {@code null} passed in
     */
    public static String identityToString(final Object object) {
        if (object == null) {
            return null;
        }
        final String name = object.getClass().getName();
        final String hexString = identityHashCodeHex(object);
        final StringBuilder builder = new StringBuilder(name.length() + 1 + hexString.length());
        // @formatter:off
        builder.append(name)
                .append(AT_SIGN)
                .append(hexString);
        // @formatter:on
        return builder.toString();
    }

    /**
     * Returns the hexadecimal hash code for the given object per {@link System#identityHashCode(Object)}.
     * <p>
     * Short hand for {@code Integer.toHexString(System.identityHashCode(object))}.
     * </p>
     *
     * @param object object for which the hashCode is to be calculated
     * @return Hash code in hexadecimal format.
     * @since 3.13.0
     */
    public static String identityHashCodeHex(final Object object) {
        return Integer.toHexString(System.identityHashCode(object));
    }
}
