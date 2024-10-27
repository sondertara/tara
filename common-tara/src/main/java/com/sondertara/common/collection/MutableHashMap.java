package com.sondertara.common.collection;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 可转换的 HashMap
 *
 * @author Jiahang Li
 * @version 1.0.0
 *  */
public class MutableHashMap<K, V> extends HashMap<K, V> implements MutableMap<K, V>, Serializable {

    private static final long serialVersionUID = 111945899468912930L;

    public MutableHashMap() {
        super();
    }

    public MutableHashMap(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    public MutableHashMap(int initialCapacity) {
        super(initialCapacity);
    }

    public MutableHashMap(Map<? extends K, ? extends V> m) {
        super(m);
    }

}
