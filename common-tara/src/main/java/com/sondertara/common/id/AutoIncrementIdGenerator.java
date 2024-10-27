package com.sondertara.common.id;

import java.util.concurrent.atomic.LongAdder;

/**
 * @author huangxiaohu.1ih
 */
public class AutoIncrementIdGenerator implements IdGenerator<Long> {
    private final LongAdder value;

    public AutoIncrementIdGenerator() {
        this(0);
    }

    public AutoIncrementIdGenerator(int basic) {
        value = new LongAdder();
        value.reset();
        value.add(basic);
    }


    @Override
    public Long get() {
        value.increment();
        return value.longValue();
    }
}
