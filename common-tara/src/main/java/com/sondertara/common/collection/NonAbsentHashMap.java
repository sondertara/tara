package com.sondertara.common.collection;

import org.jspecify.annotations.NonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

/**
 * Avoid the follower code:
 * <pre>
 *     Map<K,List<E>> map = new HashMap<K,List<E>>();
 *     List<E> list = map.get(key);
 *     if(list==null){
 *         list = new ArrayList<E>();
 *         map.put(key, list);
 *     }
 *     list.add(e);
 * </pre>
 * <p>
 * using this class, your code will be:
 * <pre>
 *     Supplier<K,List<E>> supplier = new Supplier<K,List<E>>(){
 *         public List<E> get(K key){
 *             return new ArrayList<E>();
 *         }
 *     };
 *     Map<K,List<E>> map = new NonAbsentHashMap<K,List<E>>();
 *     map.get(key).add(e);
 * </pre>
 *
 * @param <K> key
 * @param <V> value
 * @see Map
 * @see HashMap
 * @see WrappedNonAbsentMap
 */
public class NonAbsentHashMap<K, V> extends HashMap<K, V> {
    private transient Function<K, V> supplier;

    public NonAbsentHashMap(@NonNull Function<K, V> supplier) {
        super();
        setSupplier(supplier);
    }

    public NonAbsentHashMap(int initialCapacity, Function<K, V> supplier) {
        this(initialCapacity, initialCapacity, supplier);
    }

    public NonAbsentHashMap(int initialCapacity, float loadFactor, @NonNull Function<K, V> supplier) {
        super(initialCapacity, loadFactor);
        setSupplier(supplier);
    }

    public NonAbsentHashMap(Map<? extends K, ? extends V> m, Function<K, V> supplier) {
        super(m);
        setSupplier(supplier);
    }

    private void setSupplier(Function<K, V> supplier) {
        Objects.requireNonNull(supplier);
        this.supplier = supplier;
    }

    public V get(Object key, Function<K, V> supplier) {
        V v = getIfPresent(key);
        if (v == null) {
            supplier = supplier != null ? supplier : this.supplier;
            v = putIfAbsent((K) key, supplier.apply((K) key));
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
