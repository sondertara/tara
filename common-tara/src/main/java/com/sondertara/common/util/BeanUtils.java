package com.sondertara.common.util;

import com.sondertara.common.bean.copier.BeanCopy;
import com.sondertara.common.bean.copier.BeanToMapCopier;
import com.sondertara.common.bean.copier.MapToBeanCopier;
import com.sondertara.common.lang.Assert;
import com.sondertara.common.lang.reflect.ReflectUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Bean copy Utils
 *
 * @author huangxiaohu
 */
public class BeanUtils {

    /**
     * 修改spring的BeanUtils,不用null覆盖已有的值
     */
    public static void copyNonNullProperties(Object source, Object target) {

        Assert.notNull(source, "Source must not be null");
        Assert.notNull(target, "Target must not be null");
        BeanCopy.copyToIgnoreNull(source, target);

    }

    public static void copyProperties(Object source, Object target) {

        Assert.notNull(source, "Source must not be null");
        Assert.notNull(target, "Target must not be null");
        BeanCopy.copyTo(source, target);

    }

    public static <T> T beanToBean(Object source, Class<T> target) {
        return BeanCopy.copy(source, target);
    }

    public static <T> List<T> beansToBeans(Collection<?> sourceList, Class<T> targetClass) {
        if (sourceList == null || sourceList.isEmpty()) {
            return Collections.emptyList();
        }
        List<T> result = new ArrayList<>();
        BeanCopy.copy(sourceList, result, targetClass);
        return result;
    }

    /**
     * Java bean to map
     *
     * @param bean the bean
     * @param <T>  the type of the bean
     * @return map
     */
    public static <T> Map<String, Object> beanToMap(T bean) {
        Map<String, Object> map = new HashMap<>(16);
        if (bean == null) {
            return map;
        }
        new BeanToMapCopier().copy(bean, map);
        return map;
    }

    /**
     * Map to bean
     *
     * @param map the original map
     * @param t   the bean instance
     * @param <T> the type of the bean
     */
    public static <T> void mapToBean(Map<?, ?> map, T t) {
        new MapToBeanCopier().copy(map, t);
    }

    /**
     * Map to java bean
     *
     * @param map   The original map
     * @param clazz the class of target bean
     * @param <T>   the type of the bean
     * @return the target bean
     */
    public static <T> T mapToBean(Map<?, ?> map, Class<T> clazz) {
        T instance = ReflectUtils.newInstance(clazz);
        mapToBean(map, instance);
        return instance;
    }

    /**
     * Convert list of bean to List map
     *
     * @param list the list of bean
     * @param <T>  the type of the bean
     * @return the list map
     */
    public static <T> List<Map<String, Object>> beansToMaps(List<T> list) {
        List<Map<String, Object>> maps = new ArrayList<>();
        if (list == null || list.size() == 0) {
            return null;
        }
        for (T bean : list) {
            if (bean != null) {
                Map<String, Object> beanToMaps = beanToMap(bean);
                maps.add(beanToMaps);
            }
        }
        return maps;
    }

    /**
     * Convert list of map to list of bean
     *
     * @param list the map list
     * @param t    the class of the bean
     * @param <T>  the type of the bean
     * @return the list of bean
     */
    public static <T> List<T> mapsToBeans(List<Map<?, Object>> list, Class<T> t) {
        List<T> beans = new ArrayList<>();
        if (list == null || list.size() == 0) {
            return null;
        }
        for (Map<?, Object> map : list) {
            T t1 = ReflectUtils.newInstance(t);
            mapToBean(map, t1);
            beans.add(t1);
        }
        return beans;
    }

}
