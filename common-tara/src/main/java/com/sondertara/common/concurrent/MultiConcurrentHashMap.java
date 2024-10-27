package com.sondertara.common.concurrent;


import com.sondertara.common.collection.MultiMap;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;

/**
 * MultiConcurrentHashMap
 *
 * @author Jiahang Li
 * @version 1.0.0
 *  */
public class MultiConcurrentHashMap<E, K, V> extends SafeConcurrentHashMap<E, ConcurrentHashMap<K, V>>
        implements MultiMap<E, K, V, ConcurrentHashMap<K, V>>, Serializable {

    private static final long serialVersionUID = 8455892712354974891L;

    /**
     * key 初始化空间
     */
    private int keyInitialCapacity;

    public MultiConcurrentHashMap() {
        this(16,16);
    }

    public MultiConcurrentHashMap(int elementInitialCapacity) {
        this(elementInitialCapacity, 16);
    }

    public MultiConcurrentHashMap(int elementInitialCapacity, int keyInitialCapacity) {
        super(elementInitialCapacity);
        this.keyInitialCapacity = keyInitialCapacity;
    }

    /**
     * 设置 key 初始化空间
     *
     * @param keyInitialCapacity key 初始化空间
     */
    public void keyInitialCapacity(int keyInitialCapacity) {
        this.keyInitialCapacity = keyInitialCapacity;
    }

    @Override
    public ConcurrentHashMap<K, V> computeSpace(E e) {
        return super.computeIfAbsent(e, k -> new SafeConcurrentHashMap<>(keyInitialCapacity));
    }

}

