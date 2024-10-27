package com.sondertara.common.ranges;

public class IntRange extends CommonRange<Integer> {
    public IntRange() {
        this(0, 0);
    }

    public IntRange(Integer start, Integer endInclusive) {
        super(start, endInclusive);
    }

}
