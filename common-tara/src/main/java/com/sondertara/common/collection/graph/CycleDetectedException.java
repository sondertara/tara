package com.sondertara.common.collection.graph;

import com.sondertara.common.text.StringUtils;

import java.util.List;

public class CycleDetectedException extends RuntimeException {
    private List<String> cycle;

    public CycleDetectedException(final String message, final List<String> cycle) {
        super(message);
        this.cycle = cycle;
    }

    public List<String> getCycle() {
        return cycle;
    }

    public String cycleToString() {
        return StringUtils.join("-->", cycle);
    }

    @Override
    public String getMessage() {
        return super.getMessage() + " " + cycleToString();
    }
}

