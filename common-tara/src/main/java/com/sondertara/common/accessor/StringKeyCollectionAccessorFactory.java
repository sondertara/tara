package com.sondertara.common.accessor;

import com.sondertara.common.collection.Lists;

import java.util.Collection;
import java.util.List;

/**
 *  */
public class StringKeyCollectionAccessorFactory<T> extends AbstractAccessorFactory<T> {
    @Override
    public Accessor<String, T> get(Class<?> klass) {
        Accessor accessor = new StringKeyCollectionAccessor();
        return accessor;
    }

    @Override
    public List<Class> applyTo() {
        return Lists.asList(Collection.class,Object[].class);
    }

    @Override
    public boolean appliable(Class expectedClazz, Class actualClass) {
        if(actualClass.isArray()){
            return true;
        }
        return super.appliable(expectedClazz, actualClass);
    }
}
