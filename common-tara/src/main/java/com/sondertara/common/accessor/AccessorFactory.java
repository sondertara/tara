package com.sondertara.common.accessor;

import com.sondertara.common.function.Factory;

import java.util.List;

/**
 * @author huangxiaohu.1ih
 *  */
@SuppressWarnings("rawtypes")
public interface AccessorFactory<T> extends Factory<Class<?>, Accessor<String,T>>{
    @Override
    Accessor<String,T> get(Class<?> klass);

    @SuppressWarnings("rawtypes")
    List<Class> applyTo();

    boolean appliable(Class expectedClazz, Class actualClass);
}
