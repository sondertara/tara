package com.sondertara.common.collection;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;

public class PrefixHashMap<V> extends HashMap<String, V> implements Cloneable {
    private String prefix;
    private boolean readUsingPrefix;

    public PrefixHashMap(String prefix) {
        this(prefix, true);
    }

    public PrefixHashMap(String prefix, boolean readUsingPrefix) {
        Objects.requireNonNull(prefix);
        this.prefix = prefix;
        this.readUsingPrefix = readUsingPrefix;
    }


    public PrefixHashMap(String prefix, Map<String, V> map) {
        this(prefix, true, map);
    }

    public PrefixHashMap(String prefix, boolean readUsingPrefix, Map<String, V> map) {
        this(prefix, readUsingPrefix);
        putAll(map);
    }


    private String getInnerKey(Object key) {
        if (readUsingPrefix) {
            return prefix + key;
        } else {
            // key - prefix
            String key0 = key.toString();
            if (key0.startsWith(prefix)) {
                if (key0.length() > prefix.length()) {
                    return key0.substring(prefix.length());
                }
                return "";
            }
            return key0;
        }
    }

    @Override
    public V put(String key, V value) {
        if (key == null) {
            return null;
        }
        return super.put(getInnerKey(key), value);
    }

    @Override
    public void putAll(Map<? extends String, ? extends V> m) {
        BiConsumer<String, V> consumer2 = new BiConsumer<String, V>() {
            @Override
            public void accept(String key, V value) {
                put(key, value);
            }
        };
        CollectionUtils.forEach(m, consumer2);
    }

    @Override
    public Object clone() {
        return new PrefixHashMap<V>(prefix, readUsingPrefix, this);
    }
}
