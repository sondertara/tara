package com.sondertara.common.reflect;

import com.sondertara.common.accessor.BasedStringAccessor;
import org.jspecify.annotations.NonNull;

/**
 * A field accessor based on reflect
 *
 * @author jinuo.fang
 */
public class FieldAccessor extends BasedStringAccessor<String, Object> {

    public FieldAccessor() {
    }
    public FieldAccessor(@NonNull Object target) {
        setTarget(target);
    }


    private <V> V getFieldValue(String fieldName, V defaultValue) {
        V v;
        v = ReflectUtils.getAnyFieldValue(getTarget(), fieldName, true);
        if (v == null) {
            v = defaultValue;
        }
        return v;
    }

    private <V> void setFieldValue(String fieldName, V value) {
        ReflectUtils.setAnyFieldValue(getTarget(), fieldName, value, false);
    }

    @Override
    public Object get(String field) {
        return getFieldValue(field, null);
    }

    @Override
    public boolean has(String key) {
        return ReflectUtils.getAnyField(getTarget().getClass(), key) != null;
    }

    @Override
    public String getString(String key, String defaultValue) {
        Object value = get(key);
        if (value == null) {
            return defaultValue;
        }
        return value.toString();
    }

    @Override
    public void set(String field, Object value) {
        setFieldValue(field, value);
    }

    @Override
    public void setString(String field, String value) {
        set(field, value);
    }

    @Override
    public void remove(String field) {
        set(field, null);
    }
}
