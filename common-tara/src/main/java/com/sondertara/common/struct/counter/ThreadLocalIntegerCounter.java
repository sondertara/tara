package com.sondertara.common.struct.counter;

import java.util.Objects;

public class ThreadLocalIntegerCounter extends IntegerCounter {
    private ThreadLocal<Integer> valueHolder;
    private int initValue;

    public ThreadLocalIntegerCounter() {
        this(0);
    }

    public ThreadLocalIntegerCounter(Integer value) {
        Objects.requireNonNull(value);
        this.initValue=value;
        this.valueHolder = new ThreadLocal<Integer>(){
            @Override
            protected Integer initialValue() {
                return initValue;
            }
        };
    }

    @Override
    public Integer increment(Integer delta) {
        Objects.requireNonNull(delta);
        int ret = valueHolder.get() + delta;
        this.valueHolder.set(ret);
        return ret;
    }

    @Override
    public Integer getAndIncrement(Integer delta) {
        Objects.requireNonNull(delta);
        int ret = valueHolder.get();
        this.valueHolder.set(ret + delta);
        return ret;
    }

    @Override
    public Integer get() {
        return this.valueHolder.get();
    }

    @Override
    public void set(Integer value) {
        Objects.requireNonNull(value);
        this.valueHolder.set(value);
    }

    @Override
    public void reset() {
        this.valueHolder.remove();
    }

    @Override
    public String toString() {
        return get().toString();
    }
}
