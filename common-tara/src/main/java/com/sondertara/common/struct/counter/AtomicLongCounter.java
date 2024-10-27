package com.sondertara.common.struct.counter;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

public class AtomicLongCounter extends LongCounter {
    private AtomicLong vh;
    private long initValue;

    public AtomicLongCounter() {
        this(0L);
    }

    public AtomicLongCounter(long init) {
        this.initValue=init;
        this.vh = new AtomicLong(init);
    }

    @Override
    public Long increment(Long delta) {
        Objects.requireNonNull(delta);
        return vh.addAndGet(delta);
    }


    @Override
    public Long getAndIncrement(Long delta) {
        Objects.requireNonNull(delta);
        return vh.getAndAdd(delta);
    }

    @Override
    public Long get() {
        return vh.get();
    }

    @Override
    public void set(Long value) {
        Objects.requireNonNull(value);
        vh.set(value);
    }

    @Override
    public void reset() {
        set(initValue);
    }
}
