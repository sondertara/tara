package com.sondertara.common.struct.counter;

import java.util.Objects;

public class SimpleIntegerCounter extends IntegerCounter {
    int value = 0;
    int initValue;
    public SimpleIntegerCounter() {
        this.value = 0;
    }

    public SimpleIntegerCounter(Integer value) {
        this.initValue=value;
        set(value);
    }

    @Override
    public Integer increment(Integer delta) {
        value = value + delta;
        return value;
    }


    @Override
    public Integer get() {
        return value;
    }

    @Override
    public Integer getAndIncrement(Integer delta) {
        Integer value = get();
        increment(delta);
        return value;
    }

    @Override
    public void set(Integer value) {
        Objects.requireNonNull(value);
        this.value = value;
    }

    @Override
    public void reset() {
        set(initValue);
    }

    @Override
    public String toString() {
        return value+"";
    }
}
