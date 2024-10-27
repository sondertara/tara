package com.sondertara.common.concurrent;

import com.sondertara.common.collection.MutableMap;

import java.io.Serializable;
import java.util.Map;

/**
 * 可转换的ConcurrentHashMap
 *
 * @author Jiahang Li
 * @version 1.0.0
 *  */
public class MutableConcurrentHashMap<K, V> extends SafeConcurrentHashMap<K, V> implements MutableMap<K, V>, Serializable {

    private static final long serialVersionUID = 142731998248615024L;

    public MutableConcurrentHashMap() {
        super();
    }

    public MutableConcurrentHashMap(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    public MutableConcurrentHashMap(int initialCapacity) {
        super(initialCapacity);
    }

    public MutableConcurrentHashMap(Map<? extends K, ? extends V> m) {
        super(m);
    }

}
