package com.sondertara.common.collection.diff;

public interface KeyBuilder<K, O> {
    K getKey(O object);
}
