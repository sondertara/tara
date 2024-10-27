package com.sondertara.common.struct.counter;

/**
 * @author huangxiaohu.1ih
 */
public abstract class IntegerCounter implements Counter<Integer> {

    @Override
    public Integer increment() {
        return increment(1);
    }

    @Override
    public Integer decrement() {
        return decrement(1);
    }

    @Override
    public Integer decrement(Integer delta) {
        return increment(-delta);
    }

    @Override
    public Integer getAndIncrement() {
        return getAndIncrement(1);
    }

    public abstract void reset();

}
