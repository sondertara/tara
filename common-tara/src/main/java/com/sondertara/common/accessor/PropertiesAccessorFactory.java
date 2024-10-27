package com.sondertara.common.accessor;

import com.sondertara.common.collection.Lists;
import com.sondertara.common.text.properties.PropertiesAccessor;

import java.util.List;
import java.util.Properties;

/**
 *  */
public class PropertiesAccessorFactory<T> extends AbstractAccessorFactory<T> {
    @Override
    public Accessor<String, T> get(Class<?> klass) {
        Accessor accessor = new PropertiesAccessor();
        return accessor;
    }

    @Override
    public List<Class> applyTo() {
        return Lists.asList(Properties.class);
    }
}