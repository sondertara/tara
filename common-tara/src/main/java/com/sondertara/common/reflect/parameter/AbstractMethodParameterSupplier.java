package com.sondertara.common.reflect.parameter;

import com.sondertara.common.lifecycle.InitializationException;

public abstract class AbstractMethodParameterSupplier implements MethodParameterSupplier {
    protected volatile boolean inited = false;

    @Override
    public void init() throws InitializationException {
        if (!inited) {
            inited = true;
        }
    }
}
