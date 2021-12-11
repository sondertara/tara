package com.sondertara.common.util;

import org.dozer.DozerBeanMapper;

import java.util.ArrayList;
import java.util.List;

/**
 * @author SonderTara
 */
public class BeanUtils extends org.apache.commons.beanutils.BeanUtils {

    /**
     * 简单封装Dozer, 实现Bean<-->Bean深度转换
     * 依赖包 compile "net.sf.dozer:dozer:5.5.1"
     * <p>
     * 1. 持有Mapper的单例.
     * 2. 返回值类型转换.
     * 3. 批量转换Collection中的所有对象.
     * 4. 区分创建新的B对象与将对象A值复制到已存在的B对象两种函数.
     * <p>
     * 持有Dozer单例, 避免重复创建DozerMapper消耗资源
     */
    private static DozerBeanMapper dozer = new DozerBeanMapper();


    /**
     * 基于Dozer将对象A的值拷贝到对象B中
     */
    public static void copyObject(Object source, Object targetObject) {
        dozer.map(source, targetObject);
    }

    /**
     * 基于Dozer转换对象的类型
     */
    public static <T> T beanToBean(Object source, Class<T> targetClass) {
        return dozer.map(source, targetClass);
    }

    /**
     * 基于Dozer转换Collection中对象的类型
     */
    public static <T> List<T> beansToBeans(Iterable<?> sourceList, Class<T> targetClass) {
        List<T> targetList = new ArrayList<>();
        for (Object sourceObject : sourceList) {
            T targetObject = dozer.map(sourceObject, targetClass);
            targetList.add(targetObject);
        }
        return targetList;
    }

}
