package com.sondertara.common.bean.copier;

import com.sondertara.common.lang.reflect.ReflectUtils;

import java.lang.reflect.Field;
import java.util.Map;

public class MapToBeanCopier implements Copier {
    @Override
    @SuppressWarnings({"rawtypes"})
    public void copy(Object source, Object target) {
        if (!Map.class.isAssignableFrom(source.getClass())) {
            throw new IllegalArgumentException("The source must be a Map");
        }
        Map<String, Field> fieldMap = BeanCopierRegistry.findOrCreate(target.getClass());
        Map map = (Map) source;
        for (Object o : map.keySet()) {
            if (fieldMap.containsKey(o.toString())) {
                Field field = fieldMap.get(o.toString());
                Object value = map.get(o);
                if (value == null) {
                    continue;
                }
                ReflectUtils.setFieldValue(target, field, value);
            }
        }
    }
}
