package com.sondertara.common.bean.copier;

import com.sondertara.common.lang.reflect.ReflectUtils;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * @author huangxiaohu
 */
public class BeanToMapCopier implements Copier {


    @Override
    @SuppressWarnings({"unchked", "rawtypes"})
    public void copy(Object source, Object target) {
        if (!Map.class.isAssignableFrom(target.getClass())) {
            throw new IllegalArgumentException("The source must be a Map");
        }
        Map<String, Field> fieldMap = BeanCopierRegistry.findOrCreate(source.getClass());
        Map map = (Map) target;
        fieldMap.forEach((key, value) -> {
            map.put(key, ReflectUtils.getFieldValue(source, value));
        });
    }
}
