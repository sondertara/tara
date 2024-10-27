package com.sondertara.common.collection.iter;

import com.sondertara.common.base.Valid;
import com.sondertara.common.collection.ArrayUtils;
import com.sondertara.common.collection.PrimitiveArrays;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * @author jinuo.fang
 */
@SuppressWarnings("unchecked")
public class ArrayIterator<E> extends UnmodifiableIterator<E> implements Iterable<E> {
    private final E[] array;
    private int index;
    private final int length;
    private boolean reversed;

    public ArrayIterator(Object array) {
        this(array, false);
    }

    public ArrayIterator(Object array, boolean reversed) {
        if (array != null) {
            Valid.isTrue(ArrayUtils.isArray(array));
            if (PrimitiveArrays.isPrimitiveArray(array.getClass())) {
                this.array = PrimitiveArrays.wrap(array);
            } else {
                this.array = ArrayUtils.copy((E[]) array);
            }
        } else {
            this.array = null;
        }
        this.length = this.array == null ? 0 : this.array.length;
        this.reversed = reversed;
        this.index = reversed ? (this.length - 1) : 0;
    }

    public ArrayIterator(E[] array) {
        this(array, false);
    }

    public ArrayIterator(E[] array, boolean reversed) {
        this.array = ArrayUtils.copy(array);
        this.length = array == null ? 0 : array.length;
        this.reversed = reversed;
        this.index = reversed ? (this.length - 1) : 0;
    }

    @Override
    public Iterator<E> iterator() {
        return new ArrayIterator<E>(array, reversed);
    }

    @Override
    public boolean hasNext() {
        return reversed ? index >= 0 : index < length;
    }

    @Override
    public E next() {
        if (hasNext()) {
            return array[reversed ? index-- : index++];
        } else {
            throw new NoSuchElementException();
        }
    }
}
