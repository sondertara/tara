package com.sondertara.common.registry;


import com.sondertara.common.function.Factory;

public interface Registry<K, V> extends Factory<K, V> {
    void register(V v);

    void register(K key, V v);

    /**
     * @param key the key
     *      */
    void unregister(K key);

    /**
     *      */
    boolean contains(K key);
}
