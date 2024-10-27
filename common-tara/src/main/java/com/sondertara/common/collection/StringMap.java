package com.sondertara.common.collection;

import com.sondertara.common.net.http.HttpQueryStrings;

import java.util.HashMap;
import java.util.Map;

/**
 * A map with key, value are String.
 */
public class StringMap extends HashMap<String, String> {
    public static final StringMap EMPTY = new StringMap(0);

    public StringMap() {
        super();
    }


    public StringMap(int initialCapacity) {
        super(initialCapacity);
    }

    public StringMap(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    public <K, V> StringMap(Map<K, V> map) {
        if (map == null || map.isEmpty()) {
            return;
        }
        map.forEach((k, v) -> put(k.toString(), v == null ? null : v.toString()));
    }

    public StringMap(String src, String keyValueSpec, String entrySpec) {
        this(com.sondertara.common.struct.Entry.getMap(src, keyValueSpec, entrySpec));
    }

    /**
     * 只适用于一个name对应一个value的场景
     */
    public static StringMap httpUrlParameters(String url) {
        return HttpQueryStrings.getQueryStringStringMap(url);
    }
}
