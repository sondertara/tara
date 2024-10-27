package com.sondertara.common.accessor;

import com.sondertara.common.collection.Lists;
import com.sondertara.common.collection.MapAccessor;

import java.util.List;
import java.util.Map;

public class StringKeyMapAccessorFactory<T> extends AbstractAccessorFactory<T> {
    @Override
    public Accessor<String, T> get(Class<?> klass) {
        Accessor accessor = new MapAccessor();
        return accessor;
    }

    @Override
    public List<Class> applyTo() {
        return Lists.asList(Map.class);
    }
}
