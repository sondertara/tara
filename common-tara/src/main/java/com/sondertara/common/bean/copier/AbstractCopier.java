package com.sondertara.common.bean.copier;

import com.sondertara.common.bean.exception.BeanCopyException;
import com.sondertara.common.convert.TypeConverter;

import java.lang.reflect.Field;

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
    public void copy(Object source, Object target) {
        try {
            Object value = fromField.get(source);
            if (value == null) {
                if (COPY_IGNORE_NULL.get()) {
                    return;
                }
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
