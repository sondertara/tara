package com.sondertara.common.bean.copier;

import com.sondertara.common.convert.ConvertUtils;
import com.sondertara.common.function.TypeConverter;
import com.sondertara.common.reflect.ReflectUtils;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * TODO
 *
 * @author huangxiaohu.1ih
 * @date 2024/8/13 19:03
 */

@SuppressWarnings({"unchecked","rawtypes"})
public class ArrayCopier extends AbstractCopier {
    public ArrayCopier(Field fromField, Field toField) {
        super(fromField, toField);
        fromField.setAccessible(true);
        toField.setAccessible(true);
        Class<?> fromCls = fromField.getType();
        Class<?> toCls = toField.getType();
        if (fromCls.isArray() && toCls.isArray()) {
            converter = (TypeConverter<Object>) o -> ConvertUtils.convert(toCls, o);
        } else if (fromCls.isArray() && Collection.class.isAssignableFrom(toCls)) {
            converter = (TypeConverter<Object>) o -> {
                List<Object> list = Arrays.asList(o);
                Collection collection = (Collection) ReflectUtils.newInstance(toCls);
                if (null==collection){
                    throw new IllegalArgumentException("Cannot create instance for class:"+toCls);
                }
                collection.addAll(list);
                return collection;
            };
        }
    }

}
