package com.sondertara.common.struct.pair;

import com.sondertara.common.base.Nameable;
import com.sondertara.common.struct.Pair;

/**
 * @param <V> any type
 */
public class NameValuePair<V> extends Pair<String, V> implements Nameable {
    public NameValuePair() {
    }

    public NameValuePair(String name, V value) {
        super(name,value);
    }

    public String getName() {
        return getKey();
    }

    public void setName(String name) {
        setKey(name);
    }
}
