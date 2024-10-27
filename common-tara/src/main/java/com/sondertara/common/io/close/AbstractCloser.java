package com.sondertara.common.io.close;

import com.sondertara.common.io.Closer;
import com.sondertara.common.logging.Loggers;
import com.sondertara.common.reflect.ReflectUtils;
import org.jspecify.annotations.NonNull;


public abstract class AbstractCloser<I> implements Closer<I> {

    @Override
    public void close(I i) {
        if (i != null) {
            try {
                doClose(i);
            } catch (Exception e) {
                Loggers.getLogger(getClass()).warn("error occur when close {}, error: {}", ReflectUtils.getFQNClassName(i.getClass()), e.getMessage(), e);
            }
        }
    }

    protected abstract void doClose(@NonNull I i) throws Exception;


}
