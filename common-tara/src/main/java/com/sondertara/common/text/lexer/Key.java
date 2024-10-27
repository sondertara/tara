package com.sondertara.common.text.lexer;

import com.sondertara.common.base.Assert;
import com.sondertara.common.concurrent.ConcurrentReferenceHashMap;
import com.sondertara.common.reflect.reference.ReferenceType;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.concurrent.atomic.AtomicInteger;

public class Key<T> {
    private static final AtomicInteger ourKeysCounter = new AtomicInteger();

    private static final ConcurrentReferenceHashMap<Integer, Key<?>> allKeys = new ConcurrentReferenceHashMap<Integer, Key<?>>(10, ReferenceType.STRONG, ReferenceType.WEAK);

    private final int myIndex = ourKeysCounter.getAndIncrement();

    private final String myName;

    public Key(@NonNull String name) {
        this.myName = name;
        synchronized (allKeys) {
            allKeys.put(this.myIndex, this);
        }
    }

    public final int hashCode() {
        return this.myIndex;
    }

    public final boolean equals(Object obj) {
        return obj == this;
    }

    public String toString() {
        return this.myName;
    }

    @NonNull
    public static <T> Key<T> create(@NonNull String name) {
        Assert.notNull(name, "name");
        return new Key<T>(name);
    }


    public static <T> Key<T> getKeyByIndex(int index) {
        synchronized (allKeys) {
            return (Key<T>) allKeys.get(index);
        }
    }

    @Deprecated
    @Nullable
    public static Key<?> findKeyByName(@NonNull String name) {
        Assert.notNull(name, "name");
        synchronized (allKeys) {
            for (Key<?> key : allKeys.values()) {
                if (name.equals(key.myName))
                    return key;
            }
            return null;
        }
    }
}