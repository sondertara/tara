package com.sondertara.common.cache;

public interface CacheLoader<K, V> {
    V load(K key);
}
