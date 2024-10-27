package com.sondertara.common.collection.iter;

import com.sondertara.common.base.ObjectUtils;
import com.sondertara.common.collection.ArrayUtils;

import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

public class Iterables {
    public static boolean isIterable(Object obj) {
        if (ObjectUtils.isNull(obj)) {
            return false;
        }
        if (ArrayUtils.isArray(obj)) {
            return true;
        }
        if (obj instanceof Iterable) {
            return true;
        }
        if (obj instanceof Map) {
            return true;
        }
        if (obj instanceof Iterator) {
            return true;
        }
        return obj instanceof Enumeration;
    }

    public static <E> NullIterator<E> nullIterator() {
        return NullIterator.INSTANCE;
    }

    public static <E> Iterator<E> getIterator(Iterable<E> iterable) {
        if (iterable != null) {
            return iterable.iterator();
        }
        return nullIterator();
    }

    private Iterables(){

    }

    public static int size(Iterator<?> iterator) {
        int size = 0;
        if (iterator != null) {
            while (iterator.hasNext()) {
                iterator.next();
                size++;
            }
        }
        return size;
    }

    public static <T> boolean addAll(Collection<T> collection, Iterator<? extends T> iterator) {
        Objects.requireNonNull(collection);
        Objects.requireNonNull(iterator);

        boolean wasModified;
        for(wasModified = false; iterator.hasNext(); wasModified |= collection.add(iterator.next())) {
        }

        return wasModified;
    }

}
