package com.sondertara.common.bean.copier;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.sondertara.common.convert.TypeConverter;
import com.sondertara.common.lang.reflect.ReflectUtils;
import com.sondertara.common.util.StringUtils;
import com.sondertara.common.util.TypeUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author huangxiaohu
 */
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
            if (fieldMap.containsKey(o.toString())) {
                Field toField = fieldMap.get(o.toString());
                Object value = map.get(o);
                if (value == null) {
                    continue;
                }
                if (value instanceof Map) {
                    if (Map.class.isAssignableFrom(toField.getType())) {
                        Type[] typeArguments = TypeUtils.getTypeArguments(toField.getGenericType());


                        Map<?, ?> fromMap = (Map<?, ?>) value;
                        String from = Utils.nameOf(fromMap.values().stream().findFirst().get().getClass());
                        Map toMap;
                        if (LinkedHashMap.class.isAssignableFrom(toField.getType())) {
                            toMap = Maps.newLinkedHashMap();
                        } else {
                            toMap = Maps.newHashMap();
                        }
                        TypeConverter<?> converter = Utils.findOrCreateConverter(from, Utils.nameOf(typeArguments[1]));
                        if (converter == null) {
                            toMap.putAll(fromMap);
                        } else {
                            for (Map.Entry<?, ?> entry : fromMap.entrySet()) {
                                toMap.put(entry, converter.convert(entry.getValue(), null));
                            }
                        }
                        ReflectUtils.setFieldValue(target, toField, toMap);

                    } else {
                        Object instance = ReflectUtils.newInstance(toField.getType());
                        copy(value, instance);
                        ReflectUtils.setFieldValue(target, toField, instance);
                    }
                } else if (value instanceof Collection) {
                    if (!Collection.class.isAssignableFrom(toField.getType())) {
                        throw new IllegalArgumentException(StringUtils.format("Please check your bean property type,the map value for key {} is Collection,but the field is {}", o, toField.getType().getCanonicalName()));
                    }
                    Collection<?> collection = (Collection<?>) value;
                    String from = Utils.nameOf(collection.stream().findFirst().get().getClass());
                    Collection toCol;
                    TypeConverter<?> listConverter = Utils.findOrCreateConverter(from, Utils.nameOf(TypeUtils.getActualTypes(toField.getGenericType())[0]));
                    if (Set.class.isAssignableFrom(toField.getType())) {
                        toCol = Sets.newHashSet();
                    } else {
                        toCol = Lists.newArrayList();
                    }
                    if (null == listConverter) {
                        toCol.addAll(collection);
                    } else {
                        for (Object o1 : collection) {
                            toCol.add(listConverter.convert(o1, null));
                        }
                    }
                    ReflectUtils.setFieldValue(target, toField, toCol);
                } else {
                    ReflectUtils.setFieldValue(target, toField, value);
                }
            }
        }
    }
}
