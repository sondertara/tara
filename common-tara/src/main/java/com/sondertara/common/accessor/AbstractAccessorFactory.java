package com.sondertara.common.accessor;


/**
 *  */
@SuppressWarnings("ALL")
public abstract class AbstractAccessorFactory<T> implements AccessorFactory<T> {

    @Override
    public boolean appliable(Class expectedClazz, Class actualClass) {
        return expectedClazz.isAssignableFrom( actualClass);
    }
}
