package com.sondertara.common.ranges;

public class LongRange extends CommonRange<Long> {
    public LongRange() {
        this(0L, 0L);
    }

    public LongRange(Long start, Long endInclusive) {
        super(start, endInclusive);
    }
}
