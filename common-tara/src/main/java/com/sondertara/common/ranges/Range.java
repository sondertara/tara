package com.sondertara.common.ranges;

import com.sondertara.common.base.EmptyEvalutible;

public abstract class Range<T> implements EmptyEvalutible {
    private T start;
    private T endInclusive;

    protected Range(T start, T endInclusive){
        setStart(start);
        setEndInclusive(endInclusive);
    }

    public T getStart() {
        return start;
    }

    public void setStart(T start) {
        this.start = start;
    }

    public T getEndInclusive() {
        return endInclusive;
    }

    public void setEndInclusive(T endInclusive) {
        this.endInclusive = endInclusive;
    }

    public abstract boolean contains(T value);

    @Override
    public boolean isNull() {
        return false;
    }
}
