package com.sondertara.common.text.lexer;

import com.sondertara.common.base.Assert;
import org.jspecify.annotations.NonNull;

public abstract class ImmutableUserMap {
    public static final ImmutableUserMap EMPTY = new ImmutableUserMap() {
        public <T> T get(@NonNull Key<T> key) {
            Assert.notNull(key, "key");
            return null;
        }
    };

    private ImmutableUserMap() {
    }

    public final <T> ImmutableUserMap put(@NonNull Key<T> key, T value) {
        Assert.notNull(key, "key");
        return new ImmutableUserMapImpl<>(key, value, this);
    }

    public abstract <T> T get(@NonNull Key<T> paramKey);

    private static final class ImmutableUserMapImpl<V> extends ImmutableUserMap {
        private final Key<V> myKey;

        private final V myValue;

        private final ImmutableUserMap myNext;

        private ImmutableUserMapImpl(Key<V> key, V value, ImmutableUserMap next) {
            this.myKey = key;
            this.myNext = next;
            this.myValue = value;
        }

        public <T> T get(@NonNull Key<T> key) {
            Assert.notNull(key, "key");
            if (key.equals(this.myKey))
                return (T) this.myValue;
            return this.myNext.get(key);
        }
    }
}
