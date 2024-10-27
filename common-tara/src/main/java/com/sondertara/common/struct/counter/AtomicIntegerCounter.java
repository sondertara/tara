package com.sondertara.common.struct.counter;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class AtomicIntegerCounter extends IntegerCounter {
    private AtomicInteger vh;
    private int initValue;

    public AtomicIntegerCounter() {
        this(0);
    }

    public AtomicIntegerCounter(int init) {
        this.initValue = init;
        this.vh = new AtomicInteger(init);
    }

    @Override
    public Integer increment(Integer delta) {
        Objects.requireNonNull(delta);
        return vh.addAndGet(delta);
    }

    @Override
    public Integer getAndIncrement(Integer delta) {
        Objects.requireNonNull(delta);
        return vh.getAndAdd(delta);
    }

    @Override
    public Integer get() {
        return vh.get();
    }

    @Override
    public void set(Integer value) {
        Objects.requireNonNull(value);
        vh.set(value);
    }

    @Override
    public String toString() {
        return vh.toString();
    }

    @Override
    public void reset() {
        set(initValue);
    }
}
