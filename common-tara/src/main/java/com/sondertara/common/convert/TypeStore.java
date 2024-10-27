package com.sondertara.common.convert;

import com.sondertara.common.base.CloneSupport;
import com.sondertara.common.text.StringUtils;
import com.sondertara.common.base.Valid;
import com.sondertara.common.collection.ArrayUtils;
import com.sondertara.common.collection.Sets;
import com.sondertara.common.concurrent.MultiConcurrentHashMap;
import com.sondertara.common.function.TypeConverter;
import com.sondertara.common.reflect.ClassIterator;
import com.sondertara.common.reflect.ClassUtils;
import lombok.Getter;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.Enumeration;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 类型转换容器
 * 自动转换基本类型为包装类型
 * 支持子父类转换 但是不支持接口转换
 *
 * @author Jiahang Li
 * @version 1.0.0
 *  */
@Getter
@SuppressWarnings({"unchecked"})
public class TypeStore extends CloneSupport<TypeStore> implements Serializable {

    private static final long serialVersionUID = 12038041239487192L;

    public static final TypeStore STORE = new TypeStore();

    /**
     * -- GETTER --
     * 获取映射转换器
     *
     * @return 转换器列表
     */
    private final MultiConcurrentHashMap<Class<?>, Type, TypeConverter<?>> conversionMapping;

    public TypeStore() {
        this.conversionMapping = new MultiConcurrentHashMap<>();
    }

    static {
        // 装载基础Mapper
        new BasicTypeStoreProvider(STORE);
    }

    /**
     * 注册转换器
     *
     * @param source     源class
     * @param target     目标class
     * @param conversion 转换器
     * @param <T>        T
     * @param <R>        R
     */
    public <T, R> void register(Class<T> source, Class<R> target, TypeConverter<?> conversion) {
        conversionMapping.put(source, target, conversion);
    }

    /**
     * 获取对象转换器
     *
     * @param source 源class
     * @param target 目标class
     * @param <T>    T
     * @param <R>    R
     * @return 转换器
     */
    public <T, R> TypeConverter<R> get(Class<T> source, Type target) {
        return (TypeConverter<R>) conversionMapping.get(source, target);
    }

    /**
     * 转换
     *
     * @param t           T
     * @param targetClass targetClass
     * @param <R>         target
     * @return R
     */
    public <T, R> R to(T t, Class<R> targetClass) {
        Valid.notNull(t, "convert target object is null");
        Class<?> sourceClass = t.getClass();
        targetClass = (Class<R>) ClassUtils.getWrapClass(targetClass);
        // 检查是否可以直接转换
        if (canDirectConvert(sourceClass, targetClass, false)) {
            return (R) t;
        }
        // 获取类转换器
        TypeConverter<R> conversion = this.get(sourceClass, targetClass);
        if (conversion != null) {
            return conversion.apply(t);
        }
        // 获取父类转换器
        for (Class<?> sourceParentClass : new ClassIterator<>(sourceClass)) {
            conversion = this.get(sourceParentClass, targetClass);
            if (conversion != null) {
                return conversion.apply(t);
            }
        }
        // 检查是否是数组
        if (!ClassUtils.isArray(targetClass)) {
            throw new IllegalArgumentException(StringUtils.format("unable to convert source [{}] class to target [{}] class", sourceClass, targetClass));
        }
        // 如果不是基本类型的数组则无法转换
        Class<?> baseArrayClass = ClassUtils.getBaseArrayClass(targetClass);
        if (baseArrayClass.equals(targetClass)) {
            throw new IllegalArgumentException(StringUtils.format("unable to convert source [{}] class to target [{}] class", sourceClass, targetClass));
        }
        // 如果 targetClass 是 sourceClass 的包装类数组则直接包装
        if (sourceClass.equals(baseArrayClass)) {
            return (R) ArrayUtils.wrap(t);
        }
        // 尝试使用 targetClass 的基本类型数组获取
        TypeConverter<?> baseConvert = this.get(sourceClass, baseArrayClass);
        if (baseConvert == null) {
            throw new IllegalArgumentException(StringUtils.format("unable to convert source [{}] class to target [{}] class", sourceClass, targetClass));
        }
        // 如果能获取到则将转换结果包装
        Object apply = baseConvert.apply(t);
        if (apply != null) {
            return (R) ArrayUtils.wrap(apply);
        }
        return null;
    }

    /**
     * 获取适配的 class 不适配父类
     *
     * @param sourceType 原始类型
     * @return set
     */
    public Set<Type> getSuitableClasses(Class<?> sourceType) {
        return this.getSuitableClasses(sourceType, false);
    }

    /**
     * 获取所有适配的 class 适配父类
     *
     * @param sourceType 原始类型
     * @return set
     */
    public Set<Type> getAllSuitableClasses(Class<?> sourceType) {
        return this.getSuitableClasses(sourceType, true);
    }

    protected Set<Type> getSuitableClasses(Class<?> sourceType, boolean all) {
        Enumeration<Type> keys = conversionMapping.computeIfAbsent(sourceType, c -> new ConcurrentHashMap<>(8)).keys();
        Set<Type> classes = Sets.asSet(keys);
        if (all) {
            for (Class<?> parentType : new ClassIterator<>(sourceType)) {
                Map<Type, TypeConverter<?>> map = conversionMapping.get(parentType);
                if (map != null) {
                    classes.addAll(map.keySet());
                }
            }
        }
        return classes;
    }


    /**
     * 获取适配的 TypeConverter 不适配父类
     *
     * @param sourceType 原始类型
     * @return map
     */
    public Map<Type, TypeConverter<?>> getSuitableConversion(Class<?> sourceType) {
        return this.getSuitableConversion(sourceType, false);
    }

    /**
     * 获取所有适配的 TypeConverter 适配父类
     *
     * @param sourceType 原始类型
     * @return map
     */
    public Map<Type, TypeConverter<?>> getAllSuitableConversion(Class<?> sourceType) {
        return this.getSuitableConversion(sourceType, true);
    }

    public static void main(String[] args) {
        Map<Type, TypeConverter<?>> map = TypeStore.STORE.getAllSuitableConversion(Integer.class);
        for (TypeConverter<?> converter : map.values()) {
            Object apply = converter.apply(1);
            System.out.println(apply);
        }
    }

    protected Map<Type, TypeConverter<?>> getSuitableConversion(Class<?> sourceType, boolean all) {
        Map<Type, TypeConverter<?>> mapping = conversionMapping.computeIfAbsent(sourceType, c -> new ConcurrentHashMap<>(8));
        if (all) {
            for (Class<?> parentType : new ClassIterator<>(sourceType)) {
                Map<Type, TypeConverter<?>> parentMapping = conversionMapping.get(parentType);
                if (parentMapping == null) {
                    continue;
                }
                parentMapping.forEach(mapping::putIfAbsent);
            }
        }
        return mapping;
    }

    /**
     * 获取全局 Store
     *
     * @return TypeStore
     */
    public static TypeStore getStore() {
        return STORE;
    }

    /**
     * 判断类型是否可以转换 sourceClass -> targetClass
     *
     * @param sourceClass 源class
     * @param targetClass 目标class
     * @return true可以直接转换
     */
    public static boolean canConvert(Class<?> sourceClass, Class<?> targetClass) {
        return canConvert(sourceClass, targetClass, STORE);
    }

    /**
     * 判断类型是否可以转换 sourceClass -> targetClass
     *
     * @param sourceClass 源class
     * @param targetClass 目标class
     * @param store       store
     * @return true可以直接转换
     */
    public static boolean canConvert(Class<?> sourceClass, Class<?> targetClass, TypeStore store) {
        Valid.notNull(sourceClass, "source class is null");
        Valid.notNull(targetClass, "target class is null");
        sourceClass = ClassUtils.getWrapClass(sourceClass);
        targetClass = ClassUtils.getWrapClass(targetClass);
        if (canDirectConvert(sourceClass, targetClass, false)) {
            return true;
        }
        if (store.get(sourceClass, targetClass) != null) {
            return true;
        }
        for (Class<?> sourceParentClass : new ClassIterator<>(sourceClass)) {
            if (store.get(sourceParentClass, targetClass) != null) {
                return true;
            }
        }
        if (!ClassUtils.isArray(targetClass)) {
            return false;
        }
        Class<?> baseArrayClass = ClassUtils.getBaseArrayClass(targetClass);
        if (baseArrayClass.equals(targetClass)) {
            return false;
        }
        if (sourceClass.equals(baseArrayClass)) {
            return true;
        }
        return store.get(sourceClass, baseArrayClass) != null;
    }

    /**
     * 判断类型是否可以直接转换 sourceClass -> targetClass
     *
     * @param sourceClass 源class
     * @param targetClass 目标class
     * @return true可以直接转换
     */
    public static boolean canDirectConvert(Class<?> sourceClass, Class<?> targetClass) {
        return canDirectConvert(sourceClass, targetClass, true);
    }

    /**
     * 判断类型是否可以直接转换 sourceClass -> targetClass
     *
     * @param sourceClass 源class
     * @param targetClass 目标class
     * @param wrap        是否包装基本类型
     * @return true可以直接转换
     */
    private static boolean canDirectConvert(Class<?> sourceClass, Class<?> targetClass, boolean wrap) {
        Valid.notNull(sourceClass, "source class is null");
        Valid.notNull(targetClass, "target class is null");
        if (wrap) {
            sourceClass = ClassUtils.getWrapClass(sourceClass);
            targetClass = ClassUtils.getWrapClass(targetClass);
        }
        // check unable
        if (targetClass.equals(Object.class) || targetClass.equals(sourceClass)) {
            return true;
        }
        // check impl
        return ClassUtils.isImplClass(targetClass, sourceClass);
    }

}
