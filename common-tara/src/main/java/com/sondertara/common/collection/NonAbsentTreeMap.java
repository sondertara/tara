package com.sondertara.common.collection;

import org.jspecify.annotations.NonNull;

import java.util.Comparator;
import java.util.Map;
import java.util.Objects;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.Function;

public class NonAbsentTreeMap<K, V> extends TreeMap<K, V> {
    private transient java.util.function.Function<K, V> Function;

    public NonAbsentTreeMap(@NonNull Function<K, V> Function) {
        super();
        setFunction(Function);
    }

    public NonAbsentTreeMap(Comparator<? super K> comparator, @NonNull Function<K, V> Function) {
        super(comparator);
        setFunction(Function);
    }

    public NonAbsentTreeMap(Map<? extends K, ? extends V> m, @NonNull Function<K, V> Function) {
        super(m);
        setFunction(Function);
    }

    public NonAbsentTreeMap(SortedMap<K, ? extends V> m, @NonNull Function<K, V> Function) {
        super(m);
        setFunction(Function);
    }

    private void setFunction(Function<K, V> Function) {
        Objects.requireNonNull(Function);
        this.Function = Function;
    }

    public V get(Object key, Function<K, V> Function) {
        V v = getIfPresent(key);
        if (v == null) {
            Function = Function != null ? Function : this.Function;
            v = putIfAbsent((K) key, Function.apply((K) key));
        }
        return v;
    }

    @Override
    public V get(Object key) {
        return get(key, null);
    }

    public V getIfPresent(Object key) {
        return super.get(key);
    }

    public V putIfAbsent(K key, V value) {
        V v = super.get(key);
        if (v == null) {
            super.put(key, value);
            v = value;
        }
        return v;
    }
}
