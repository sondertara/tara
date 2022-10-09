
package com.sondertara.common.bean;

/**
 * Defines how to extract the key from value.
 *
 * @author huangxiaohu
 * @param <K> Key
 * @param <V> Value
 */
public interface KeyExtractor<K, V> {
    /**
     * Get key by value
     *
     * @param value the value
     * @return the key
     */
    K extract(V value);
}
