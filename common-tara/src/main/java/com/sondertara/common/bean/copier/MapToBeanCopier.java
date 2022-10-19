package com.sondertara.common.bean.copier;

import com.sondertara.common.lang.reflect.ReflectUtils;

import java.lang.reflect.Field;
import java.util.Map;

public class MapToBeanCopier implements Copier {
    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void copy(Object source, Object target) {
        if (!Map.class.isAssignableFrom(source.getClass())) {
            throw new IllegalArgumentException("The source must be a Map");
        }
        Map<String, Field> fieldMap = BeanCopierRegistry.findOrCreate(target.getClass());
        Map map = (Map) source;
        for (Object o : map.keySet()) {
            Object value = map.get(o);
            if (value == null) {
                continue;
            }
            Field field = fieldMap.get(o.toString());
            ReflectUtils.setFieldValue(target, field, value);

        }
    }
}
