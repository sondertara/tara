package com.sondertara.common.reflect;

import com.sondertara.common.base.Assert;
import com.sondertara.common.base.Emptys;
import com.sondertara.common.base.ObjectUtils;
import com.sondertara.common.base.Valid;
import com.sondertara.common.collection.ArrayUtils;
import com.sondertara.common.collection.CollectionUtils;
import com.sondertara.common.collection.Lists;
import com.sondertara.common.collection.PrimitiveArrays;
import com.sondertara.common.collection.Sets;
import com.sondertara.common.collection.StreamUtils;
import com.sondertara.common.collection.WeakConcurrentMap;
import com.sondertara.common.concurrent.ConcurrentReferenceHashMap;
import com.sondertara.common.convert.ConvertUtils;
import com.sondertara.common.exception.ExceptionMessage;
import com.sondertara.common.exception.ExceptionUtils;
import com.sondertara.common.exception.ReflectionException;
import com.sondertara.common.logging.Loggers;
import com.sondertara.common.reflect.parameter.ConstructorParameter;
import com.sondertara.common.reflect.parameter.MethodParameter;
import com.sondertara.common.reflect.signature.TypeSignatures;
import com.sondertara.common.reflect.type.TypeUtils;
import com.sondertara.common.regex.RegexPattern;
import com.sondertara.common.regex.RegexUtils;
import com.sondertara.common.struct.Holder;
import com.sondertara.common.text.CharUtils;
import com.sondertara.common.text.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.net.URL;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.lang.System.arraycopy;

/**
 * 反射工具类
 *
 * @author huangxiaohu
 */
@Slf4j
public class ReflectUtils {

    /**
     * 构造对象缓存
     */
    private static final WeakConcurrentMap<Class<?>, Constructor<?>[]> CONSTRUCTORS_CACHE = new WeakConcurrentMap<>();

    /**
     * 方法缓存
     */
    private static final WeakConcurrentMap<Class<?>, Method[]> METHODS_CACHE = new WeakConcurrentMap<>();

    // ---------------------------------------------------------------------------------------------------------
    // Constructor

    /**
     * 查找类中的指定参数的构造方法，如果找到构造方法，会自动设置可访问为true
     *
     * @param <T>            对象类型
     * @param clazz          类
     * @param parameterTypes 参数类型，只要任何一个参数是指定参数的父类或接口或相等即可，此参数可以不传
     * @return 构造方法，如果未找到返回null
     */
    @SuppressWarnings("unchecked")
    public static <T> Constructor<T> getConstructor(Class<T> clazz, Class<?>... parameterTypes) {
        if (null == clazz) {
            return null;
        }

        final Constructor<?>[] constructors = getConstructors(clazz);
        Class<?>[] pts;
        for (Constructor<?> constructor : constructors) {
            pts = constructor.getParameterTypes();
            if (ClassUtils.isAllAssignableFrom(pts, parameterTypes)) {
                // 构造可访问
                setAccessible(constructor);
                return (Constructor<T>) constructor;
            }
        }
        return null;
    }

    /**
     * 获得一个类中所有构造列表
     *
     * @param <T>       构造的对象类型
     * @param beanClass 类，非{@code null}
     * @return 字段列表
     * @throws SecurityException 安全检查异常
     */
    @SuppressWarnings("unchecked")
    public static <T> Constructor<T>[] getConstructors(Class<T> beanClass) throws SecurityException {
        Assert.notNull(beanClass);
        return (Constructor<T>[]) CONSTRUCTORS_CACHE.computeIfAbsent(beanClass,
                () -> getConstructorsDirectly(beanClass));
    }

    /**
     * 获得一个类中所有构造列表，直接反射获取，无缓存
     *
     * @param beanClass 类
     * @return 字段列表
     * @throws SecurityException 安全检查异常
     */
    public static Constructor<?>[] getConstructorsDirectly(Class<?> beanClass) throws SecurityException {
        return beanClass.getDeclaredConstructors();
    }

    // ---------------------------------------------------------------------------------------------------------
    // Field

    /**
     * 查找指定类中是否包含指定名称对应的字段，包括所有字段（包括非public字段），也包括父类和Object类的字段
     *
     * @param beanClass 被查找字段的类,不能为null
     * @param name      字段名
     * @return 是否包含字段
     * @throws SecurityException 安全异常
     */
    public static boolean hasField(Class<?> beanClass, String name) throws SecurityException {
        return null != ClassUtils.getField(beanClass, name);
    }

    /**
     * 获取字段名
     *
     * @param field 字段
     * @return 字段名
     */
    public static String getFieldName(Field field) {
        if (null == field) {
            return null;
        }

        return field.getName();
    }


    /**
     * 获取指定类中字段名和字段对应的有序Map，包括其父类中的字段<br>
     * 如果子类与父类中存在同名字段，则这两个字段同时存在，子类字段在前，父类字段在后。
     *
     * @param beanClass 类
     * @return 字段名和字段对应的Map，有序
     */
    public static Map<String, Field> getFieldMap(Class<?> beanClass) {
        return ClassUtils.getFieldMap(beanClass);
    }


    /**
     * 获取静态字段值
     *
     * @param field 字段
     * @return 字段值
     */
    public static Object getStaticFieldValue(Field field) {
        return getFieldValue(null, field);
    }

    /**
     * 获取字段值
     *
     * @param obj   对象，static字段则此字段为null
     * @param field 字段
     * @return 字段值
     */
    public static Object getFieldValue(Object obj, Field field) {
        if (null == field) {
            return null;
        }
        if (obj instanceof Class) {
            // 静态字段获取时对象为null
            obj = null;
        }

        setAccessible(field);
        Object result;
        try {
            result = field.get(obj);
        } catch (IllegalAccessException e) {
            throw new ReflectionException("IllegalAccess for {}.{}", field.getDeclaringClass(), field.getName(), e);
        }
        return result;
    }

    /**
     * 获取所有字段的值
     *
     * @param obj bean对象，如果是static字段，此处为类class
     * @return 字段值数组
     */
    public static Object[] getFieldsValue(Object obj) {
        if (null != obj) {
            final List<Field> fields = ClassUtils.getFieldsByCache(obj instanceof Class ? (Class<?>) obj : obj.getClass());
            final Object[] values = new Object[fields.size()];
            for (int i = 0; i < fields.size(); i++) {
                values[i] = getFieldValue(obj, fields.get(i));
            }
            return values;
        }
        return null;
    }

    /**
     * 设置字段值
     *
     * @param obj       对象,static字段则此处传Class
     * @param fieldName 字段名
     * @param value     值，值类型必须与字段类型不匹配，会自动转换对象类型
     */
    public static void setFieldValue(Object obj, String fieldName, Object value) {
        Assert.notNull(obj);
        Assert.notBlank(fieldName);

        final Field field = ClassUtils.getField((obj instanceof Class) ? (Class<?>) obj : obj.getClass(), fieldName);
        if (null == field) {
            return;
        }
        setFieldValue(obj, field, value);
    }

    /**
     * 设置字段值
     *
     * @param obj   对象，如果是static字段，此参数为null
     * @param field 字段
     * @param value 值，值类型必须与字段类型匹配，不会自动转换对象类型
     */
    public static void setFieldValue(Object obj, Field field, Object value) {
        Assert.notNull(field, "Field in [{}] not exist !", obj);

        final Class<?> fieldType = field.getType();
        if (null != value) {
            if (!fieldType.isAssignableFrom(value.getClass())) {
                // 对于类型不同的字段，尝试转换，转换失败则使用原对象类型
                try {
                    value = ConvertUtils.convert(fieldType, value);
                } catch (Exception e) {
                    log.warn("Convert value[{}] to target[{}] error", value, fieldType, e);
                }
            }
        } else {
            // 获取null对应默认值，防止原始类型造成空指针问题
            value = ClassUtils.getDefaultValue(fieldType);
        }

        setAccessible(field);
        try {
            field.set(obj instanceof Class ? null : obj, value);
        } catch (IllegalAccessException e) {
            throw new ReflectionException("IllegalAccess for {}.{}", obj, field.getName(), e);
        }
    }

    /**
     * 是否为父类引用字段<br>
     * 当字段所在类是对象子类时（对象中定义的非static的class），会自动生成一个以"this$0"为名称的字段，指向父类对象
     *
     * @param field 字段
     * @return 是否为父类引用字段
     */
    public static boolean isOuterClassField(Field field) {
        return "this$0".equals(field.getName());
    }

    // ---------------------------------------------------------------------------------------------------------
    // method

    /**
     * 获得指定类本类及其父类中的Public方法名<br>
     * 去重重载的方法
     *
     * @param clazz 类
     * @return 方法名Set
     */
    public static Set<String> getPublicMethodNames(Class<?> clazz) {
        final HashSet<String> methodSet = new HashSet<>();
        final List<Method> methodArray = getPublicMethods(clazz);
        if (ArrayUtils.isNotEmpty(methodArray)) {
            for (Method method : methodArray) {
                methodSet.add(method.getName());
            }
        }
        return methodSet;
    }

    /**
     * 获得本类及其父类所有Public方法
     *
     * @param clazz 查找方法的类
     * @return 过滤后的方法列表
     */
    public static List<Method> getPublicMethods(Class<?> clazz) {
        return null == clazz ? Collections.emptyList() : Arrays.asList(clazz.getMethods());
    }

    /**
     * 获得指定类过滤后的Public方法列表<br>
     *
     * @param clazz  查找方法的类
     * @param filter 过滤器
     * @return 过滤后的方法列表
     */
    public static List<Method> getPublicMethods(Class<?> clazz, Predicate<Method> filter) {
        if (null == clazz) {
            return new ArrayList<>();
        }

        final List<Method> methods = getPublicMethods(clazz);

        if (null == filter) {
            return methods;
        }
        List<Method> methodList = new ArrayList<>();
        for (Method method : methods) {
            if (filter.test(method)) {
                methodList.add(method);
            }
        }

        return methodList;
    }

    /**
     * 获得指定类过滤后的Public方法列表
     *
     * @param clazz          查找方法的类
     * @param excludeMethods 不包括的方法
     * @return 过滤后的方法列表
     */
    public static List<Method> getPublicMethods(Class<?> clazz, Method... excludeMethods) {
        final HashSet<Method> excludeMethodSet = Sets.newHashSet(excludeMethods);
        return getPublicMethods(clazz, method -> !excludeMethodSet.contains(method));
    }

    /**
     * 获得指定类过滤后的Public方法列表
     *
     * @param clazz              查找方法的类
     * @param excludeMethodNames 不包括的方法名列表
     * @return 过滤后的方法列表
     */
    public static List<Method> getPublicMethods(Class<?> clazz, String... excludeMethodNames) {
        final HashSet<String> excludeMethodNameSet = Sets.newHashSet(excludeMethodNames);
        return getPublicMethods(clazz, method -> !excludeMethodNameSet.contains(method.getName()));
    }

    /**
     * 查找指定Public方法 如果找不到对应的方法或方法不为public的则返回{@code null}
     *
     * @param clazz      类
     * @param methodName 方法名
     * @param paramTypes 参数类型
     * @return 方法
     * @throws SecurityException 无权访问抛出异常
     */
    public static Method getPublicMethod(Class<?> clazz, String methodName, Class<?>... paramTypes)
            throws SecurityException {
        try {
            return clazz.getMethod(methodName, paramTypes);
        } catch (NoSuchMethodException ex) {
            return null;
        }
    }

    /**
     * 查找指定对象中的所有方法（包括非public方法），也包括父对象和Object类的方法
     *
     * <p>
     * 此方法为精准获取方法名，即方法名和参数数量和类型必须一致，否则返回{@code null}。
     * </p>
     *
     * @param obj        被查找的对象，如果为{@code null}返回{@code null}
     * @param methodName 方法名，如果为空字符串返回{@code null}
     * @param args       参数
     * @return 方法
     * @throws SecurityException 无访问权限抛出异常
     */
    public static Method getMethodOfObj(Object obj, String methodName, Object... args) throws SecurityException {
        if (null == obj || StringUtils.isBlank(methodName)) {
            return null;
        }
        return getMethod(obj.getClass(), methodName, ClassUtils.getClasses(args));
    }

    /**
     * 忽略大小写查找指定方法，如果找不到对应的方法则返回{@code null}
     *
     * <p>
     * 此方法为精准获取方法名，即方法名和参数数量和类型必须一致，否则返回{@code null}。
     * </p>
     *
     * @param clazz      类，如果为{@code null}返回{@code null}
     * @param methodName 方法名，如果为空字符串返回{@code null}
     * @param paramTypes 参数类型，指定参数类型如果是方法的子类也算
     * @return 方法
     * @throws SecurityException 无权访问抛出异常
     */
    public static Method getMethodIgnoreCase(Class<?> clazz, String methodName, Class<?>... paramTypes) {
        return getMethod(clazz, true, methodName, paramTypes);
    }

    /**
     * 查找指定方法 如果找不到对应的方法则返回{@code null}
     *
     * <p>
     * 此方法为精准获取方法名，即方法名和参数数量和类型必须一致，否则返回{@code null}。
     * </p>
     *
     * @param clazz      类，如果为{@code null}返回{@code null}
     * @param methodName 方法名，如果为空字符串返回{@code null}
     * @param paramTypes 参数类型，指定参数类型如果是方法的子类也算
     * @return 方法
     * @throws SecurityException 无权访问抛出异常
     */
    public static Method getMethod(Class<?> clazz, String methodName, Class<?>... paramTypes) throws SecurityException {
        return getMethod(clazz, false, methodName, paramTypes);
    }

    /**
     * 查找指定方法 如果找不到对应的方法则返回{@code null}<br>
     * 此方法为精准获取方法名，即方法名和参数数量和类型必须一致，否则返回{@code null}。<br>
     * 如果查找的方法有多个同参数类型重载，查找第一个找到的方法
     *
     * @param clazz      类，如果为{@code null}返回{@code null}
     * @param ignoreCase 是否忽略大小写
     * @param methodName 方法名，如果为空字符串返回{@code null}
     * @param paramTypes 参数类型，指定参数类型如果是方法的子类也算
     * @return 方法
     * @throws SecurityException 无权访问抛出异常
     */
    public static Method getMethod(Class<?> clazz, boolean ignoreCase, String methodName, Class<?>... paramTypes)
            throws SecurityException {
        if (null == clazz || StringUtils.isBlank(methodName)) {
            return null;
        }

        final Method[] methods = getMethods(clazz);
        if (ArrayUtils.isNotEmpty(methods)) {
            for (Method method : methods) {
                if (StringUtils.equals(methodName, method.getName(), ignoreCase)
                        && ClassUtils.isAllAssignableFrom(method.getParameterTypes(), paramTypes)
                        // 排除桥接方法，pr#1965@Github
                        && !method.isBridge()) {
                    return method;
                }
            }
        }
        return null;
    }

    /**
     * 按照方法名查找指定方法名的方法，只返回匹配到的第一个方法，如果找不到对应的方法则返回{@code null}
     *
     * <p>
     * 此方法只检查方法名是否一致，并不检查参数的一致性。
     * </p>
     *
     * @param clazz      类，如果为{@code null}返回{@code null}
     * @param methodName 方法名，如果为空字符串返回{@code null}
     * @return 方法
     * @throws SecurityException 无权访问抛出异常
     */
    public static Method getMethodByName(Class<?> clazz, String methodName) throws SecurityException {
        return getMethodByName(clazz, false, methodName);
    }

    /**
     * 按照方法名查找指定方法名的方法，只返回匹配到的第一个方法，如果找不到对应的方法则返回{@code null}
     *
     * <p>
     * 此方法只检查方法名是否一致（忽略大小写），并不检查参数的一致性。
     * </p>
     *
     * @param clazz      类，如果为{@code null}返回{@code null}
     * @param methodName 方法名，如果为空字符串返回{@code null}
     * @return 方法
     */
    public static Method getMethodByNameIgnoreCase(Class<?> clazz, String methodName) {
        return getMethodByName(clazz, true, methodName);
    }

    /**
     * 按照方法名查找指定方法名的方法，只返回匹配到的第一个方法，如果找不到对应的方法则返回{@code null}
     *
     * <p>
     * 此方法只检查方法名是否一致，并不检查参数的一致性。
     * </p>
     *
     * @param clazz      类，如果为{@code null}返回{@code null}
     * @param ignoreCase 是否忽略大小写
     * @param methodName 方法名，如果为空字符串返回{@code null}
     * @return 方法
     */
    public static Method getMethodByName(Class<?> clazz, boolean ignoreCase, String methodName) {
        if (null == clazz || StringUtils.isBlank(methodName)) {
            return null;
        }

        final Method[] methods = getMethods(clazz);
        if (ArrayUtils.isNotEmpty(methods)) {
            for (Method method : methods) {
                if (StringUtils.equals(methodName, method.getName(), ignoreCase)
                        // 排除桥接方法
                        && !method.isBridge()) {
                    return method;
                }
            }
        }
        return null;
    }

    /**
     * 获得指定类中的Public方法名<br>
     * 去重重载的方法
     *
     * @param clazz 类
     * @return 方法名Set
     * @throws SecurityException 安全异常
     */
    public static Set<String> getMethodNames(Class<?> clazz) throws SecurityException {
        final HashSet<String> methodSet = new HashSet<>();
        final Method[] methods = getMethods(clazz);
        for (Method method : methods) {
            methodSet.add(method.getName());
        }
        return methodSet;
    }

    /**
     * 获得指定类过滤后的Public方法列表
     *
     * @param clazz  查找方法的类
     * @param filter 过滤器
     * @return 过滤后的方法列表
     * @throws SecurityException 安全异常
     */
    public static Method[] getMethods(Class<?> clazz, Predicate<Method> filter) throws SecurityException {
        if (null == clazz) {
            return null;
        }
        return ArrayUtils.filter(getMethods(clazz), filter);
    }

    /**
     * 获得一个类中所有方法列表，包括其父类中的方法
     *
     * @param beanClass 类，非{@code null}
     * @return 方法列表
     * @throws SecurityException 安全检查异常
     */
    public static Method[] getMethods(Class<?> beanClass) throws SecurityException {
        Assert.notNull(beanClass);
        return METHODS_CACHE.computeIfAbsent(beanClass, () -> getMethodsDirectly(beanClass, true, true));
    }

    /**
     * 获得一个类中所有方法列表，直接反射获取，无缓存<br>
     * 接口获取方法和默认方法，获取的方法包括：
     * <ul>
     * <li>本类中的所有方法（包括static方法）</li>
     * <li>父类中的所有方法（包括static方法）</li>
     * <li>Object中（包括static方法）</li>
     * </ul>
     *
     * @param beanClass            类或接口
     * @param withSupers           是否包括父类或接口的方法列表
     * @param withMethodFromObject 是否包括Object中的方法
     * @return 方法列表
     * @throws SecurityException 安全检查异常
     */
    public static Method[] getMethodsDirectly(Class<?> beanClass, boolean withSupers, boolean withMethodFromObject)
            throws SecurityException {
        Assert.notNull(beanClass);

        if (beanClass.isInterface()) {
            // 对于接口，直接调用Class.getMethods方法获取所有方法，因为接口都是public方法
            return withSupers ? beanClass.getMethods() : beanClass.getDeclaredMethods();
        }

        final Map<String, Method> result = new LinkedHashMap<>();
        Class<?> searchType = beanClass;
        while (searchType != null) {
            if (!withMethodFromObject && Object.class == searchType) {
                break;
            }
            Method[] methods = searchType.getDeclaredMethods();
            for (Method method : methods) {
                result.putIfAbsent(getUniqueKey(method), method);
            }
            List<Method> list = getDefaultMethodsFromInterface(searchType);
            for (Method method : list) {
                result.putIfAbsent(getUniqueKey(method), method);
            }

            searchType = (withSupers && !searchType.isInterface()) ? searchType.getSuperclass() : null;
        }

        return result.values().toArray(new Method[0]);
    }

    /**
     * 是否为equals方法
     *
     * @param method 方法
     * @return 是否为equals方法
     */
    public static boolean isEqualsMethod(Method method) {
        if (method == null || 1 != method.getParameterCount() || !"equals".equals(method.getName())) {
            return false;
        }
        return (method.getParameterTypes()[0] == Object.class);
    }


    public static boolean isGetterOrSetter(@NonNull Method method) {
        return isGetterOrSetter(method, false);
    }

    /**
     * 检查给定方法是否为Getter或者Setter方法，规则为：<br>
     * <ul>
     * <li>方法参数必须为0个或1个</li>
     * <li>方法名称不能是getClass</li>
     * <li>如果是无参方法，则判断是否以“get”或“is”开头</li>
     * <li>如果方法参数1个，则判断是否以“set”开头</li>
     * </ul>
     *
     * @param method          方法
     * @param filterNonFields 是否过滤无属性
     * @return 是否为Getter或者Setter方法
     */
    public static boolean isGetterOrSetter(@NonNull Method method, boolean filterNonFields) {
        Assert.notNull(method);
        if (Modifiers.isStatic(method) || Modifiers.isAbstract(method) || !Modifiers.isPublic(method)) {
            return false;
        }

        String methodName = method.getName();
        String fieldName = null;
        if (methodName.startsWith("set")) {
            if (method.getParameterTypes().length != 1) {
                return false;
            }
            fieldName = methodName.substring(3);
        } else if (methodName.startsWith("get")) {
            if (method.getParameterTypes().length != 0) {
                return false;
            }
            fieldName = methodName.substring(3);
        } else if (methodName.startsWith("is")) {
            if (method.getParameterTypes().length != 0) {
                return false;
            }
            fieldName = methodName.substring(2);
        }
        if (StringUtils.isEmpty(fieldName)) {
            return false;
        }
        fieldName = StringUtils.lowerCaseFirstChar(fieldName);
        if (filterNonFields) {
            Class<?> beanClass = method.getDeclaringClass();
            Field field = getAnyField(beanClass, fieldName);
            return field != null;
        }

        return true;
    }
    // ---------------------------------------------------------------------------------------------------------
    // newInstance

    public static <T> T newInstance(Constructor<T> constructor, Object... params) {
        try {
            return constructor.newInstance(params);
        } catch (Exception e) {
            throw new ReflectionException("Instance class [{}] error!", constructor.getDeclaringClass(), e);
        }

    }

    /**
     * 实例化对象
     *
     * @param <T>    对象类型
     * @param clazz  类
     * @param params 构造函数参数
     * @return 对象
     */
    public static <T> T newInstance(Class<T> clazz, Object... params) {
        if (ArrayUtils.isEmpty(params)) {
            final Constructor<T> constructor = getConstructor(clazz);
            try {
                return constructor.newInstance();
            } catch (Exception ignored) {
            }
        }

        final Class<?>[] paramTypes = ClassUtils.getClasses(params);
        final Constructor<T> constructor = getConstructor(clazz, paramTypes);
        if (null == constructor) {
            throw new ReflectionException("No Constructor matched for parameter types: [{}]", new Object[]{paramTypes});
        }
        try {
            return constructor.newInstance(params);
        } catch (Exception e) {
            throw new ReflectionException("Instance class [{}] error!", clazz, e);
        }
    }

    /**
     * 尝试遍历并调用此类的所有构造方法，直到构造成功并返回
     * <p>
     * 对于某些特殊的接口，按照其默认实现实例化，例如：
     *
     * <pre>
     *     Map       -》 HashMap
     *     Collction -》 ArrayList
     *     List      -》 ArrayList
     *     Set       -》 HashSet
     * </pre>
     *
     * @param <T>  对象类型
     * @param type 被构造的类
     * @return 构造后的对象，构造失败返回{@code null}
     */
    @SuppressWarnings("unchecked")
    public static <T> T newInstanceIfPossible(Class<T> type) {
        Assert.notNull(type);

        // 原始类型
        if (type.isPrimitive()) {
            return (T) ClassUtils.getPrimitiveDefaultValue(type);
        }

        // 某些特殊接口的实例化按照默认实现进行
        if (type.isAssignableFrom(AbstractMap.class)) {
            type = (Class<T>) HashMap.class;
        } else if (type.isAssignableFrom(List.class)) {
            type = (Class<T>) ArrayList.class;
        } else if (type.isAssignableFrom(Set.class)) {
            type = (Class<T>) HashSet.class;
        }

        try {
            return newInstance(type);
        } catch (Exception e) {
            // ignore
            // 默认构造不存在的情况下查找其它构造
        }

        // 枚举
        if (type.isEnum()) {
            return type.getEnumConstants()[0];
        }

        // 数组
        if (type.isArray()) {
            return (T) Array.newInstance(type.getComponentType(), 0);
        }

        final Constructor<T>[] constructors = getConstructors(type);
        Class<?>[] parameterTypes;
        for (Constructor<T> constructor : constructors) {
            parameterTypes = constructor.getParameterTypes();
            if (0 == parameterTypes.length) {
                continue;
            }
            setAccessible(constructor);
            try {
                return constructor.newInstance(ClassUtils.getDefaultValues(parameterTypes));
            } catch (Exception ignore) {
                // 构造出错时继续尝试下一种构造方式
            }
        }
        return null;
    }

    // ---------------------------------------------------------------------------------------------------------
    // invoke

    /**
     * 执行静态方法
     *
     * @param <T>    对象类型
     * @param method 方法（对象方法或static方法都可）
     * @param args   参数对象
     * @return 结果
     */
    public static <T> T invokeStatic(Method method, Object... args) {
        return invoke(null, method, args);
    }

    /**
     * 执行方法<br>
     * 执行前要检查给定参数：
     *
     * <pre>
     * 1. 参数个数是否与方法参数个数一致
     * 2. 如果某个参数为null但是方法这个位置的参数为原始类型，则赋予原始类型默认值
     * </pre>
     *
     * @param <T>    返回对象类型
     * @param obj    对象，如果执行静态方法，此值为{@code null}
     * @param method 方法（对象方法或static方法都可）
     * @param args   参数对象
     * @return 结果
     */
    public static <T> T invokeWithCheck(Object obj, Method method, Object... args) {
        final Class<?>[] types = method.getParameterTypes();
        if (null != args) {
            Assert.isTrue(args.length == types.length,
                    "Params length [{}] is not fit for param length [{}] of method !", args.length, types.length);
            Class<?> type;
            for (int i = 0; i < args.length; i++) {
                type = types[i];
                if (type.isPrimitive() && null == args[i]) {
                    // 参数是原始类型，而传入参数为null时赋予默认值
                    args[i] = ClassUtils.getDefaultValue(type);
                }
            }
        }

        return invoke(obj, method, args);
    }

    /**
     * 执行方法
     *
     * <p>
     * 对于用户传入参数会做必要检查，包括：
     *
     * <pre>
     *     1、忽略多余的参数
     *     2、参数不够补齐默认值
     *     3、传入参数为null，但是目标参数类型为原始类型，做转换
     * </pre>
     *
     * @param <T>    返回对象类型
     * @param obj    对象，如果执行静态方法，此值为{@code null}
     * @param method 方法（对象方法或static方法都可）
     * @param args   参数对象
     * @return 结果
     * @throws InvocationTargetException 目标方法执行异常
     * @throws IllegalAccessException    访问异常
     */
    @SuppressWarnings("unchecked")
    public static <T> T invoke(Object obj, Method method, Object... args) {
        setAccessible(method);

        // 检查用户传入参数：
        // 1、忽略多余的参数
        // 2、参数不够补齐默认值
        // 3、通过NullWrapperBean传递的参数,会直接赋值null
        // 4、传入参数为null，但是目标参数类型为原始类型，做转换
        // 5、传入参数类型不对应，尝试转换类型
        final Class<?>[] parameterTypes = method.getParameterTypes();
        final Object[] actualArgs = new Object[parameterTypes.length];
        if (null != args) {
            for (int i = 0; i < actualArgs.length; i++) {
                if (i >= args.length || null == args[i]) {
                    // 越界或者空值
                    actualArgs[i] = ClassUtils.getDefaultValue(parameterTypes[i]);
                } else if (!parameterTypes[i].isAssignableFrom(args[i].getClass())) {
                    // 对于类型不同的字段，尝试转换，转换失败则使用原对象类型
                    final Object targetValue = ConvertUtils.convert(parameterTypes[i], args[i]);
                    if (null != targetValue) {
                        actualArgs[i] = targetValue;
                    }
                } else {
                    actualArgs[i] = args[i];
                }
            }
        }

        if (method.isDefault()) {
            // 当方法是default方法时，尤其对象是代理对象，需使用句柄方式执行
            // 代理对象情况下调用method.invoke会导致循环引用执行，最终栈溢出
            return MethodHandleUtils.invokeSpecial(obj, method, args);
        }
        try {
            return (T) method.invoke(ClassUtils.isStatic(method) ? null : obj, actualArgs);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new ReflectionException(e);
        }

    }

    /**
     * 执行对象中指定方法
     * 如果需要传递的参数为null,请使用NullWrapperBean来传递,不然会丢失类型信息
     *
     * @param <T>        返回对象类型
     * @param obj        方法所在对象
     * @param methodName 方法名
     * @param args       参数列表
     * @return 执行结果
     */
    public static <T> T invoke(Object obj, String methodName, Object... args) {
        Assert.notNull(obj, "Object to get method must be not null!");
        Assert.notBlank(methodName, "Method name must be not blank!");

        final Method method = getMethodOfObj(obj, methodName, args);
        if (null == method) {
            throw new IllegalArgumentException(StringUtils.format("No such method: [{}] from [{}]", methodName, obj.getClass()));
        }
        return invoke(obj, method, args);
    }

    /**
     * 设置方法为可访问（私有方法可以被外部调用）
     *
     * @param <T>              AccessibleObject的子类，比如Class、Method、Field等
     * @param accessibleObject 可设置访问权限的对象，比如Class、Method、Field等
     * @return 被设置可访问的对象
     */
    public static <T extends AccessibleObject> T setAccessible(T accessibleObject) {
        if (null != accessibleObject && !accessibleObject.isAccessible()) {
            accessibleObject.setAccessible(true);
        }
        return accessibleObject;
    }

    /**
     * 获取方法的唯一键，结构为:
     *
     * <pre>
     *     返回类型#方法名:参数1类型,参数2类型...
     * </pre>
     *
     * @param method 方法
     * @return 方法唯一键
     */
    private static String getUniqueKey(Method method) {
        final StringBuilder sb = new StringBuilder();
        sb.append(method.getReturnType().getName()).append('#');
        sb.append(method.getName());
        Class<?>[] parameters = method.getParameterTypes();
        for (int i = 0; i < parameters.length; i++) {
            if (i == 0) {
                sb.append(':');
            } else {
                sb.append(',');
            }
            sb.append(parameters[i].getName());
        }
        return sb.toString();
    }

    /**
     * 获取类对应接口中的非抽象方法（default方法）
     *
     * @param clazz 类
     * @return 方法列表
     */
    private static List<Method> getDefaultMethodsFromInterface(Class<?> clazz) {
        List<Method> result = new ArrayList<>();
        for (Class<?> ifc : clazz.getInterfaces()) {
            for (Method m : ifc.getMethods()) {
                if (!ModifierUtils.isAbstract(m)) {
                    result.add(m);
                }
            }
        }
        return result;
    }


    /**
     * Load Class by class name. If class not found in it's Class loader or one of the parent class loaders - delegate to the Thread's ContextClassLoader
     *
     * @param className Canonical class name
     * @return Class definition of className
     * @throws ClassNotFoundException no class found
     */
    public static Class<?> loadClassByName(String className) throws ClassNotFoundException {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            return Thread.currentThread().getContextClassLoader().loadClass(className);
        }
    }


    /**
     * Returns overridden method from superclass if it exists. If method was not found returns null.
     *
     * @param method is method to find
     * @return overridden method from superclass
     */
    public static Method getOverriddenMethod(Method method) {
        Class<?> declaringClass = method.getDeclaringClass();
        Class<?> superClass = declaringClass.getSuperclass();
        Method result = null;
        if (superClass != null && !(superClass.equals(Object.class))) {
            result = findMethod(method, superClass);
        }
        if (result == null) {
            for (Class<?> anInterface : declaringClass.getInterfaces()) {
                result = findMethod(method, anInterface);
                if (result != null) {
                    return result;
                }
            }
        }
        return result;
    }

    /**
     * Searches the method methodToFind in given class cls. If the method is found returns it, else return null.
     *
     * @param methodToFind is the method to search
     * @param cls          is the class or interface where to search
     * @return method if it is found
     */
    public static Method findMethod(Method methodToFind, Class<?> cls) {
        if (cls == null) {
            return null;
        }
        String methodToSearch = methodToFind.getName();
        Class<?>[] soughtForParameterType = methodToFind.getParameterTypes();
        Type[] soughtForGenericParameterType = methodToFind.getGenericParameterTypes();
        for (Method method : cls.getMethods()) {
            if (method.getName().equals(methodToSearch) && method.getReturnType().isAssignableFrom(methodToFind.getReturnType())) {
                Class<?>[] srcParameterTypes = method.getParameterTypes();
                Type[] srcGenericParameterTypes = method.getGenericParameterTypes();
                if (soughtForParameterType.length == srcParameterTypes.length &&
                        soughtForGenericParameterType.length == srcGenericParameterTypes.length) {
                    if (hasIdenticalParameters(srcParameterTypes, soughtForParameterType, srcGenericParameterTypes, soughtForGenericParameterType)) {
                        return method;
                    }
                }
            }
        }
        return null;
    }

    private static boolean hasIdenticalParameters(Class<?>[] srcParameterTypes, Class<?>[] soughtForParameterType,
                                                  Type[] srcGenericParameterTypes, Type[] soughtForGenericParameterType) {
        for (int j = 0; j < soughtForParameterType.length; j++) {
            Class<?> parameterType = soughtForParameterType[j];
            if (!(srcParameterTypes[j].equals(parameterType) || (!srcGenericParameterTypes[j].equals(soughtForGenericParameterType[j]) &&
                    srcParameterTypes[j].isAssignableFrom(parameterType)))) {
                return false;
            }
        }
        return true;
    }


    /**
     * Returns the list of declared fields from the class <code>cls</code> and its superclasses
     * excluding <code>Object</code> class. If the field from child class hides the field from superclass,
     * the field from superclass won't be added to the result list.
     * <p>
     * The list is sorted by name to make the output of this method deterministic.
     * See https://docs.oracle.com/javase/8/docs/api/java/lang/Class.html#getFields--
     *
     * @param cls is the processing class
     * @return list of Fields
     */
    public static List<Field> getDeclaredFields(Class<?> cls) {
        if (cls == null || Object.class.equals(cls)) {
            return new ArrayList<>();
        }
        final List<Field> fields = new ArrayList<>();
        final Set<String> fieldNames = new HashSet<>();
        for (Field field : cls.getDeclaredFields()) {
            fields.add(field);
            fieldNames.add(field.getName());
        }
        for (Field field : getDeclaredFields(cls.getSuperclass())) {
            if (!fieldNames.contains(field.getName())) {
                fields.add(field);
            }
        }

        // Make sure the order is deterministic
        fields.sort(Comparator.comparing(Field::getName));

        return fields;
    }

    /**
     * Returns an annotation by type from a method.
     *
     * @param method          is the method to find
     * @param annotationClass is the type of annotation
     * @param <A>             is the type of annotation
     * @return annotation if it is found
     */
    public static <A extends Annotation> A getAnnotation(Method method, Class<A> annotationClass) {
        A annotation = method.getAnnotation(annotationClass);
        if (annotation == null) {
            for (Annotation metaAnnotation : method.getAnnotations()) {
                annotation = metaAnnotation.annotationType().getAnnotation(annotationClass);
                if (annotation != null) {
                    return annotation;
                }
            }
            Method superclassMethod = getOverriddenMethod(method);
            if (superclassMethod != null) {
                annotation = getAnnotation(superclassMethod, annotationClass);
            }
        }
        return annotation;
    }

    public static <A extends Annotation> A getAnnotation(Class<?> cls, Class<A> annotationClass) {
        A annotation = cls.getAnnotation(annotationClass);
        if (annotation == null) {
            for (Annotation metaAnnotation : cls.getAnnotations()) {
                annotation = metaAnnotation.annotationType().getAnnotation(annotationClass);
                if (annotation != null) {
                    return annotation;
                }
            }
            Class<?> superClass = cls.getSuperclass();
            if (superClass != null && !(superClass.equals(Object.class))) {
                annotation = getAnnotation(superClass, annotationClass);
            }
        }
        if (annotation == null) {
            for (Class<?> anInterface : cls.getInterfaces()) {
                for (Annotation metaAnnotation : anInterface.getAnnotations()) {
                    annotation = metaAnnotation.annotationType().getAnnotation(annotationClass);
                    if (annotation != null) {
                        return annotation;
                    }
                }
                annotation = getAnnotation(anInterface, annotationClass);
                if (annotation != null) {
                    return annotation;
                }
            }
        }
        return annotation;
    }

    /**
     * Returns a List of repeatable annotations by type from a method.
     *
     * @param method          is the method to find
     * @param annotationClass is the type of annotation
     * @param <A>             is the type of annotation
     * @return List of repeatable annotations if it is found
     */
    public static <A extends Annotation> List<A> getRepeatableAnnotations(Method method, Class<A> annotationClass) {
        Set<A> annotationsSet = new LinkedHashSet<>();
        A[] annotations = method.getAnnotationsByType(annotationClass);
        if (annotations != null) {
            annotationsSet.addAll(Arrays.asList(annotations));
        }
        for (Annotation metaAnnotation : method.getAnnotations()) {
            annotations = metaAnnotation.annotationType().getAnnotationsByType(annotationClass);
            if (annotations.length > 0) {
                annotationsSet.addAll(Arrays.asList(annotations));
            }
        }
        Method superclassMethod = getOverriddenMethod(method);
        if (superclassMethod != null) {
            List<A> superAnnotations = getRepeatableAnnotations(superclassMethod, annotationClass);
            if (superAnnotations != null) {
                annotationsSet.addAll(superAnnotations);
            }
        }
        if (annotationsSet.isEmpty()) {
            return null;
        }
        return new ArrayList<>(annotationsSet);
    }

    public static <A extends Annotation> List<A> getRepeatableAnnotations(Class<?> cls, Class<A> annotationClass) {
        A[] annotations = getRepeatableAnnotationsArray(cls, annotationClass);
        if (annotations == null || annotations.length == 0) {
            return null;
        }
        return Arrays.asList(annotations);
    }

    public static <A extends Annotation> A[] getRepeatableAnnotationsArray(Class<?> cls, Class<A> annotationClass) {
        A[] annotations = cls.getAnnotationsByType(annotationClass);
        if (annotations.length == 0) {
            for (Annotation metaAnnotation : cls.getAnnotations()) {
                annotations = metaAnnotation.annotationType().getAnnotationsByType(annotationClass);
                if (annotations.length > 0) {
                    return annotations;
                }
            }
            Class<?> superClass = cls.getSuperclass();
            if (superClass != null && !(superClass.equals(Object.class))) {
                annotations = getRepeatableAnnotationsArray(superClass, annotationClass);
            }
        }
        if (annotations == null || annotations.length == 0) {
            for (Class<?> anInterface : cls.getInterfaces()) {
                for (Annotation metaAnnotation : anInterface.getAnnotations()) {
                    annotations = metaAnnotation.annotationType().getAnnotationsByType(annotationClass);
                    if (annotations.length > 0) {
                        return annotations;
                    }
                }
                annotations = getRepeatableAnnotationsArray(anInterface, annotationClass);
                if (annotations != null) {
                    return annotations;
                }
            }
        }
        return annotations;
    }

    public static Annotation[][] getParameterAnnotations(Method method) {
        Annotation[][] methodAnnotations = method.getParameterAnnotations();
        Method overriddenmethod = getOverriddenMethod(method);

        while (overriddenmethod != null) {
            Annotation[][] overriddenAnnotations = overriddenmethod
                    .getParameterAnnotations();

            for (int i = 0; i < methodAnnotations.length; i++) {
                List<Type> types = new ArrayList<>();
                for (int j = 0; j < methodAnnotations[i].length; j++) {
                    types.add(methodAnnotations[i][j].annotationType());
                }
                for (int j = 0; j < overriddenAnnotations[i].length; j++) {
                    if (!types.contains(overriddenAnnotations[i][j]
                            .annotationType())) {
                        methodAnnotations[i] = ArrayUtils.append(
                                methodAnnotations[i],
                                overriddenAnnotations[i][j]);
                    }
                }

            }

            overriddenmethod = getOverriddenMethod(overriddenmethod);
        }
        return methodAnnotations;
    }


    public static Optional<Object> safeInvoke(Method method, Object obj, Object... args) {
        try {
            return Optional.ofNullable(method.invoke(obj, args));
        } catch (IllegalAccessException | InvocationTargetException e) {
            return Optional.empty();
        }

    }

    private static final ParameterServiceRegistry PARAMETER_SERVICE_REGISTRY = ParameterServiceRegistry.getInstance();

    private static final Map<Class<?>, Method[]> DECLARED_METHODS_CACHE = new ConcurrentReferenceHashMap<Class<?>, Method[]>(256);
    private static final Method OBJECT_EQUALS = getDeclaredMethod(Object.class, "equals", Object.class);
    private static final Method OBJECT_HASHCODE = getDeclaredMethod(Object.class, "hashCode");
    private static final Class<?>[] EMPTY_CLASS_ARRAY = new Class<?>[0];

    private static final Method[] EMPTY_METHOD_ARRAY = new Method[0];

    private static final Field[] EMPTY_FIELD_ARRAY = new Field[0];

    public static String getTypeName(@NonNull Class type) {
        return TypeUtils.toString(type);
    }

    public static boolean isInnerClass(@NonNull Class<?> clazz) {
        return clazz.isMemberClass() && !isStatic(clazz) && clazz.getEnclosingClass() != null;
    }

    public static boolean isLambda(@NonNull Class<?> clazz) {
        Assert.notNull(clazz);
        return clazz.isSynthetic() && RegexUtils.match(RegexPattern.PATTERN_LAMBDA_CLASS, getSimpleClassName(clazz));
    }

    public static boolean isStatic(@NonNull Class<?> clazz) {
        return (clazz.getModifiers() & Modifier.STATIC) != 0;
    }

    public static boolean isAnonymousOrLocal(@NonNull Class<?> clazz) {
        return isAnonymous(clazz) || isLocal(clazz);
    }

    public static boolean isAnonymous(@NonNull Class clazz) {
        return (!Enum.class.isAssignableFrom(clazz)) && clazz.isAnonymousClass();
    }

    public static boolean isLocal(@NonNull Class clazz) {
        return (!Enum.class.isAssignableFrom(clazz)) && clazz.isLocalClass();
    }


    public static Class<? extends Member> memberType(Member member) {
        Objects.requireNonNull(member, "member");
        if (member instanceof Field) {
            return Field.class;
        } else if (member instanceof Method) {
            return Method.class;
        } else if (member instanceof Constructor) {
            return Constructor.class;
        } else {
            throw new IllegalArgumentException("Unsupported implementation class for Member, " + member.getClass());
        }
    }

    public static String getSimpleClassName(@NonNull Object obj) {
        return getSimpleClassName(obj.getClass());
    }

    public static String getSimpleClassName(@NonNull Class clazz) {
        return clazz.getSimpleName();
    }

    public static String getFQNClassName(@NonNull Class clazz) {
        Objects.requireNonNull(clazz, "Class must not be null");
        if (clazz.isArray()) {
            return getQualifiedNameForArray(clazz);
        } else {
            return clazz.getName();
        }
    }

    private static String getQualifiedNameForArray(Class<?> clazz) {
        StringBuilder result = new StringBuilder();
        while (clazz.isArray()) {
            clazz = clazz.getComponentType();
            result.append("[]");
        }
        result.insert(0, clazz.getName());
        return result.toString();
    }

    public static String getPackageName(@NonNull String classFullName) {
        /*
         * 类名A$类名B：类名A中的类名B
         *
         * 类名A$12：类名A中的匿名内部类，种类索引为12（类索引从1开始）
         *
         * 类名A$$Lambda$12：类名A中的Lambda表达式（类索引为12）
         */
        int index = classFullName.lastIndexOf('.');
        if (index != -1) {
            return classFullName.substring(0, index);
        }
        return "";
    }

    public static String getPackageName(@NonNull Class clazz) {
        Package pkg = clazz.getPackage();
        if (pkg != null) {
            return pkg.getName();
        }
        String className = getFQNClassName(clazz);
        return getPackageName(className);
    }

    public static String getJvmSignature(@NonNull Class clazz) {
        return TypeSignatures.toTypeSignature(getFQNClassName(clazz));
    }

    public static String getCodeLocationString(@NonNull Class clazz) {
        URL url = getCodeLocation(clazz);
        if (url == null) {
            return null;
        }
        return url.toString();
    }

    public static URL getCodeLocation(@NonNull Class clazz) {
        Objects.requireNonNull(clazz);
        if (TypeUtils.isArray(clazz)) {
            return getCodeLocation(clazz.getComponentType());
        }
        ProtectionDomain pd = clazz.getProtectionDomain();
        if (pd == null) {
            return null;
        }
        CodeSource codeSource = pd.getCodeSource();
        if (codeSource == null) {
            return null;
        }
        return codeSource.getLocation();
    }


    /**
     * Get the interfaces for the specified class.
     *
     * @param cls             the class to look up, may be {@code null}
     * @param interfacesFound the {@code Set} of interfaces for the class
     */
    private static void getAllInterfaces(@NonNull Class<?> cls, final HashSet<Class<?>> interfacesFound) {
        while (cls != null) {
            final Class<?>[] interfaces = cls.getInterfaces();

            for (final Class<?> i : interfaces) {
                if (interfacesFound.add(i)) {
                    getAllInterfaces(i, interfacesFound);
                }
            }

            cls = cls.getSuperclass();
        }
    }

    /**
     * Get an {@link Iterable} that can iterate over a class hierarchy in ascending (subclass to superclass) order,
     * excluding interfaces.
     *
     * @param type the type to get the class hierarchy from
     * @return Iterable an Iterable over the class hierarchy of the given class
     */
    public static Iterable<Class<?>> hierarchy(@NonNull final Class<?> type) {
        Objects.requireNonNull(type);
        return hierarchy(type, true);
    }

    /**
     * Get an {@link Iterable} that can iterate over a class hierarchy in ascending (subclass to superclass) order.
     *
     * @param type              the type to get the class hierarchy from
     * @param excludeInterfaces switch indicating whether to include or exclude interfaces
     * @return Iterable an Iterable over the class hierarchy of the given class
     */
    public static Iterable<Class<?>> hierarchy(@NonNull final Class<?> type, final boolean excludeInterfaces) {
        Objects.requireNonNull(type);
        final Iterable<Class<?>> classes = new Iterable<Class<?>>() {

            @Override
            public Iterator<Class<?>> iterator() {
                final Holder<Class<?>> next = new Holder<Class<?>>(type);
                return new Iterator<Class<?>>() {

                    @Override
                    public boolean hasNext() {
                        return next.get() != null;
                    }

                    @Override
                    public Class<?> next() {
                        final Class<?> result = next.get();
                        next.set(result.getSuperclass());
                        return result;
                    }

                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }

                };
            }

        };
        if (excludeInterfaces) {
            return classes;
        }
        return new Iterable<Class<?>>() {

            @Override
            public Iterator<Class<?>> iterator() {
                final Set<Class<?>> seenInterfaces = new HashSet<Class<?>>();
                final Iterator<Class<?>> wrapped = classes.iterator();

                return new Iterator<Class<?>>() {
                    Iterator<Class<?>> interfaces = new TreeSet<Class<?>>().iterator();

                    @Override
                    public boolean hasNext() {
                        return interfaces.hasNext() || wrapped.hasNext();
                    }

                    @Override
                    public Class<?> next() {
                        if (interfaces.hasNext()) {
                            final Class<?> nextInterface = interfaces.next();
                            seenInterfaces.add(nextInterface);
                            return nextInterface;
                        }
                        final Class<?> nextSuperclass = wrapped.next();
                        final Set<Class<?>> currentInterfaces = new LinkedHashSet<Class<?>>();
                        walkInterfaces(currentInterfaces, nextSuperclass);
                        interfaces = currentInterfaces.iterator();
                        return nextSuperclass;
                    }

                    private void walkInterfaces(final Set<Class<?>> addTo, final Class<?> c) {
                        for (final Class<?> iface : c.getInterfaces()) {
                            if (!seenInterfaces.contains(iface)) {
                                addTo.add(iface);
                            }
                            walkInterfaces(addTo, iface);
                        }
                    }

                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }

                };
            }
        };
    }


    /**
     * Check whether the given class is cache-safe in the given context,
     * i.e. whether it is loaded by the given ClassLoader or a parent of it.
     *
     * @param clazz       the class to analyze
     * @param classLoader the ClassLoader to potentially cache metadata in
     *                    (may be {@code null} which indicates the system class loader)
     */
    public static boolean isCacheSafe(Class<?> clazz, ClassLoader classLoader) {
        Objects.requireNonNull(clazz, "Class must not be null");
        try {
            ClassLoader target = clazz.getClassLoader();
            // Common cases
            if (target == classLoader || target == null) {
                return true;
            }
            if (classLoader == null) {
                return false;
            }
            // Check for match in ancestors -> positive
            ClassLoader current = classLoader;
            while (current != null) {
                current = current.getParent();
                if (current == target) {
                    return true;
                }
            }
            // Check for match in children -> negative
            while (target != null) {
                target = target.getParent();
                if (target == classLoader) {
                    return false;
                }
            }
        } catch (SecurityException ex) {
            // Fall through to Class reference comparison below
        }

        // Fallback for ClassLoaders without parent/child relationship:
        // safe if same Class can be loaded from given ClassLoader
        return (classLoader != null && isVisible(clazz, classLoader));
    }

    /**
     * Check whether the given class is visible in the given ClassLoader.
     *
     * @param clazz       the class to check (typically an interface)
     * @param classLoader the ClassLoader to check against
     *                    (may be {@code null} in which case this method will always return {@code true})
     */
    public static boolean isVisible(Class<?> clazz, ClassLoader classLoader) {
        if (classLoader == null) {
            return true;
        }
        try {
            return (clazz == classLoader.loadClass(clazz.getName()));
            // Else: different class with same name found
        } catch (ClassNotFoundException ex) {
            // No corresponding class found at all
            return false;
        }
    }

    /**
     * Returns true if an annotation for the specified type
     * is present on this element, else false.  This method
     * is designed primarily for convenient access to marker annotations.
     */
    public static boolean isAnnotationPresent(@NonNull AnnotatedElement annotatedElement, @NonNull Class<? extends Annotation> annotationClass) {
        return annotatedElement.isAnnotationPresent(annotationClass);
    }

    public static <E extends Annotation> boolean hasAnnotation(@NonNull AnnotatedElement annotatedElement, @NonNull Class<E> annotationClass) {
        return getAnnotation(annotatedElement, annotationClass) != null;
    }

    /**
     * Returns this element's annotation for the specified type if
     * such an annotation is present, else null.
     */
    public static <E extends Annotation> E getAnnotation(@NonNull AnnotatedElement annotatedElement, @NonNull Class<E> annotationClass) {
        return annotatedElement.getAnnotation(annotationClass);
    }

    public static <E extends Annotation> E getDeclaredAnnotation(@NonNull AnnotatedElement annotatedElement, @NonNull Class<E> annotationClass) {
        return getAnnotation(annotatedElement, annotationClass);
    }

    /**
     * Returns all annotations present on this element.  (Returns an array
     * of length zero if this element has no annotations.)  The caller of
     * this method is free to modify the returned array; it will have no
     * effect on the arrays returned to other callers.
     */
    public static Annotation[] getAnnotations(@NonNull AnnotatedElement annotatedElement) {
        return annotatedElement.getAnnotations();
    }

    /**
     * Returns all annotations that are directly present on this
     * element.  Unlike the other methods in this interface, this method
     * ignores inherited annotations.  (Returns an array of length zero if
     * no annotations are directly present on this element.)  The caller of
     * this method is free to modify the returned array; it will have no
     * effect on the arrays returned to other callers.
     */
    public static List<Annotation> getDeclaredAnnotations(@NonNull AnnotatedElement annotatedElement) {
        return Lists.asList(annotatedElement.getDeclaredAnnotations());
    }

    public static Field getStaticField(@NonNull Class clazz, @NonNull String fieldName) {
        Field field = getDeclaredField(clazz, fieldName);
        if (field == null) {
            return null;
        }
        if (Modifiers.isStatic(field)) {
            return field;
        }
        return null;
    }

    public static Field getPublicField(@NonNull Class clazz, @NonNull String fieldName) {
        Field field = null;
        try {
            field = clazz.getField(fieldName);
        } catch (NoSuchFieldException e) {
            // ignore it
        }
        return field;
    }

    public static Field getDeclaredField(@NonNull Class clazz, @NonNull final String fieldName) {
        List<Field> fields = getDeclaredFields(clazz);
        return fields.stream().filter(field -> ObjectUtils.equals(field.getName(), fieldName)).findFirst().orElse(null);
    }

    public static Field getAnyField(@NonNull Class clazz, @NonNull String fieldName) {
        return findField(clazz, fieldName);
    }

    /**
     * Attempt to find a {@link Field field} on the supplied {@link Class} with the
     * supplied {@code name}. Searches all superclasses up to {@link Object}.
     *
     * @param clazz the class to introspect
     * @param name  the name of the field
     * @return the corresponding Field object, or {@code null} if not found
     */
    @Nullable
    public static Field findField(Class<?> clazz, String name) {
        return findField(clazz, name, null);
    }

    /**
     * Attempt to find a {@link Field field} on the supplied {@link Class} with the
     * supplied {@code name} and/or {@link Class type}. Searches all superclasses
     * up to {@link Object}.
     *
     * @param clazz     the class to introspect
     * @param name      the name of the field (may be {@code null} if type is specified)
     * @param fieldType the type of the field (may be {@code null} if name is specified)
     * @return the corresponding Field object, or {@code null} if not found
     */
    @Nullable
    public static Field findField(Class<?> clazz, @Nullable String name, @Nullable Class<?> fieldType) {
        Objects.requireNonNull(clazz, "Class must not be null");
        Assert.isTrue(name != null || fieldType != null, "Either name or type of the field must be specified");
        Class<?> searchType = clazz;
        while (Object.class != searchType && searchType != null) {
            List<Field> fields = getDeclaredFields(searchType);
            for (Field field : fields) {
                if ((name == null || name.equals(field.getName())) && (fieldType == null || fieldType.equals(field.getType()))) {
                    return field;
                }
            }
            searchType = searchType.getSuperclass();
        }
        return null;
    }

    public static Collection<Field> findAllFields(@NonNull Class<?> clazz, boolean containsStatic) {
        Collection<Field> fields = new ArrayList<>();
        findAllFields(fields, clazz, containsStatic);
        return fields;
    }

    private static void findAllFields(Collection<Field> result, @NonNull Class<?> clazz, boolean containsStatic) {
        if (clazz.isInterface()) {
            return;
        }
        Collection<Field> declaredFields = getAllDeclaredFields(clazz, containsStatic);
        result.addAll(declaredFields);
        Class<?> superClass = clazz.getSuperclass();
        if (superClass != null && Object.class != superClass) {
            findAllFields(result, superClass, containsStatic);
        }
    }

    public static Collection<Field> getAllDeclaredFields(@NonNull Class<?> clazz) {
        return getAllDeclaredFields(clazz, false);
    }

    public static Collection<Field> getAllDeclaredFields(@NonNull Class<?> clazz, boolean containsStatic) {
        List<Field> fields = getDeclaredFields(clazz);
        return !containsStatic ? filterFields(fields, Modifier.STATIC) : filterFields(fields);
    }


    public static Collection<Field> getAllPublicInstanceFields(@NonNull Class<?> clazz) {
        return getAllPublicFields(clazz, false);
    }

    public static Collection<Field> getAllPublicFields(@NonNull Class<?> clazz, boolean containsStatic) {
        List<Field> fields = Arrays.stream(clazz.getFields()).collect(Collectors.toList());
        return !containsStatic ? filterFields(fields, Modifier.STATIC) : filterFields(fields);
    }

    public static Collection<Field> filterFields(@NonNull List<Field> fields, final int... excludedModifiers) {
        final List<Integer> excludedModifierList = Lists.asList(PrimitiveArrays.wrap(excludedModifiers, false));
        if (excludedModifierList.isEmpty()) {
            return Lists.asList(fields);
        }
        return CollectionUtils.filter(fields, field -> CollectionUtils.noneMatch(excludedModifierList, modifier -> Modifiers.hasModifier(field, modifier)));
    }

    public static <V> V getPublicFieldValueForcedIfPresent(@NonNull Object object, @NonNull String fieldName) {
        return getPublicFieldValue(object, fieldName, false);
    }

    @SuppressWarnings("unchecked")
    public static <V> V getPublicFieldValue(@NonNull Object object, @NonNull String fieldName, boolean throwException) {
        try {
            Field field = getPublicField(object.getClass(), fieldName);
            if (field == null) {
                if (throwException) {
                    throw new IllegalArgumentException(new ExceptionMessage("Can't find public field {0} in the class {1}", fieldName, object.getClass().getCanonicalName()).getMessage());
                }
                return null;
            } else {
                return (V) field.get(object);
            }
        } catch (Throwable ex) {
            if (throwException) {
                throw ExceptionUtils.wrapAsRuntimeException(ex);
            } else {
                return null;
            }
        }
    }

    public static <V> V getDeclaredFieldValueForcedIfPresent(@NonNull Object object, @NonNull String fieldName) {
        return getDeclaredFieldValue(object, fieldName, false);
    }

    public static <V> V getDeclaredFieldValue(@NonNull Object object, String fieldName, boolean throwException) {
        try {
            Field field = getDeclaredField(object.getClass(), fieldName);
            if (field == null) {
                if (throwException) {
                    throw new IllegalArgumentException(new ExceptionMessage("Can't find a declared field {0} in the class {1}", fieldName, object.getClass().getCanonicalName()).getMessage());
                }
                return null;
            } else {
                return getFieldValue(field, object, throwException);
            }
        } catch (Throwable ex) {
            throw ExceptionUtils.wrapAsRuntimeException(ex);
        }
    }

    public static <V> V getAnyFieldValueForcedIfPresent(@NonNull Object object, @NonNull String fieldName) {
        return getAnyFieldValue(object, fieldName, false);
    }

    public static <V> V getAnyFieldValue(@NonNull Object object, @NonNull String fieldName, boolean throwException) {
        try {
            Field field = getAnyField(object.getClass(), fieldName);
            if (field == null) {
                if (throwException) {
                    throw new IllegalArgumentException(new ExceptionMessage("Can't find a declared field {0} in the class {1} and its all super class", fieldName, object.getClass().getCanonicalName()).getMessage());
                }
                return null;
            } else {
                return getFieldValue(field, object, throwException);
            }
        } catch (Throwable ex) {
            if (throwException) {
                throw ExceptionUtils.wrapAsRuntimeException(ex);
            } else {
                return null;
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static <V> V getFieldValue(@NonNull Field field, @NonNull Object object, boolean throwException) {
        try {
            // accessible
            if (!field.isAccessible()) {
                // unaccessible && force
                makeAccessible(field);
            }
            return (V) field.get(object);
        } catch (Throwable ex) {
            if (throwException) {
                throw ExceptionUtils.wrapAsRuntimeException(ex);
            } else {
                return null;
            }
        }
    }


    public static void setPublicFieldValue(@NonNull Object object, @NonNull String fieldName, Object value, boolean throwException) {
        try {
            Field field = getPublicField(object.getClass(), fieldName);
            if (field == null) {
                if (throwException) {
                    throw new IllegalArgumentException(new ExceptionMessage("Can't find a declared field {0} in the class {1} and its all super class", fieldName, object.getClass().getCanonicalName()).getMessage());
                }
            } else {
                setFieldValue(object, field, value);
            }
        } catch (Throwable ex) {
            if (throwException) {
                throw ExceptionUtils.wrapAsRuntimeException(ex);
            }
        }
    }

    public static void setDeclaredFieldValue(@NonNull Object object, @NonNull String fieldName, Object value, boolean throwException) {
        try {
            Field field = getDeclaredField(object.getClass(), fieldName);
            if (field == null) {
                if (throwException) {
                    throw new IllegalArgumentException(new ExceptionMessage("Can't find a declared field {0} in the class {1} and its all super class", fieldName, object.getClass().getCanonicalName()).getMessage());
                }
            } else {
                setFieldValue(object, field, value);
            }
        } catch (Throwable ex) {
            if (throwException) {
                throw ExceptionUtils.wrapAsRuntimeException(ex);
            }
        }
    }

    public static void setAnyFieldValue(@NonNull Object object, @NonNull String fieldName, Object value, boolean throwException) {
        try {


            Field field = getAnyField(object.getClass(), fieldName);
            if (field == null) {
                if (throwException) {
                    throw new IllegalArgumentException(new ExceptionMessage("Can't find a declared field {0} in the class {1} and its all super class", fieldName, object.getClass().getCanonicalName()).getMessage());
                }
            } else {
                setFieldValue(object, field, value);
            }
        } catch (Throwable ex) {
            if (throwException) {
                throw ExceptionUtils.wrapAsRuntimeException(ex);
            }
        }
    }

    public static <E> boolean hasConstructor(@NonNull Class<E> clazz, Class... parameterTypes) {
        return getConstructor(clazz, parameterTypes) != null;
    }


    public static <E> E newInstance(@NonNull Class<E> clazz) {
        Objects.requireNonNull(clazz);
        try {
            return clazz.newInstance();
        } catch (Throwable ex) {
            Logger logger = Loggers.getLogger(ReflectUtils.class);
            if (logger.isDebugEnabled()) {
                logger.debug("Create {} instance fail", getFQNClassName(clazz), ex);
            }
            return null;
        }
    }

    public static <E> E newInstance(@NonNull Class<E> clazz, @Nullable Class[] parameterTypes, @NonNull Object... parameters) {
        Objects.requireNonNull(clazz);
        Constructor<E> constructor = getConstructor(clazz, parameterTypes);
        if (constructor != null) {
            return newInstance(constructor, parameters);
        }
        return null;
    }

    public static List<Method> getAnnotatedMethods(final Class<?> type, final Class<? extends Annotation> annotation) {
        final List<Method> methods = new ArrayList<Method>();
        Class<?> clazz = type;
        while (!Object.class.equals(clazz)) {
            Method[] currentClassMethods = getDeclaredMethods(clazz, true);
            for (final Method method : currentClassMethods) {
                if (annotation == null || method.isAnnotationPresent(annotation)) {
                    methods.add(method);
                }
            }
            // move to the upper class in the hierarchy in search for more methods
            clazz = clazz.getSuperclass();
        }
        return methods;
    }

    public static Collection<Method> findGetterOrSetter(Class clazz) {
        return findGetterOrSetter(clazz, true);
    }

    public static Collection<Method> findGetterOrSetter(Class clazz, final boolean checkFieldExists) {
        Collection<Method> methods = findMethods(clazz);
        return StreamUtils.of(methods)
                .filter(new Predicate<Method>() {
                    @Override
                    public boolean test(Method value) {
                        return isGetterOrSetter(value, checkFieldExists);
                    }
                }).collect(Collectors.toList());
    }


    public static Collection<Method> findMethods(Class clazz) {
        return findMethods(clazz, false);
    }

    public static Collection<Method> findMethods(Class clazz, boolean containsStatic) {
        Collection<Method> result = new ArrayList<Method>();
        findMethods(result, clazz, containsStatic);
        return result;
    }

    public static void findMethods(Collection<Method> result, Class clazz, boolean containsStatic) {
        if (clazz == null || clazz == Object.class) {
            return;
        }
        Collection<Method> methods = getAllDeclaredMethods(clazz, containsStatic);
        result.addAll(methods);
        findMethods(result, clazz.getSuperclass(), containsStatic);
    }


    public static Collection<Method> getAllDeclaredMethods(@NonNull Class clazz) {
        return getAllDeclaredMethods(clazz, false);
    }

    public static Collection<Method> getAllDeclaredMethods(@NonNull Class clazz, boolean containsStatic) {
        Method[] methods = getDeclaredMethods(clazz, true);
        return !containsStatic ? filterMethods(methods, Modifier.STATIC) : filterMethods(methods);
    }

    public static Collection<Method> filterMethods(@NonNull Method[] methods, final int... excludedModifiers) {
        final List<Integer> excludedModifierList = Lists.asList(PrimitiveArrays.wrap(excludedModifiers, false));
        if (excludedModifierList.isEmpty()) {
            return Lists.asList(methods);
        }
        return CollectionUtils.filter(methods, method -> CollectionUtils.noneMatch(excludedModifierList, modifier -> Modifiers.hasModifier(method, modifier)));
    }


    public static Method getDeclaredMethod(@NonNull Class clazz, @NonNull final String methodName, final Class... parameterTypes) {

        Method[] methods = getDeclaredMethods(clazz, true);
        Method method = StreamUtils.of(methods).filter(method1 -> {
            if (!ObjectUtils.equals(method1.getName(), methodName)) {
                return false;
            }
            Class<?>[] pts = method1.getParameterTypes();
            if (pts.length == 0 && (parameterTypes == null || parameterTypes.length == 0)) {
                return true;
            }
            if (pts.length != parameterTypes.length) {
                return false;
            }
            return ObjectUtils.equals(pts, parameterTypes);
        }).findFirst().orElse(null);
        return method;
    }


    /**
     * Determine whether the given class has a public method with the given signature,
     * and return it if available (else return {@code null}).
     * <p>In case of any signature specified, only returns the method if there is a
     * unique candidate, i.e. a single public method with the specified name.
     * <p>Essentially translates {@code NoSuchMethodException} to {@code null}.
     *
     * @param clazz      the clazz to analyze
     * @param methodName the name of the method
     * @param paramTypes the parameter types of the method
     *                   (may be {@code null} to indicate any signature)
     * @return the method, or {@code null} if not found
     * @see Class#getMethod
     */
    public static Method getMethodIfAvailable(Class<?> clazz, String methodName, Class<?>... paramTypes) {
        Objects.requireNonNull(clazz, "Class must not be null");
        Objects.requireNonNull(methodName, "Method name must not be null");
        if (paramTypes != null) {
            try {
                return clazz.getMethod(methodName, paramTypes);
            } catch (NoSuchMethodException ex) {
                return null;
            }
        } else {
            Set<Method> candidates = new HashSet<>(1);
            Method[] methods = clazz.getMethods();
            for (Method method : methods) {
                if (methodName.equals(method.getName())) {
                    candidates.add(method);
                }
            }
            if (candidates.size() == 1) {
                return candidates.iterator().next();
            }
            return null;
        }
    }

    public static Method getAnyMethod(@NonNull Class<?> clazz, @NonNull String methodName, Class<?>... parameterTypes) {
        return findMethod(clazz, methodName, parameterTypes);
    }

    public static Method findMethod(Class<?> clazz, String name, @Nullable Class<?>... paramTypes) {
        Objects.requireNonNull(clazz, "Class must not be null");
        Objects.requireNonNull(name, "Method name must not be null");
        Class<?> searchType = clazz;
        while (searchType != null) {
            Method[] methods = (searchType.isInterface() ? searchType.getMethods() : getDeclaredMethods(searchType, false));
            for (Method method : methods) {
                if (name.equals(method.getName()) && (paramTypes == null || hasSameParams(method, paramTypes))) {
                    return method;
                }
            }
            searchType = searchType.getSuperclass();
        }
        return null;
    }

    private static boolean hasSameParams(Method method, Class<?>[] paramTypes) {
        return (paramTypes.length == method.getParameterTypes().length &&
                Arrays.equals(paramTypes, method.getParameterTypes()));
    }

    /**
     * @param defensive boolean
     *                  是否返回可保护的值，因为是从 cache 中获取的，为了保护缓存值不被改变，通常 传值为 true
     */
    private static Method[] getDeclaredMethods(Class<?> clazz, boolean defensive) {
        Objects.requireNonNull(clazz, "Class must not be null");
        Method[] result = DECLARED_METHODS_CACHE.get(clazz);
        if (result == null) {
            try {
                Method[] declaredMethods = clazz.getDeclaredMethods();
                List<Method> defaultMethods = findConcreteMethodsOnInterfaces(clazz);
                if (defaultMethods != null) {
                    result = new Method[declaredMethods.length + defaultMethods.size()];
                    System.arraycopy(declaredMethods, 0, result, 0, declaredMethods.length);
                    int index = declaredMethods.length;
                    for (Method defaultMethod : defaultMethods) {
                        result[index] = defaultMethod;
                        index++;
                    }
                } else {
                    result = declaredMethods;
                }
                DECLARED_METHODS_CACHE.put(clazz, (result.length == 0 ? EMPTY_METHOD_ARRAY : result));
            } catch (Throwable ex) {
                throw new IllegalStateException("Failed to introspect Class [" + clazz.getName() +
                        "] from ClassLoader [" + clazz.getClassLoader() + "]", ex);
            }
        }
        return (result.length == 0 || !defensive) ? result : result.clone();
    }

    @Nullable
    private static List<Method> findConcreteMethodsOnInterfaces(Class<?> clazz) {
        List<Method> result = null;
        for (Class<?> ifc : clazz.getInterfaces()) {
            for (Method ifcMethod : ifc.getMethods()) {
                if (!Modifier.isAbstract(ifcMethod.getModifiers())) {
                    if (result == null) {
                        result = new ArrayList<Method>();
                    }
                    result.add(ifcMethod);
                }
            }
        }
        return result;
    }

    public static <V> V invokePublicMethodForcedIfPresent(@NonNull Object object, @NonNull String methodName, @Nullable Class[] parameterTypes, @Nullable Object[] parameters) {
        return invokePublicMethod(object, methodName, parameterTypes, parameters, false);
    }

    public static <V> V invokePublicMethod(@NonNull Object object, @NonNull String methodName, @Nullable Class[] parameterTypes, @Nullable Object[] parameters, boolean throwException) {
        try {
            Method method = getPublicMethod(object.getClass(), methodName, parameterTypes);
            if (method == null) {
                if (throwException) {
                    throw new IllegalArgumentException(new ExceptionMessage("Can't find the method: {0}", getMethodString(getFQNClassName(object.getClass()), methodName, null, parameterTypes)).getMessage());
                }
                return null;
            }
            return invoke(method, object, parameters, throwException);
        } catch (Throwable ex) {
            if (throwException) {
                throw ExceptionUtils.wrapAsRuntimeException(ex);
            } else {
                return null;
            }
        }
    }

    public static <V> V invokeDeclaredMethodForcedIfPresent(@NonNull Object object, @NonNull String methodName, @Nullable Class[] parameterTypes, @Nullable Object[] parameters) {
        return invokeDeclaredMethod(object, methodName, parameterTypes, parameters, false);
    }

    public static <V> V invokeDeclaredMethod(@NonNull Object object, @NonNull String methodName, @Nullable Class[] parameterTypes, @Nullable Object[] parameters, boolean throwException) {
        try {
            Method method = getDeclaredMethod(object.getClass(), methodName, parameterTypes);
            if (method == null) {
                if (throwException) {
                    throw new IllegalArgumentException(new ExceptionMessage("Can't find the method: {0}", getMethodString(getFQNClassName(object.getClass()), methodName, null, parameterTypes)).getMessage());
                }
                return null;
            }
            return invoke(method, object, parameters, throwException);
        } catch (Throwable ex) {
            if (throwException) {
                throw ExceptionUtils.wrapAsRuntimeException(ex);
            } else {
                return null;
            }
        }
    }

    public static <V> V invokeAnyMethodForcedIfPresent(@NonNull Object object, @NonNull String methodName, @Nullable Class[] parameterTypes, @Nullable Object[] parameters) {
        return invokeAnyMethod(object, methodName, parameterTypes, parameters, false);
    }

    public static <V> V invokeAnyMethod(@NonNull Object object, @NonNull String methodName, @Nullable Class[] parameterTypes, @Nullable Object[] parameters, boolean throwException) {
        try {
            Method method = getAnyMethod(object.getClass(), methodName, parameterTypes);
            if (method == null) {
                if (throwException) {
                    throw new NoSuchMethodException(new ExceptionMessage("Can't find the method: {0}", getMethodString(getFQNClassName(object.getClass()), methodName, null, parameterTypes)).getMessage());
                }
                return null;
            }
            return invoke(method, object, parameters, throwException);
        } catch (Throwable ex) {
            if (throwException) {
                throw ExceptionUtils.wrapAsRuntimeException(ex);
            } else {
                return null;
            }
        }
    }

    public static <V> V invokeMethod(@NonNull Method method, @NonNull Object object, @Nullable Object... parameters) {
        return invoke(method, object, parameters, true);
    }

    public static <V> V invoke(@NonNull Method method, @NonNull Object object, @Nullable Object[] parameters, boolean throwException) {
        try {
            if (method.isAccessible()) {
                return invokeMethodOrNull(method, object, parameters, throwException);
            }

            // force && unaccessible
            makeAccessible(method);
            return invokeMethodOrNull(method, object, parameters, throwException);
        } catch (Throwable ex) {
            throw ExceptionUtils.wrapAsRuntimeException(ex);
        }
    }

    public static <V> V invokeGetterOrFiled(Object object, String field, boolean throwException) {
        Objects.requireNonNull(object, "the object is null");
        Valid.notNull(field, "the field name is null or empty");
        Method method = getGetter(object.getClass(), field);
        if (method != null) {
            return invoke(method, object, new Object[0], throwException);
        }
        return getAnyFieldValue(object, field, throwException);
    }

    @SuppressWarnings("unchecked")
    private static <V> V invokeMethodOrNull(@NonNull Method method, @NonNull Object object, @Nullable Object[] parameters, boolean throwException) throws IllegalAccessException, InvocationTargetException {
        try {
            return (V) method.invoke(object, parameters);
        } catch (IllegalAccessException | InvocationTargetException ex) {
            if (throwException) {
                throw ExceptionUtils.wrapAsRuntimeException(ex);
            }
            return null;
        }
    }

    public static <V> V invokeAnyStaticMethod(String clazz, String methodName, Class[] parameterTypes, Object[] parameters, boolean force, boolean throwException) throws ClassNotFoundException {
        return invokeAnyStaticMethod(Class.forName(clazz), methodName, parameterTypes, parameters, force, throwException);
    }

    public static <V> V invokeAnyStaticMethod(Class clazz, String methodName, Class[] parameterTypes, Object[] parameters, boolean force, boolean throwException) {
        try {
            Method method = getAnyMethod(clazz, methodName, parameterTypes);
            if (method == null) {
                throw new NoSuchMethodException();
            }
            if (Modifiers.isStatic(method)) {
                return invoke(method, null, parameters, throwException);
            }
        } catch (Throwable ex) {
            throw ExceptionUtils.wrapAsRuntimeException(ex);
        }
        return null;
    }

    public static String getMethodString(@Nullable String clazzFQN,
                                         @NonNull String methodName,
                                         @Nullable Class returnType,
                                         @Nullable Class[] parameterTypes) {
        try {
            StringBuilder sb = new StringBuilder();
            if (returnType != null) {
                sb.append(getTypeName(returnType)).append(" ");
            }
            if (StringUtils.isNotBlank(clazzFQN)) {
                sb.append(clazzFQN).append(".");
            }
            sb.append(methodName).append("(");
            if (!Emptys.isEmpty(parameterTypes)) {
                // avoid clone
                Class[] params = parameterTypes;
                for (int j = 0; j < params.length; j++) {
                    sb.append(getTypeName(params[j]));
                    if (j < (params.length - 1)) {
                        sb.append(",");
                    }
                }
            }
            sb.append(")");
            return sb.toString();
        } catch (Exception e) {
            return "<" + e + ">";
        }
    }


    public static MethodParameter getMethodParameter(String supplierName, Method method, int index) {
        return PARAMETER_SERVICE_REGISTRY.getMethodParameter(supplierName, method, index);
    }

    public static MethodParameter getMethodParameter(Method method, int index) {
        return PARAMETER_SERVICE_REGISTRY.getMethodParameter(method, index);
    }

    public static List<MethodParameter> getMethodParameters(String supplierName, Method method) {
        return PARAMETER_SERVICE_REGISTRY.getMethodParameters(supplierName, method);
    }

    public static List<MethodParameter> getMethodParameters(Method method) {
        return PARAMETER_SERVICE_REGISTRY.getMethodParameters(method);
    }

    public static ConstructorParameter getConstructorParameter(String supplierName, Constructor constructor, int index) {
        return PARAMETER_SERVICE_REGISTRY.getConstructorParameter(supplierName, constructor, index);
    }

    public static ConstructorParameter getConstructorParameter(Constructor constructor, int index) {
        return PARAMETER_SERVICE_REGISTRY.getConstructorParameter(constructor, index);
    }

    public static List<ConstructorParameter> getConstructorParameters(Constructor constructor) {
        return PARAMETER_SERVICE_REGISTRY.getConstructorParameters(constructor);
    }

    public static List<ConstructorParameter> getConstructorParameters(String supplierName, Constructor constructor) {
        return PARAMETER_SERVICE_REGISTRY.getConstructorParameters(supplierName, constructor);
    }

    public static Set<Class> getAllInterfaces(Class clazz) {
        final Set<Class> set = new LinkedHashSet<>();
        Class[] interfaces = clazz.getInterfaces();
        if (interfaces.length > 0) {
            CollectionUtils.addAll(set, interfaces);
            CollectionUtils.forEach(interfaces, new BiConsumer<Integer, Class>() {
                @Override
                public void accept(Integer index, Class iface) {
                    set.addAll(getAllInterfaces(iface));
                }
            });
        }
        return set;
    }

    public static Set<Class> getAllSuperClass(Class clazz) {
        final Set<Class> set = new LinkedHashSet<>();
        Class superClass = clazz.getSuperclass();
        if (superClass != null) {
            set.add(superClass);
            set.addAll(getAllInterfaces(superClass));
        }
        return set;
    }

    /**
     * This new method 'slightly' outperforms the old method, it was
     * essentially a perfect example of me wasting my time and a
     * premature optimization.  But what the hell...
     *
     * @param s -
     * @return String
     */
    public static String getSetter(String s) {
        char[] chars = new char[s.length() + 3];

        chars[0] = 's';
        chars[1] = 'e';
        chars[2] = 't';

        chars[3] = CharUtils.toUpperCase(s.charAt(0));

        for (int i = s.length() - 1; i != 0; i--) {
            chars[i + 3] = s.charAt(i);
        }

        return new String(chars);
    }


    public static String getGetter(String s) {
        char[] c = s.toCharArray();
        char[] chars = new char[c.length + 3];

        chars[0] = 'g';
        chars[1] = 'e';
        chars[2] = 't';

        chars[3] = CharUtils.toUpperCase(c[0]);

        arraycopy(c, 1, chars, 4, c.length - 1);

        return new String(chars);
    }


    public static String getIsGetter(String s) {
        char[] c = s.toCharArray();
        char[] chars = new char[c.length + 2];

        chars[0] = 'i';
        chars[1] = 's';

        chars[2] = CharUtils.toUpperCase(c[0]);

        arraycopy(c, 1, chars, 3, c.length - 1);

        return new String(chars);
    }

    /**
     *
     */
    public static String getIsSetter(String s) {
        String ret = s;
        if (!StringUtils.startsWith(s, "is")) {
            ret = getIsGetter(s);
        }
        char[] c = ret.toCharArray();
        char[] chars = new char[c.length + 3];

        chars[0] = 's';
        chars[1] = 'e';
        chars[2] = 't';
        chars[3] = CharUtils.toUpperCase(c[0]);

        arraycopy(c, 1, chars, 4, c.length - 1);

        return new String(chars);
    }

    public static Method getSetter(Class clazz, String field) {
        String setter = getSetter(field);

        for (Method method : clazz.getMethods()) {
            if (setter.equals(method.getName()) && Modifiers.isPublic(method) && method.getParameterTypes().length == 1) {
                return method;
            }
        }
        return null;
    }

    public static Method getSetter(Class clazz, String field, Class parameterType) {
        String setter = getSetter(field);
        Method method = getDeclaredMethod(clazz, setter, parameterType);
        if (method != null && Modifiers.isPublic(method)) {
            return method;
        }
        return null;
    }

    public static boolean hasGetter(Field field) {
        Method method = getGetter(field.getDeclaringClass(), field.getName());
        return method != null && field.getType().isAssignableFrom(method.getReturnType());
    }

    public static boolean hasSetter(Field field) {
        Method method = getSetter(field.getDeclaringClass(), field.getName());
        return method != null && field.getType().isAssignableFrom(method.getParameterTypes()[0]);
    }

    /**
     * 找到 public 的, 非 static 的 Getter
     */
    public static Method getGetter(Class clazz, String field) {
        String simple = "get" + field;
        String simpleIsGet = "is" + field;
        String isGet = getIsGetter(field);
        String getter = getGetter(field);

        Method candidate = null;

        if (Collection.class.isAssignableFrom(clazz) && "isEmpty".equals(isGet)) {
            try {
                return Collection.class.getMethod("isEmpty");
            } catch (NoSuchMethodException ignore) {
                // ignore it
            }
        }

        for (Method method : clazz.getMethods()) {
            if (isGetter(method) && candidate == null) {
                String methodName = method.getName();
                candidate = method;
            }
        }
        return candidate;
    }

    public static String extractFieldName(Member member) {
        if (member instanceof Field) {
            return member.getName();
        }
        if (member instanceof Method) {
            return extractFieldName((Method) member);
        }
        return null;
    }

    public static String extractFieldName(Method method) {
        if (isGetterOrSetter(method)) {
            String methodName = method.getName();
            String fieldName = null;
            if (methodName.startsWith("set") || methodName.startsWith("get")) {
                fieldName = methodName.substring(3);
            } else if (methodName.startsWith("is")) {
                fieldName = methodName.substring(2);
            }
            if (fieldName == null) {
                fieldName = methodName;
            }
            return CharUtils.toLowerCase(fieldName.charAt(0)) + (fieldName.length() > 1 ? fieldName.substring(1) : "");
        }
        return null;
    }

    public static boolean isSetter(@NonNull Method method) {
        if (isGetterOrSetter(method)) {
            String methodName = method.getName();
            return methodName.startsWith("set");
        }
        return false;
    }

    public static boolean isGetter(@NonNull Method method) {
        if (isGetterOrSetter(method)) {
            String methodName = method.getName();
            return (methodName.startsWith("get") || methodName.startsWith("is")) && method.getReturnType() == boolean.class;
        }
        return false;
    }


    public static boolean makeAccessible(@NonNull Field field) {
        return makeAccessibleMember(field);
    }

    /**
     *
     */
    public static boolean makeAccessible(@NonNull Method m) {
        return makeAccessibleMember(m);
    }

    /**
     *
     */
    public static boolean makeAccessible(@NonNull Constructor c) {
        return makeAccessibleMember(c);
    }

    /**
     *
     */
    public static boolean makeAccessibleMember(@NonNull Member c) {
        if ((!Modifiers.isPublic(c) || !Modifiers.isPublic(c.getDeclaringClass()) || !Modifiers.isFinal(c)) && (c instanceof AccessibleObject) && !((AccessibleObject) c).isAccessible()) {
            try {
                ((AccessibleObject) c).setAccessible(true);
                return true;
            } catch (SecurityException ex) {
                return false;
            }
        } else {
            return true;
        }
    }


    /**
     * Determine whether the given method is a "hashCode" method.
     *
     * @see Object#hashCode()
     */
    public static boolean isHashCodeMethod(@Nullable Method method) {
        return (method != null && "hashCode".equals(method.getName()) && !method.isVarArgs() && method.getParameterTypes().length == 0);
    }

    /**
     * Determine whether the given method is a "toString" method.
     *
     * @see Object#toString()
     */
    public static boolean isToStringMethod(@Nullable Method method) {
        return (method != null && "toString".equals(method.getName()) && !method.isVarArgs() && method.getParameterTypes().length == 0);
    }

    /**
     * Determine whether the given method is originally declared by {@link Object}.
     */
    public static boolean isObjectMethod(@Nullable Method method) {
        return (method != null && (method.getDeclaringClass() == Object.class || isEqualsMethod(method) || isHashCodeMethod(method) || isToStringMethod(method)));
    }


    /**
     * Determine if the given class defines an {@link Object#equals} override.
     *
     * @param clazz The class to check
     * @return True if clazz defines an equals override.
     */
    public static boolean isOverrideEquals(Class clazz) {
        Method equals = getDeclaredMethod(clazz, "equals", Object.class);
        return !OBJECT_EQUALS.equals(equals);
    }

    /**
     * Determine if the given class defines a {@link Object#hashCode} override.
     *
     * @param clazz The class to check
     * @return True if clazz defines an hashCode override.
     */
    public static boolean isOverrideHashCode(Class clazz) {
        Method hashCode = getDeclaredMethod(clazz, "hashCode");
        return !OBJECT_HASHCODE.equals(hashCode);
    }


    @SuppressWarnings("unchecked")
    public static <E> Class<E> getComponentType(E[] array) {
        Objects.requireNonNull(array);
        Class<?> clazz = array.getClass();
        return (Class<E>) clazz.getComponentType();
    }

}
