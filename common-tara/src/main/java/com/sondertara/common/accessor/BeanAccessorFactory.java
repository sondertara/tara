package com.sondertara.common.accessor;

import com.sondertara.common.collection.Lists;
import com.sondertara.common.reflect.FieldAccessor;

import java.util.List;

/**
 *  */
public class BeanAccessorFactory<T> extends AbstractAccessorFactory<T> {
    @Override
    public Accessor<String, T> get(Class<?> klass) {
        Accessor accessor = new FieldAccessor();
        return accessor;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public List<Class> applyTo() {
        return Lists.asList(Object.class);
    }
}
