package com.sondertara.common.event;

import java.io.Serializable;

public abstract class EventSource<T> implements Serializable {

    protected transient T source;
    private final long timestamp;

    public EventSource(T source) {
        if (source == null) {
            throw new IllegalArgumentException("null source");
        }
        this.source = source;
        this.timestamp = System.currentTimeMillis();
    }

    public T getSource() {
        return source;
    }

    public final long getTimestamp() {
        return this.timestamp;
    }
}
