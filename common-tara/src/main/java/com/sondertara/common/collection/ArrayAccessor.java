package com.sondertara.common.collection;

import com.sondertara.common.base.Assert;
import com.sondertara.common.accessor.BasedStringAccessor;
import org.jspecify.annotations.NonNull;

import java.lang.reflect.Array;
import java.util.Objects;

/**
 * An array accessor
 *
 * @param <E> an array
 * @author jinuo.fang
 */
public class ArrayAccessor<E> extends BasedStringAccessor<Integer, E> {

    public ArrayAccessor() {
    }

    public ArrayAccessor(E target) {
        setTarget(target);
    }

    @Override
    public void setTarget(@NonNull E target) {
        Objects.requireNonNull(target);
         Assert.isTrue(ArrayUtils.isArray(target));
        super.setTarget(target);
    }

    @Override
    public boolean has(Integer index) {
        int length = Array.getLength(getTarget());
        return index >=0 && index < length;
    }

    @Override
    public Object get(Integer index) {
        return Array.get(getTarget(), index);
    }

    @Override
    public String getString(Integer index, String defaultValue) {
        Object o = get(index);
        return o == null ? defaultValue : o.toString();
    }

    @Override
    public void set(Integer index, Object value) {
        Array.set(getTarget(), index, value);
    }

    @Override
    public void remove(Integer index) {
        set(index, null);
    }
}
