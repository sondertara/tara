package com.sondertara.common.collection.diff;

public interface Differ<V, R extends DiffResult> {
    R diff(V oldValue, V newValue);
}
