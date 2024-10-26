package com.sondertara.common.bean.copier;

import com.sondertara.common.bean.exception.BeanCopyException;
import com.sondertara.common.function.TypeConverter;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author huangxiaohu
 */
public abstract class AbstractCopier implements Copier {
    protected Field fromField;
    protected Field toField;
    protected TypeConverter<?> converter;


    public AbstractCopier(Field fromField, Field toField) {
        this.fromField = fromField;
        this.toField = toField;
    }

    @Override
    public void copy(Object source, Object target, String... ignoreProperties) {
        try {
            Object value = fromField.get(source);
            Set<String> set = Arrays.stream(ignoreProperties).collect(Collectors.toSet());
            if (set.contains(fromField.getName())) {
                return;
            }
            if (value == null) {
                toField.set(target, null);
                return;
            }
            if (converter == null) {
                toField.set(target, value);
            } else {
                toField.set(target, converter.convert(value, null));
            }
        } catch (IllegalAccessException e) {
            throw new BeanCopyException(e);
        }
    }
}
