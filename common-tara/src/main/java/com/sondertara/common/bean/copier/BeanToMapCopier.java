package com.sondertara.common.bean.copier;

import com.sondertara.common.reflect.ReflectUtils;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author huangxiaohu
 */
public class BeanToMapCopier implements Copier {


    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void copy(Object source, Object target, String... ignoreProperties) {
        if (!Map.class.isAssignableFrom(target.getClass())) {
            throw new IllegalArgumentException("The source must be a Map");
        }
        Map<String, Field> fieldMap = BeanCopierRegistry.findOrCreate(source.getClass());
        Map map = (Map) target;
        Set<String> ignoredSet = Arrays.stream(ignoreProperties).collect(Collectors.toSet());
        fieldMap.forEach((key, value) -> {
            if (!ignoredSet.contains(key)) {
                map.put(key, ReflectUtils.getFieldValue(source, value));
            }
        });
    }
}
