package com.sondertara.common.pattern.patternset;

import com.sondertara.common.base.Nameable;

public class StringPatternEntry implements Nameable {
    private String name;

    public StringPatternEntry() {

    }

    public StringPatternEntry(String name) {
        this.name = name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

}
