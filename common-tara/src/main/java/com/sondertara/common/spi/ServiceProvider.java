package com.sondertara.common.spi;

import com.sondertara.common.base.Provider;

import java.util.Iterator;

public interface ServiceProvider<T> extends Provider<Class<T>, Iterator<T>> {
    @Override
    Iterator<T> get(Class<T> serviceClass);
}
